/*
package com.pangu.crawler.framework.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.resource.ResourceReader;
import com.pangu.crawler.framework.utils.CharsetEnum;
import com.pangu.crawler.framework.utils.EncEnum;
import freemarker.cache.StringTemplateLoader;
import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class RequestBodyPart {

    private static final Logger logger = LoggerFactory.getLogger(RequestBodyPart.class);

    private static final StringTemplateLoader templateLoader = new StringTemplateLoader();

    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

    static {
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setTemplateLoader(templateLoader);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    private String trace;

    private String key;

    private String raw;

    private String content;

    public RequestBodyPart(@NotNull String trace, @NotNull String key, @NotNull String raw) {
        this.trace = trace;
        this.key = key;
        this.raw = raw;
        this.content = "";
    }

    public void parse() throws Exception {
        parse(Collections.emptyMap());
    }
    
    public void parse(String content) throws Exception {
    	this.content = content;
    }

    public void parse(Map<String, String> params) throws Exception {
        logger.info("[{{}] - request body parse start! params = {}", trace, params);
        this.content = raw;
        String[] lines;
        if (raw.contains(ResourceReader.LINE_SEP)) {
            lines = raw.split(ResourceReader.LINE_SEP);
        } else {
            lines = new String[]{raw};
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - request body parse, raw = {}, lines = {}", trace, raw, Arrays.toString(lines));
        }
        if (params != null && !params.isEmpty()) {
            this.content = Arrays.stream(lines).map(line -> {
                String[] lineWrap = new String[]{line};
                params.forEach((k, v) -> {
                    String actualK = "[[" + k + "]]";
                    while (lineWrap[0].contains(actualK)) {
                        //若参数为null赋值为空字符串
                        if (v != null) {
                            lineWrap[0] = lineWrap[0].replace(actualK, v);
                        } else {
                            lineWrap[0] = lineWrap[0].replace(actualK, "");
                        }
                    }
                });
                return lineWrap[0];
            }).collect(Collectors.joining("\r\n"));
        } else {
            this.content = String.join("\r\n", lines);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] - request body parsed, content = {}", trace, content);
        }
        logger.info("[{{}] - request body parse end!", trace);
    }

    public void ftlProcess(@NotNull String xmlJsonBw, @NotNull CharsetEnum charset,
                           BiFunction<String, CharsetEnum, JSONObject> prepareJson,
                           BiFunction<String, CharsetEnum, NodeModel> prepareXml) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("[{{}] - request body ftl process start! bw = {}, charset = {}", trace, xmlJsonBw, charset);
        } else {
            logger.info("[{{}] - request body ftl process start! charset = {}", trace, charset);
        }
        if (xmlJsonBw.trim().isEmpty()) {
            logger.info("[{{}] - request body ftl process end : bw is empty!", trace);
            return;
        }
        Object model;
        if (xmlJsonBw.startsWith("<")) {
            logger.info("[{{}] - request body ftl process as xml bw!", trace);
            NodeModel xmlModel = NodeModel.parse(new InputSource(new ByteArrayInputStream(
                    xmlJsonBw.getBytes(charset.getCharset()))));
            if (prepareXml != null) {
                xmlModel = prepareXml.apply(xmlJsonBw, charset);
                if (xmlModel == null) {
                    logger.info("[{{}] - request body ftl process end : bw prepare as xml failed!", trace);
                    return;
                }
            }
            model = xmlModel;
        } else if (xmlJsonBw.startsWith("{")) {
            logger.info("[{{}] - request body ftl process as json bw!", trace);
            JSONObject jsonModel = JSON.parseObject(xmlJsonBw);
            if (prepareJson != null) {
                jsonModel = prepareJson.apply(xmlJsonBw, charset);
                if (jsonModel == null) {
                    logger.info("[{{}] - request body ftl process end : bw prepare as json failed!", trace);
                    return;
                }
            }
            model = jsonModel;
        } else {
            throw new Exception("bw type error : " + xmlJsonBw);
        }
        logger.info("[{{}] - request body ftl get template start!", trace);
        templateLoader.putTemplate(this.key, this.content);
        Template template = configuration.getTemplate(this.key);
        logger.info("[{{}] - request body ftl get template end!", trace);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(byteArrayOutputStream)) {
            logger.info("[{{}] - request body ftl template process start!", trace);
            template.process(model, writer);
            logger.info("[{{}] - request body ftl template process end!", trace);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        this.content = new String(bytes, charset.getCharset());
        if (logger.isDebugEnabled()) {
            logger.debug("[{{}] - request body ftl process end! content = {}", trace, content);
        } else {
            logger.info("[{{}] - request body ftl process end!", trace);
        }
    }

    public void prefix(String prefix) {
        logger.info("[{{}] - request body add prefix start! prefix = {}", trace, prefix);
        if (this.content == null) {
            this.content = prefix;
        } else {
            this.content = prefix + this.content;
        }
        logger.info("[{{}] - request body add prefix end!", trace);
    }

    public void suffix(String suffix) {
        logger.info("[{{}] - request body add suffix start! suffix = {}", trace, suffix);
        if (this.content == null) {
            this.content = suffix;
        } else {
            this.content += suffix;
        }
        logger.info("[{{}] - request body add suffix end!", trace);
    }

    public void encode(@NotNull EncEnum enc, @NotNull CharsetEnum charset) throws Exception {
        logger.info("[{{}] - request body encode start! enc = {}, charset = {}", trace, enc, charset);
        if (enc == EncEnum.NONE) {
            logger.info("[{}] - request body do not need encode!", trace);
        } else if (enc == EncEnum.URL) {
            logger.info("[{}] - request body need url encode!", trace);
            this.content = URLEncoder.encode(this.content, charset.getCharset().name());
        } else if (enc == EncEnum.BASE64) {
            logger.info("[{}] - request body need base64 encode!", trace);
            this.content = Base64.getEncoder().encodeToString(this.content.getBytes(charset.getCharset().name()));
        } else {
            throw new Exception("enc error : " + enc);
        }
        logger.info("[{{}] - request body encode end!", trace);
    }

    public String getTrace() {
        return trace;
    }

    public String getKey() {
        return key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RequestBodyPart{" +
                "trace='" + trace + '\'' +
                ", key='" + key + '\'' +
                ", raw='" + raw + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
*/

/*
package jiaojiaowork;

*/
/**
 * @Author sheng.ding
 * @Date 2020/10/24 14:07
 * @Version 1.0
 **//*


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FreeMakerReplaceWordContent {

    private static  Log logger = LogFactory.getLog(FreeMakerReplaceWordContent.class );
    private static final String DEFAULT_TEMPLATE_KEY = "default_template_key";
    private static final String DEFAULT_TEMPLATE_EXPRESSION = "default_template_expression";
    private static final Configuration CONFIGURER = new Configuration();
    static {
        CONFIGURER.setClassicCompatible(true);
    }
    */
/**
     * 配置SQL表达式缓存
     *//*

    private static Map<String, Template> templateCache = new HashMap<String, Template>();
    */
/**
     * 分库表达式缓存
     *//*

    private static Map<String, Template> expressionCache = new HashMap<String, Template>();

    public static String process(String expression, Map<String, Object> root)   {
        StringReader reader = null;
        StringWriter out = null;
        Template template = null;
        try {
            if (expressionCache.get(expression) != null) {
                template = expressionCache.get(expression);
            }
            if (template == null) {
                template = createTemplate(DEFAULT_TEMPLATE_EXPRESSION, new StringReader(expression));
                expressionCache.put(expression, template);
            }
            out = new StringWriter();
            template.process(root, out);
            return out.toString();
        } catch (Exception e) {
            logger.error("freemark解析sql 异常", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static Template createTemplate(String templateKey, Reader reader) throws IOException {
        Template template = new Template(DEFAULT_TEMPLATE_KEY, reader, CONFIGURER);
        template.setNumberFormat("#");
        return template;
    }

    public static String process(Map<String, Object> root, String sql, String sqlId) {
        StringReader reader = null;
        StringWriter out = null;
        Template template = null;
        try {
            if (templateCache.get(sqlId) != null) {
                template = templateCache.get(sqlId);
            }
            if (template == null) {
                reader = new StringReader(sql);
                template = createTemplate(DEFAULT_TEMPLATE_KEY, reader);
                templateCache.put(sqlId, template);
            }
            out = new StringWriter();
            template.process(root, out);
            return out.toString();
        } catch (Exception e) {
            logger.error("freemark解析sql 异常", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }


    */
/**
     *  模板文件解析
     *  @param paramMap 参数
     *  @param resultFileWriter  结果文件写入器
     *  @param templateFileReader 模板文件读取器
     *  @author liu ao
     *  @created 2018年4月3日 下午4:41:47
     *//*

    public static void process(Map<String, Object> paramMap, Writer resultFileWriter,
                               Reader templateFileReader ) {
        CONFIGURER.setDefaultEncoding("UTF-8");// 设置默认编码方式
        try {
            Template template = createTemplate(DEFAULT_TEMPLATE_EXPRESSION, templateFileReader);
            template.process(paramMap, resultFileWriter);
            logger.info(".............freemark文件解析完成..........");
        } catch (Exception e) {
            logger.error("freemark 解析异常", e);
            e.printStackTrace();
        }
    }

    */
/**
     * 模板写好，使用word打开保存为word2003的xml格式，在修改后缀名为ftl
     * 使用这个程序替换内容
     * @param args
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     *//*

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        String strx = "C:\\Users\\Lenovo\\Desktop\\万达\\询价函及报价函标准文件（2016版）(模板）.ftl";
        File inputFile = new File(strx);
        String targetFiletaPath = "D:\\tempalte.doc";
        File targetFile = new File(targetFiletaPath);
        Writer resultFileWriter = new OutputStreamWriter(new FileOutputStream(targetFile), "utf-8");
        Reader templateFileReader = new InputStreamReader(new FileInputStream(inputFile),"utf-8");
        Map param = new HashMap();
        param.put("name", "我是丁升");
        param.put("time1", "我昨天干到亮点");
        param.put("time2", "我今天早上迟到了");
        FreeMakerReplaceWordContent.process(param, resultFileWriter, templateFileReader);
    }
}*/

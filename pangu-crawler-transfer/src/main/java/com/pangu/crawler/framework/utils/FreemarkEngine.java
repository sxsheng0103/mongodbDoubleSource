/*
package com.pangu.crawler.framework.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

*/
/**
 * FreemarkEngine解析引擎。
 * @author HANT
 *//*

@Component
public class FreemarkEngine {

	@Autowired
	private Configuration configuration;


	*/
/**
	 * 把指定的模板生成对应的字符串。
	 * @param templateName  模板名，模板的基础路径为：WEB-INF/template目录。
	 * @param model  传入数据对象。
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 *//*

	public String mergeTemplateIntoString(String templateName,Object model) throws IOException, TemplateException {
		Template template=configuration.getTemplate(templateName);
		return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
	}

	
	
	
	*/
/**
	 * 根据字符串模版解析出内容
	 * @param obj 需要解析的对象。
	 * @param templateSource	字符串模版。
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 *//*

	public  String parseByStringTemplate(Object obj,String templateSource) throws TemplateException, IOException
	{
		Configuration cfg = new Configuration();
		StringTemplateLoader loader = new StringTemplateLoader();
		cfg.setTemplateLoader(loader);
		cfg.setClassicCompatible(true);
		loader.putTemplate("freemaker", templateSource);
                Template template = cfg.getTemplate("freemaker");
                StringWriter writer = new StringWriter();   
                template.process(obj, writer); 
		return writer.toString();
		
	}
	
}
*/

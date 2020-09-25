package com.pangu.crawler.transfer.service;

import com.alibaba.fastjson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryBusinessTransferRule;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.com.log.TLog;
import com.pangu.crawler.transfer.service.iservice.ITransferJsonDataService;
import com.pangu.crawler.transfer.utils.RuleDataFilterUtils;
import com.pangu.crawler.transfer.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransferJsonDataServiceImpl implements ITransferJsonDataService{
	private Logger LOGGER = null;

	private Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
	@Autowired
	TransferRuleDataService transferRuleDataService;
	@Autowired
	CrawleredDataService crawleredDataServiceService;
	@Autowired
	AsyncQueryBusinessTransferRule asyncQueryBusinessTransferRule;
	@Autowired
	TaxCodeReleationService taxCodeReleationService;

	/**
	 * @param dataMap
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formcode
	 * @param id
	 * @return
	 * @description 转化原始类型为josn格式的报文为标准报文
	 */
	public Map<String, Object> majarTransferJsonData(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> tempresult = null;
		JSONObject resultData = new JSONObject();
		StringBuilder error = new StringBuilder();
		try {
			boolean breakoneflag = false;
			for (Map.Entry<String, String> map : dataMap.entrySet()) {
				if (map.getKey().equals("zb")) {
					breakoneflag = true;
				}
				String form = map.getKey();
				if (map.getValue() == null) {
					TLog.registerLog(LOGGER,this.getClass()).warn(TimeUtils.getCurrentDateTime(new Date(), TimeUtils.sdf4) + nsrdq + "-" + ruleszcode + "-" + formcode + "表单内容不存在!");
					tempresult = new HashMap<String, String>();
					tempresult.put("code","fail:" + form );
					tempresult.put("message", form + ":数据源表单内容null");
					result.put(form, tempresult);
					continue;
				}
				String formid = "";
				JSONObject jsonsource = JSON.parseObject(map.getValue());
				AsyncBusinessTransferRuleEntity ruleEntity = null;
				for (AsyncBusinessTransferRuleEntity ruleEntity1 : transferRuleEntity) {
					formid = ruleEntity1.getFormid();
					if (!formid.equals(form) && !form.equals("zb")) {
						continue;
					} else {
						ruleEntity = ruleEntity1;
					}
					if (breakoneflag == true) {//zb代表只有一个数据块，该税种下每一个规则都在这里校验，完成后直接返回
						if (ruleEntity == null) {
							tempresult = new HashMap<String, String>();
							tempresult.put("code", "warn:" + formid);
							tempresult.put("message", "没有找到对应的表单规则文件:" + formid);
							result.put(formid, tempresult);
						} else {
							error.setLength(0);
							tempresult = combineJSONResultData(nsrdq, ruleszcode, formid, form, error, result, resultData, ruleEntity, jsonsource);
							result.put(formid, tempresult);
						}
					}
				}
				if (breakoneflag == true) {
					Set<String> resultkeys = result.keySet();
					Map<String, String> temp1 = null;
					boolean warn = false;
					boolean errorf = false;
					for (String key : resultkeys) {
						if ((result.get(key) instanceof Map)) {
							temp1 = (Map<String, String>) result.get(key);
							if (temp1.get("code") != null && temp1.get("code").contains("fail")) {
								result.put("finalresult", "转化失败");
								result.put("code", "fail");
								result.put("data", resultData);
								return result;
							} else if (temp1.get("code") != null && temp1.get("code").contains("warn")) {
								warn = true;
							} else if (temp1.get("code") != null && temp1.get("code").contains("error")) {
								errorf = true;
							}
						}
					}
					if (errorf == true) {
						result.put("finalresult", "转化错误!");
						result.put("code", "error");
						result.put("data", resultData);
						return result;
					} else if (warn == true) {
						result.put("finalresult", "转化完成,但是有警告信息!");
						result.put("code", "warn");
						result.put("data", resultData);
						return result;
					} else {
						result.put("finalresult", "转化成功");
						result.put("code", "succsess");
						result.put("data", resultData);
						return result;
					}
				}
				if(breakoneflag != true){
					if (ruleEntity == null) {
						TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);
						result.put("code", "warn");
						result.put("message", "没有找到对应的表单规则文件:" + form);
					} else {
						tempresult = combineJSONResultData(nsrdq, ruleszcode, formid, form, error, result, resultData, ruleEntity, jsonsource);
						result.put(form, tempresult);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			tempresult.put("code", "fail");
			tempresult.put("message", "Json数据转化异常" + e.getMessage());
			result.put("fianlresult", tempresult);
		} catch (Exception e) {
			e.printStackTrace();
			tempresult.put("code", "fail");
			tempresult.put("message", "数据转化异常" + e.getMessage());
			result.put("fianlresult", tempresult);
		} finally {
			Set<String> resultkeys = result.keySet();
			Map<String, String> temp1 = null;
			boolean warn = false;
			boolean errorf = false;
			for (String key : resultkeys) {
				if ((result.get(key) instanceof Map)) {
					temp1 = (Map<String, String>) result.get(key);
					if (temp1.get("code") != null && temp1.get("code").contains("fail")) {
						result.put("finalresult", "转化失败");
						result.put("code", "fail");
						result.put("data", resultData);
						return result;
					} else if (temp1.get("code") != null && temp1.get("code").contains("warn")) {
						warn = true;
					} else if (temp1.get("code") != null && temp1.get("code").contains("error")) {
						errorf = true;
					}
				}
			}
			if (result.get("code") != null && result.get("code").equals("fail")) {
				return result;
			}
			if (errorf == true) {
				result.put("finalresult", "转化错误!");
				result.put("code", "warn");
				result.put("data", resultData);
				return result;
			} else if (warn == true) {
				result.put("finalresult", "转化完成,但是有警告信息!");
				result.put("code", "warn");
				result.put("data", resultData);
				return result;
			} else {
				result.put("finalresult", "转化成功");
				result.put("code", "succsess");
				result.put("data", resultData);
			}
			return result;
		}
	}


	/**
	 * @direction  一组数据通过指定规则生成标准报文
	 * 					规则参考山东增值税附表1
	 * 				       xxx[0].ewbhxh=@@1  、xxx[0].hmc=@@13%税率的货物及加工修理修配劳务  以@@开始的字段是 以【0】为下标的元素并且值以@@开始的行为这一组元素的识别行
	 * 				       xxx[0].exterkey=@@#[?(@.ewbhxh=1)]                               以@@#开始的字段是 以【0】为下表的元素的结束符号
	 * 				    其他的行   xxx[0].kchXxynse=xxx.bqxsqkmxbGridlbVO[?(@.hmc='13%税率的货物及加工修理修配劳务')].kchXxynse 为通过指定名称查找对应行的字段【如：kchXxynse】
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formid
	 * @param form
	 * @param error
	 * @param result
	 * @param resultData
	 * @param ruleEntity
	 * @param jsonsource
	 * @description 原始类型为json格式的报文转化数据组合逻辑
	 */
	public Map<String, String> combineJSONResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder error, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, JSONObject jsonsource) {
		Map<String, String> tempresult = new HashMap<String, String>();
		if (ruleEntity == null) {
			TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + formid);
			tempresult.put("code", "warn:" + formid);
			tempresult.put("message", formid + ":没有找到对应的表单规则文件");
			return tempresult;
		}

		List<String> rules = new ArrayList<String>();
		rules.addAll(Arrays.asList(ruleEntity.getContent().split("\\n")));
		error.setLength(0);
		Map<String,Object> ruleresult = RuleDataFilterUtils.splitJsonGroupByTableRules(rules);
		Queue rulegroupQueue =  (Queue)ruleresult.get("data");
		String temprule = "";
		String dataprefix = "";
		if(rulegroupQueue==null){
			TLog.registerLog(LOGGER,this.getClass()).error(nsrdq+"-"+ruleszcode+"-"+formid+":json解析类型错误请检查解析类型");
			tempresult.put("code", "error:" + formid);
			tempresult.put("message", formid + "解析类型错误请检查解析类型!");
			return tempresult;
		}
		while(!rulegroupQueue.isEmpty()){
			Map<String,Object> rulegroup = 	(Map<String,Object>)rulegroupQueue.poll();
			String type = rulegroup.get("type").toString();
				if(type.equals("nomallogical")||type.equals("prefixlogical")){//普通查找走这个
					transferNomalJsonRule( ruleszcode, jsonsource, temprule, (List<String>)rulegroup.get("data"), dataprefix, nsrdq, error,resultData);
				}else if(type.equals("dynamicaddrowlogical")){
					transferdynamicaddrowJsonRule( ruleszcode, jsonsource, (Map)rulegroup.get("data"), nsrdq, error,resultData);
				}else{
					TLog.registerLog(LOGGER,this.getClass()).error("解析类型错误，没有这种类型的处理功能");
				}
		}

		if (error.toString().equals("")) {
//			resultData.put("",docObj);
//			resultData.putAll((JSONObject)docObj);
			tempresult.put("code", "success:" + formid);
			tempresult.put("message", formid + ":" + "转化成功");
		} else {
			tempresult.put("code", "error:" + formid);
			tempresult.put("message", formid + error.toString());
		}
		return tempresult;
	}




	public void transferNomalJsonRule(String ruleszcode,JSONObject jsonsource,String temprule,List<String> rulelist,String dataprefix,String nsrdq,StringBuilder error,JSONObject resultData){
			/**
			 * @variable matchextenal 记录需要查找有多条数据的精准变量值
			 */
			String matchextenal = null;
			for (String rule : rulelist) {
				if (StringUtils.isNotEmpty(rule)) {
					if (rule.indexOf("//") != -1) {
						temprule = rule.substring(0, rule.indexOf("//")).replace("\\s", "");
					} else {
						temprule = rule;
					}
					Matcher m = pattern.matcher(temprule == null ? "" : temprule);
					temprule = m.replaceAll("");
					if (jsonsource != null && StringUtils.isNotEmpty(temprule)) {
						try {
							if ((dataprefix + "." + temprule.substring(rule.indexOf("=") + 1)).replace("\\s*", "").equals("")) {
								TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + rule + ":节点JSON选择器为空");
								error.append(dataprefix + "-> " + rule + ":节点JSON选择器为空");
								continue;
							}

							String jvpath = (StringUtils.isNotEmpty(dataprefix) ? "" : (dataprefix + ".")) + temprule.substring(rule.indexOf("=") + 1).replace("\\s*", "");
							if (jvpath == null || jvpath.equals("")) {
								TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + rule + ":[error]查找数据为空,请检查数据是否可以忽略");
								error.append(rule + ":[error]查找数据为空,请检查数据是否可以忽略;");
								continue;
							}
							if (temprule.substring(0, rule.indexOf("=")).endsWith("exterkey") && jvpath.startsWith("@@#") && !jvpath.equals("@@#")) {
								matchextenal = jvpath.substring(jvpath.indexOf("@@#") + 3);
								continue;
							} else if (temprule.substring(0, rule.indexOf("=")).endsWith("exterkey") && jvpath.equals("@@#")) {
								matchextenal = null;
							}
							if (jvpath.startsWith("@@")) {
								JSONPath.set(resultData, temprule.substring(0, rule.indexOf("=")), jvpath.substring(jvpath.indexOf("@@") + 2));
								continue;
							}

							Object o = JSONPath.eval(jsonsource, jvpath);
							if (o instanceof Collection) {
								if (((Collection) o).size() == 1) {
									o = ((JSONArray) o).get(0);
								} else if (((Collection) o).size() > 1) {
									//判断重复的表
									if (matchextenal != null) {
										jvpath = jvpath.substring(0, jvpath.lastIndexOf("]") + 1) + matchextenal + jvpath.substring(jvpath.lastIndexOf("]") + 1);
										o = JSONPath.eval(jsonsource, jvpath);
										if (o instanceof Collection) {
											if (((Collection) o).size() == 1) {
												o = ((JSONArray) o).get(0);
											} else if (((Collection) o).size() > 1) {
												TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点重复");
												error.append(dataprefix + "-> " + ":" + rule + ":节点重复");
												continue;
											} else {
												TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点不存在");
												error.append(dataprefix + "-> " + ":" + rule + ":节点不存在");
												continue;
											}
										}
									} else {
										TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点重复");
										error.append(dataprefix + "-> " + ":" + rule + ":节点重复");
										continue;
									}
								} else {
									TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点不存在");
									error.append(dataprefix + "-> " + ":" + rule + ":节点不存在");
									continue;
								}
							}
							boolean existsMultirow = false;
							try {
								if (((JSONArray) o).size() >= 0) {
									existsMultirow = true;
								} else {
									existsMultirow = false;
								}
							} catch (Exception e) {
								existsMultirow = false;
							}
							if (!existsMultirow && o == null) {
								TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点不存在");
								error.append(dataprefix + "-> " + ":" + rule + ":节点不存在\n");
							} else {
								JSONPath.set(resultData, temprule.substring(0, rule.indexOf("=")), o == null ? "" : o);
							}
						} catch (Exception e) {
//							e.printStackTrace();
							TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + rule + ":[error]查找数据异常,请检查数据规范填写");
							error.append(rule + ":[error]查找数据异常,请检查数据规范填写\n");

						}
					} else {
						TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode + rule + ":配置信息错误dataprefix:" + dataprefix + ",temprule:" + temprule);
						error.append(rule + ":配置信息错误\n");
					}
				}
			}
//			resultData.put("",docObj);
//			resultData.putAll((JSONObject)docObj);
	}

	/*处理动态添加的行，例如山东增值税  表单hznsqyzzsfpb  山东企业会计制度现金流量表（没有ewbhxh，只能全部获取） */
	public void transferdynamicaddrowJsonRule(String ruleszcode,JSONObject jsonsource,Map rulegroup,String nsrdq,StringBuilder error,JSONObject resultData){

		String reportpath = (String)rulegroup.get("reportpath");
		String jsonpath = (String)rulegroup.get("jsonpath");
		Set<String>	propertyreleations =  (Set<String>)rulegroup.get("propertyreleations");
		Object o = null;
		try{
			o = JSONPath.eval(jsonsource, jsonpath);
		}catch (Exception e){
			TLog.registerLog(LOGGER,this.getClass()).info("动态添加行没有找到数据");
		}
		if(o==null){
			JSONPath.set(resultData, reportpath,new JSONArray());
		}else{
			Object val = null;
			int i = 0;
			for(Object e: ((JSONArray) o)){
				JSONObject element = (JSONObject)e;

				for(String property : propertyreleations){
					if(property==null){
						TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode  + "-属性["+property+"]:[error]属性关系内容为空");
						error.append("-属性["+property+"]:[error]属性["+property+"]:[error]属性关系内容为空\n");
						continue;
					}

					if(property.split("@@@").length!=2){
						TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode  + "-属性["+property+"]:[error]属性格式错误,原始报文和标准报文字段名称中间用三个@隔开");
						error.append("-属性["+property+"]:[error]属性格式错误,原始报文和标准报文字段名称中间用三个@隔开\n");
						continue;
					}
					if(property.split("@@@")[1].startsWith("generateIncreasedKey")){
						JSONPath.set(resultData, reportpath+"["+i+"]."+property.split("@@@")[0], (i+1));
						continue;
					}
					try{
						val = JSONPath.eval(element, property.split("@@@")[0]);
					}catch(Exception e1){
						TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode  + "-属性["+property+"]:[error]不存在或查找数据异常");
						error.append("-属性["+property+"]:[error]不存在或查找数据异常\n");
					}
					try{
						JSONPath.set(resultData, reportpath+"["+i+"]."+property.split("@@@")[1], val);
					}catch(Exception e1){
						TLog.registerLog(LOGGER,this.getClass()).warn(nsrdq + "-" + ruleszcode  + "-属性["+property+"]:[error]数据生成失败,请检查数据格式");
						error.append("-属性["+property+"]:[error]数据生成失败,请检查数据格式");
					}
				}
				i++;
			}
		}
	}
}
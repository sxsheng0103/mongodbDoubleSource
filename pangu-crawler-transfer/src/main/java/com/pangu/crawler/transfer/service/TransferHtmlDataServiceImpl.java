package com.pangu.crawler.transfer.service;

import com.alibaba.fastjson.*;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryBusinessTransferRule;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.enums.SzEnum;
import com.pangu.crawler.transfer.service.iservice.ITransferHtmlDataService;
import com.pangu.crawler.transfer.utils.RuleDataFilterUtils;
import com.pangu.crawler.transfer.utils.TimeUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TransferHtmlDataServiceImpl implements ITransferHtmlDataService {


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
	 * @description 转化原始类型为html格式的报文为标准报文
     *              所有的html原始文件通过这个方法进行转换，通过转换规则‘/文件转换成标准报文文件
	 */
	public Map<String, Object> majarTransferHtmlData(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> tempresult  = new HashMap<String, String>();
		JSONObject resultData = new JSONObject();
		StringBuilder error = new StringBuilder();
		Paging<AsyncAreaTaxCodeReleationEntity> taxCodeReleationEntity = null;
		try {
			/*当数据存储一个数据块，所有表单html结构数据存储在一个字段里边时使用这个标识。这时遍历处理每个表单规则转换文件，转化成标准报文*/
			boolean breakoneflag = false;
			for (Map.Entry<String, String> map : dataMap.entrySet()) {
//				tempresult.clear();
				if (map.getKey().equals("zb")) {
					breakoneflag = true;
				}
				String form = map.getKey();
				/************** 一、这里是增值税里边嵌套附件税的情况 当数据存储的key键值是【zzsybnsr&fjs】时，这个增值税一般纳税人税种里边存储了附加税的html原始报文html数据块 *******************************************/
				if (map.getKey().equals("zzsybnsr&fjs")) {
					Map<String, String> zzsfjsMap = new HashMap<String, String>();
					zzsfjsMap.put(SzEnum.fjs.getCode(), dataMap.get(map.getKey()));
					Map<String, String> paramsfjs = new HashMap<String, String>(4);
					paramsfjs.put("type", "html");
					paramsfjs.put("nsrdq", nsrdq);
					paramsfjs.put("rulesz", SzEnum.fjs.getCode());
					List<AsyncBusinessTransferRuleEntity> transferRuleEntityfjs = transferRuleDataService.queryHistoricalData(paramsfjs, null, null).getData();
					/* zzsfjsMap 这个HashMap里边放了一个原始报文节点，里边存附件税html，
					* 调用 transferMixHtml 方法处理附加税转换成附加税的标准报文*/
					Map<String, Object> resultfjs = transferMixHtml(zzsfjsMap, nsrdq, SzEnum.fjs.getCode(), formcode, id, transferRuleEntityfjs,resultData);
					tempresult = new HashMap<String, String>();
					tempresult.put("code", resultfjs.get("code") + ":" +form );
					String finalresult = (resultfjs.get("finalresult") == null ? "" : resultfjs.get("finalresult").toString());
					resultfjs.remove("finalresult");
					resultfjs.remove("data");
					tempresult.put("message", form + ":增值税里边含附加税处理情况," + finalresult + "详细信息:" + JSON.toJSONString(resultfjs));

					result.put(form, tempresult);
					continue;
					//保存附加税信息
				}
				if (map.getValue() == null) {
					tempresult = new HashMap<String, String>();
					tempresult.put("code", "fail:"+ form );
					tempresult.put("message", form + ":数据源表单内容null");
					result.put(form, tempresult);
					log.warn(TimeUtils.getCurrentDateTime(new Date(), TimeUtils.sdf4) + nsrdq + "-" + ruleszcode + "-" + formcode + "表单内容不存在!");
					continue;
				}

				String formid = "";
				Document doc = null;
				try {
					doc = Jsoup.parse(map.getValue());
				} catch (Exception eq) {
					log.warn(nsrdq + "-" + ruleszcode + "转化document失败,需要检查原始数据格式问题:" + eq.getMessage());
					tempresult = new HashMap<String, String>();
					tempresult.put("code", "error:" + form);
					tempresult.put("message", "转化document失败,需要检查原始数据格式问题:" + eq.getMessage() + "-" + form);
					result.put(form, tempresult);
					continue;
				}

				AsyncBusinessTransferRuleEntity ruleEntity = null;
				for (AsyncBusinessTransferRuleEntity ruleEntity1 : transferRuleEntity) {
					formid = ruleEntity1.getFormid();
					/********************************************** 二、(1)zb代表只有一个数据块，该税种下每一个规则都在这里校验，完成后直接返回.
					 * zb类型的原始报文结构包含整个税种的所有表单，下边为处理的方式*****************************/
					if (breakoneflag == true) {
						if (ruleEntity == null) {
							log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);
							tempresult = new HashMap<String, String>();
							tempresult.put("code", "warn:" + form);
							tempresult.put("message", "没有找到对应的表单规则文件:" + form);
							result.put(form, tempresult);
							continue;
						} else {
							tempresult = combineHtmlResultData(nsrdq, ruleszcode, formid, form, error, result, resultData, ruleEntity, doc);
							result.put(form, tempresult);
						}
					}else{
						if (!formid.equals(form)) {
							continue;
						} else {
							ruleEntity = ruleEntity1;
						}
					}

				}
				/********************************************** 二、(2)zb类型时,在这里获取返回转换结果*****************************/
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
						result.put("finalresult", "转化成功,但是有警告信息!");
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
				/********************************************** 三、处zb、zzsybnsr&fjs类型的原始报文结构使用下边通用的格式下边为处理的方式**********/
				if (ruleEntity == null) {
					log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);
					tempresult = new HashMap<String, String>();
					tempresult.put("code", "warn:" + form);
					tempresult.put("message", "没有找到对应的表单规则文件:" + form);
					result.put(form, tempresult);
				} else {
					tempresult = new HashMap<String, String>();

					Map<String, Object> ruleresult = RuleDataFilterUtils.dynamicSplitGroupHtmlRules(ruleEntity.getContent());
					Queue rulegroupQueue =  (Queue)ruleresult.get("data");
					String errorgroup = ruleresult.get("error").toString();
					if(!errorgroup.equals("")){
						log.warn(nsrdq + "-" + ruleszcode + "规则配置文件不全请参照说明编写规则文件:" + form);//+formid+"<->"
						tempresult.put("code", "error:" + form);
						tempresult.put("message", form + ":"+errorgroup);
						result.put(form, tempresult);
					}
					while(!rulegroupQueue.isEmpty()){
						Map<String,Object> rulegroup = 	(Map<String,Object>)rulegroupQueue.poll();
						String type = rulegroup.get("type").toString();
						try{

							if(type.equals("nomallogical")){//普通查找走这个
								tempresult = transferNomalRule((List<String>)rulegroup.get("data"),error,doc,nsrdq,ruleszcode,resultData);
							}else if(type.equals("booleanlogical")){//布尔查找走这个■□
								tempresult = transferBooleanTypeRule((List<String>)rulegroup.get("data"),error,doc,nsrdq,ruleszcode,resultData);
							}else if(type.equals("dynamicmatchrowlogical")){//动态匹配选择器走这个  上海增值税减免税表
								Map<String,String> ruledata = (Map<String,String>)rulegroup.get("data");
								tempresult = combineDynamicRowsHtmlResultData(nsrdq, ruleszcode, formid, form, error, result, resultData, ruleEntity, doc,ruledata);
							}else if(type.equals("fixmatchrowlogical")){//模糊查找行走这个  如云南企业会计制度现金流量表
								Map<String,String> paramsData = new HashMap<String,String>(1);
								paramsData.put(map.getKey(),map.getValue());
								final AsyncBusinessTransferRuleEntity ruleEntityTemp = ruleEntity;
								Map<String,Object> resulttemp = new HashMap<String,Object>();
								resulttemp =  transferMixHtml(paramsData, nsrdq, ruleszcode, formcode, id, new ArrayList<AsyncBusinessTransferRuleEntity>(){{add(ruleEntityTemp);}},resultData);
								resulttemp.remove("data");resulttemp.remove("code");
								resulttemp.remove("finalresult");
								for(Map.Entry e:resulttemp.entrySet()){
									tempresult.put(e.getKey().toString(),e.getValue()==null?"":e.getValue().toString());
								}
							}else{
								log.error("类型错误，没有这种类型的处理功能");
							}
						}catch (Exception e){
							error.append("异常出错");
						}
					}

					if (error.toString().equals("")) {
						tempresult.put("code", "success:" + form);
						tempresult.put("message", form + ":" + "转化成功");
						result.put(form, tempresult);
					} else {
						tempresult.put("code", "error:" + form);
						tempresult.put("message", form + error.toString());
						result.put(form, tempresult);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			tempresult = new HashMap<String, String>();
			tempresult.put("code", "fail");
			tempresult.put("message", "Json数据转化异常" + e.getMessage());
			result.put("fianlresult", tempresult);
		} catch (Exception e) {
			e.printStackTrace();
			tempresult = new HashMap<String, String>();
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
				result.put("finalresult", "转化成功,但是有警告信息!");
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




	public Map<String,String> transferBooleanTypeRule(List<String> ruledata,StringBuilder error,Document doc,String nsrdq,String ruleszcode,JSONObject resultData){
		Map<String, String> result = new HashMap<String, String>();
		String errorrule = "";
		try{
			for(String rule:ruledata){
				try{
					if(rule.substring(rule.indexOf("=") + 1).trim().equals("")){
						error.append(rule + ":html中没有指定html路径");
						log.warn(nsrdq + "-" + ruleszcode + rule + ":" + ":html中没有指定html路径");
						continue;
					}
					Elements links = doc.select(rule.substring(rule.indexOf("=") + 1)); //带有href属性的a元素
					if (links.size() == 0) {
						error.append(rule + ":html中没有找到节点;");
						log.warn(nsrdq + "-" + ruleszcode + rule + ":" + ":节点不存在");
					} else if (links.size() != 1) {
						error.append(rule + ":html中有重复节点;");
						log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":html中有重复节点");
					} else {
						if(links.get(0).wholeText().trim().contains("■ 是") ){
							JSONPath.set(resultData, rule.substring(0, rule.indexOf("=")), "Y");
						}else{
							JSONPath.set(resultData, rule.substring(0, rule.indexOf("=")), "N");
						}
					}
				}catch (Exception e){
					log.error("规则：["+errorrule+"]转换失败");
					result.put("code","error");
					error.append(rule + "规则：["+errorrule+"]转换失败");
				}

			}
			if(result.get("code")==null){
				result.put("code","warn");
			}
			result.put("message",error.toString());
		}catch (Exception e){
			e.printStackTrace();
			log.error("处理错误"+e.getMessage());
		}finally {
			return result;
		}
	}
	/**
	 * @param dataMap
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formcode
	 * @param id
	 * @return
	 * @description 方案二：当附加税在增值税里边存储在一块时（例如上海增值税+附加税）转化原始数据类型为html格式的原始报文为标准报文，
     * 将附加税当成一个整体数据块来处理
	 */
	public Map<String, Object> transferMixHtml(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity,JSONObject resultData) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> tempresult = new HashMap<String, String>();
		JSONObject resultData1 = new JSONObject();
		StringBuilder error = new StringBuilder();
		try {
			for (Map.Entry<String, String> map : dataMap.entrySet()) {
				String form = map.getKey();
				if (map.getValue() == null) {
					tempresult = new HashMap<String, String>();
					tempresult.put("code","fail:"+ form  );
					tempresult.put("message", form + ":数据源表单内容null");
					result.put(form, tempresult);
					log.warn(TimeUtils.getCurrentDateTime(new Date(), TimeUtils.sdf4) + nsrdq + "-" + ruleszcode + "-" + formcode + "表单内容不存在!");
					continue;
				}
				String formid = "";
				Document doc = null;
				try {
					doc = Jsoup.parse(map.getValue().toString());
				} catch (Exception eq) {
					log.warn(nsrdq + "-" + ruleszcode + "转化document失败,需要检查原始数据格式问题:" + eq.getMessage());
					tempresult = new HashMap<String, String>();
					tempresult.put("code", "error:" + form);
					tempresult.put("message", "转化document失败,需要检查原始数据格式问题:" + eq.getMessage() + "-" + form);
					result.put(form, tempresult);
				}
				for (AsyncBusinessTransferRuleEntity ruleEntity1 : transferRuleEntity) {
					form = ruleEntity1.getFormid();
					tempresult = new HashMap<String, String>();
					tempresult = combineMatchColsHtmlResultData(nsrdq, ruleszcode, form, form, error, result, resultData, ruleEntity1, doc);
					result.put(form, tempresult);
				}
			}
            result.put("data", resultData);
		} catch (JSONException e) {
			e.printStackTrace();
			tempresult = new HashMap<String, String>();
			tempresult.put("code", "fail");
			tempresult.put("message", "Json数据转化异常" + e.getMessage());
			result.put("fianlresult", tempresult);
		} catch (Exception e) {
			e.printStackTrace();
			tempresult = new HashMap<String, String>();
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
					if (temp1.get("code")!=null&&temp1.get("code").contains("fail")) {
						result.put("finalresult", "转化失败");
						result.put("code", "fail");
						result.put("data", resultData);
						return result;
					} else if (temp1.get("code")!=null&&temp1.get("code").contains("warn")) {
						warn = true;
					} else if (temp1.get("code")!=null&&temp1.get("code").contains("error")) {
						errorf = true;
						break;
					}
				}
			}
			if (result.get("code") != null && result.get("code").equals("fail")) {
				return result;
			}
			if (errorf == true) {
				result.put("finalresult", "转化错误!");
				result.put("code", "error");
				result.put("data", resultData);
				return result;
			} else if (warn == true) {
				result.put("finalresult", "转化成功,但是有警告信息!");
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

	public Map<String,String> transferNomalRule(List<String> ruledata,StringBuilder error,Document doc,String nsrdq,String ruleszcode,JSONObject resultData){
		Map<String, String> result = new HashMap<String, String>();
		String errorrule = "";
		try{
			for(String rule:ruledata){
				try{
					errorrule = rule;
					if(rule.substring(rule.indexOf("=") + 1)!=null&&rule.substring(rule.indexOf("=") + 1).trim().equals("--")){
						JSONPath.set(resultData, rule.substring(0, rule.indexOf("=")), "--");
						continue;
					}
					if(rule.lastIndexOf("//")!=-1){
						rule = rule.substring(0,rule.lastIndexOf("//"));
					}
					Elements links = doc.select(rule.substring(rule.indexOf("=") + 1)); //带有href属性的a元素
					if (links.size() == 0) {
						error.append(rule + ":html中没有找到节点;");
						log.warn(nsrdq + "-" + ruleszcode + rule + ":" + ":节点不存在");
					} else if (links.size() != 1) {
						error.append(rule + ":html中有重复节点;");
						log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":html中有重复节点");
					} else {
						JSONPath.set(resultData, rule.substring(0, rule.indexOf("=")), links.get(0).wholeText());
					}
				}catch (Exception e){
//					e.printStackTrace();
					result.put("code","error");
					error.append(rule + "规则：["+errorrule+"]转换失败");
				}
			}
			if(result.get("code")==null){
				result.put("code","warn");
			}
			result.put("message",error.toString());
		}catch (Exception e){
			e.printStackTrace();
			log.error("处理错误"+e.getMessage());
		}finally {
			return result;
		}
	}


	/**
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formid
	 * @param form
	 * @param errorresult
	 * @param result
	 * @param resultData
	 * @param ruleEntity
	 * @param doc
	 * @return
	 * @description 报文转化时使用jsoup查找转换文件中每个报文节点对应的html document节点，生成json格式报文数据
	 */
	public Map<String, String> combineHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc) {

		Map<String, String> tempresult = new HashMap<String, String>();
		if (ruleEntity == null) {
			log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);//+formid+"<->"
			tempresult.put("code", "warn:" + form);
			tempresult.put("message", form + ":没有找到对应的表单规则文件");
			return tempresult;
		}
		List<String> rules = new ArrayList<String>();
		StringBuilder error = new StringBuilder();
		rules.addAll(Arrays.asList(ruleEntity.getContent().split("\\n")));
		Map<String, List<String>> rulegroup = RuleDataFilterUtils.splitHtmlGroupByTableRules(rules);
		String temprule = "";
//		JSONObject docObj = new JSONObject();
		String dataprefix = "";
		for (Map.Entry<String, List<String>> ruleset : rulegroup.entrySet()) {
			dataprefix = ruleset.getKey();
			for (String rule : ruleset.getValue()) {
				if (StringUtils.isNotEmpty(rule)) {
					if (rule.indexOf("//") != -1) {
						temprule = rule.substring(0, rule.indexOf("//"));
					} else {
						temprule = rule;
					}
					Matcher m = pattern.matcher(temprule == null ? "" : temprule);
					temprule = m.replaceAll("");
					if (doc != null && StringUtils.isNotEmpty(temprule)) {
						try {
							if (temprule.indexOf("=") == -1 || temprule.substring(rule.indexOf("=") + 1).replace("\\s*", "").equals("")) {
								error.append(rule + ":节点CSS选择器为空");
								log.warn(nsrdq + "-" + ruleszcode + rule + ":" + ":节点CSS选择器为空");
								continue;
							}
							if (rule.indexOf("=") == -1 || temprule.substring(rule.indexOf("=") + 1).equals("")) {
								error.append(rule + ":html中没有找到节点;");
								log.warn(nsrdq + "-" + ruleszcode + rule + ":" + ":节点不存在");
							}
							Elements links = doc.select(temprule.substring(rule.indexOf("=") + 1)); //带有href属性的a元素
							if (links.size() == 0) {
								error.append(rule + ":html中没有找到节点;");
								log.warn(nsrdq + "-" + ruleszcode + rule + ":" + ":节点不存在");
							} else if (links.size() != 1) {
								error.append(rule + ":html中有重复节点;");
								log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":html中有重复节点");
							} else {
								JSONPath.set(resultData, temprule.substring(0, rule.indexOf("=")), links.get(0).wholeText());
							}
						} catch (Exception e) {
							error.append(rule + ":[error]查找数据异常,请检查数据规范填写");
							log.warn(nsrdq + "-" + ruleszcode + rule + ":[error]查找数据异常,请检查数据规范填写");
						}
					} else {
						error.append(" " + rule + "配置信息错误");
						log.warn(nsrdq + "-" + ruleszcode + rule + "配置信息错误dataprefix:" + dataprefix + ",temprule:" + temprule);
					}
				}
			}

		}
		if (error.toString().equals("")) {
			tempresult.put("code", "success:" + form);
			tempresult.put("message", form + ":" + "转化成功");
		} else {
			tempresult.put("code", "error:" + form);
			tempresult.put("message", form + error.toString());
		}
		return tempresult;
	}


	Element trelement = null;
	String rowMarkInfo = "";
	List<Element> doclist  = new ArrayList<Element>();//存放两个相同的征收项目、征收品目doc节点
	/**
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formid
	 * @param form
	 * @param errorresult
	 * @param result
	 * @param resultData
	 * @param ruleEntity
	 * @param doc
	 * @return
	 * @description 获取对应规则的报文结果（根据掩码模糊匹配数据行tr，根据tr寻找td索引）如附加税查找对应项目和附加税
	 */
	public Map<String, String> combineMatchColsHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc) {
		Pattern pattern = Pattern.compile("(\\d+)");
		Pattern pstr = Pattern.compile("\\s*|\t|\r|\n");
		Pattern trrule = Pattern.compile("\\$tr\\{\\$td(\\d+)}@\\s*");
		Pattern contentrule = Pattern.compile("\\$\\{@([\\s\\S]*)@}");
		Pattern tdrule = Pattern.compile("\\$\\{td(\\d+)}");
		Map<String, String> tempresult = new HashMap<String, String>();
		if (ruleEntity == null) {
			log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);
			tempresult.put("code", "warn:" + form);
			tempresult.put("message", form + ":没有找到对应的表单规则文件");
			return tempresult;
		}
		List<String> rules = new ArrayList<String>();
		StringBuilder error = new StringBuilder();
		rules.addAll(Arrays.asList(ruleEntity.getContent().split("\\n")));
		Map<String, List<String>> rulegroup = RuleDataFilterUtils.splitGroupMatchColsByTableRules(rules);
		Map<String, JSONObject> rulegroupJson = new HashMap<String, JSONObject>();
		for (Map.Entry<String, List<String>> ruleplist : rulegroup.entrySet()) {
			try{
				rulegroupJson.put(ruleplist.getKey(), (JSONObject) RuleDataFilterUtils.getValidJsonBefore(ruleplist.getValue()));
			}catch (Exception e){
				log.warn(nsrdq + "-" + ruleszcode + "规则文件有错误"+e.getMessage());//+formid+"<->"
				tempresult.put("code", "warn:" + form);
				tempresult.put("message", form + ":规则文件有错误"+e.getMessage());
				return tempresult;
			}
		}
		if (rulegroupJson.entrySet().size() <= 0) {
			log.warn(nsrdq + "-" + ruleszcode + "规则文件中未找到规则:" + form);//+formid+"<->"
			tempresult.put("code", "warn:" + form);
			tempresult.put("message", form + ":规则文件中未找到规则");
			return tempresult;
		}

				//遍历json模糊查找document节点匹配行tr
		Object docObj = new JSONObject();
		for (Map.Entry<String, JSONObject> e : rulegroupJson.entrySet()) {
			String docPath = (StringUtils.isNotEmpty(e.getKey())&&e.getKey().indexOf("@")!=-1)?e.getKey().substring(0, e.getKey().indexOf("@")):"";
			String elementPath = e.getKey().substring(e.getKey().indexOf("@") + 1);
			JSONArray arr = (JSONArray) JSONPath.eval(e.getValue(), docPath);
			Elements links = doc.select(elementPath + " > tr");

			arr.stream().forEach((jsonobejct) -> {
				int realrow = 0;
				int row = arr.indexOf(jsonobejct);
				try {
					JSONObject json = (JSONObject) jsonobejct;
					Set<String> primiry = new HashSet<String>(6);
					Set<String> items = new HashSet<String>();
					int ignorecount = 0;
					for (Map.Entry<String, Object> e3 : json.entrySet()) {
						if ((tdrule.matcher(e3.getValue() == null ? "" : e3.getValue().toString()).find()) || (trrule.matcher(e3.getValue() == null ? "" : e3.getValue().toString()).find())
								|| (contentrule.matcher(e3.getValue() == null ? "" : e3.getValue().toString()).find())) {
						} else {
							error.append(docPath + "[" + row + "]." + e3.getKey() + ":[error]格式配置错误,请检查数据规范填写");
							log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "]." + e3.getKey() + ":[error]格式配置错误,请检查数据规范填写");
							continue;
						}
						if (e3.getKey().contains("markcol")) {
							ignorecount++;
							primiry.add(e3.getValue().toString());
						}else if (e3.getValue().toString().startsWith("$tr")) {
							primiry.add(e3.getValue().toString());
						} else {
							items.add(e3.getValue().toString());
						}
					}
					int mincols = primiry.size() + items.size() - ignorecount;
					if (doc != null) {
						try {

							//带有href属性的a元素
							trelement = null;
							doclist.clear();
							long count = Arrays.asList(links.toArray(new Element[]{})).stream().filter(trlink -> {
								Elements tds = trlink.getElementsByTag("td");
								if (tds.size() >= mincols) {
									rowMarkInfo = "";
									//存在符合行则行数据都存在
									for (String e1 : primiry) {
                                        String trtdindex = "";
                                        String souceName = "";
                                        String trtdstr = e1.substring(0, e1.indexOf("@"));
                                        String trtdtext = e1.substring(e1.indexOf("@") + 1).replace("\\s*", "");
										rowMarkInfo+=trtdtext;
                                        Matcher m = pstr.matcher(trtdtext);
                                        if (m.find()) {
                                            trtdtext = m.replaceAll("");
                                        }
                                        Matcher matcher = pattern.matcher(trtdstr);
                                        if (matcher.find()) {
                                            trtdindex = matcher.group();
                                        }
                                        m = pstr.matcher(tds.get(Integer.valueOf(trtdindex)).wholeText());
                                        if (m.find()) {
                                            souceName = m.replaceAll("").replaceAll("　　","").replaceAll("　","");
                                         }
                                        if (!souceName.equals(trtdtext)) {
                                            return false;//本来返回false，在这里直接结束掉这一条判断
                                        }
                                    }

									trelement = trlink;
									return true;
									//这种现对上海附加税处理,过滤无效的行数据
                                  /*  double testvalue = tds.get(3).wholeText()==null?new Double(0.00):new Double(tds.get(3).wholeText().trim().replace(",",""));
									if(testvalue!=0.00){
										doclist.add(trlink);
                                        return true;
                                    }else{
                                        return false;
                                    }*/
								}
								return false;
							}).count();
							if (count == 1) {
								htmlFillJosonValue(json,pstr, tdrule,pattern,resultData,docPath,trelement,error,nsrdq,ruleszcode,row);
							}else if (count == 2) {//上海增值税存在征收项目和征收品目相同出现两条的情况。这里处理两条的情况
								realrow =2*row-1;
								for(Element trelement:doclist){
									htmlFillJosonValue(json,pstr, tdrule,pattern,resultData,docPath,trelement,error,nsrdq,ruleszcode,++realrow);
								}
							} else if (count > 1) {
								error.append(docPath + "[" + row + "].【"+rowMarkInfo+"】行找到重复行");
								log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].【"+rowMarkInfo+"】行找到重复行");
							} else {
								error.append(docPath + "[" + row + "].【"+rowMarkInfo+"】行未找到,请检查tr标识列是否填写正确【标识名称必须和税局相同】");
								log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].【"+rowMarkInfo+"】行未找到,请检查tr标识列是否填写正确【标识名称必须和税局相同】");
							}
						} catch (Exception e2) {
							e2.printStackTrace();
							error.append(docPath + "[" + row + "].【"+rowMarkInfo+"】行数据处理异常" + e2.getMessage());
							log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].【"+rowMarkInfo+"】行数据处理异常" + e2.getMessage());
						}
					} else {
						error.append(docPath + "[" + row + "].html对应的document节点为空");
						log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].html对应的document节点为空");
					}
				} catch (Exception e3) {
					error.append(docPath + "[" + row + "].处理数据异常，请查看联系管理人员查看具体问题" + e3.getMessage());
					log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].处理数据异常，请查看联系管理人员查看具体问题" + e3.getMessage());
				}
			});
		}
		if (error.toString().equals("")) {
			tempresult.put("code", "success:" + form);
			tempresult.put("message", form + ":" + "转化成功");
		} else {
			tempresult.put("code", "error:" + form);
			tempresult.put("message", form + error.toString());
		}
		return tempresult;
	}


	/**
	 * @param json
	 * @param pstr
	 * @param tdrule
	 * @param resultData
	 * @param docPath
	 * @param trelement
	 * @param error
	 * @param nsrdq
	 * @param ruleszcode
	 * @param row
	 */
	public static void htmlFillJosonValue(JSONObject json,Pattern pstr,Pattern tdrule,Pattern pattern,JSONObject resultData,String docPath,Element trelement,StringBuilder error,
										  String nsrdq,String ruleszcode,int row){
		Pattern contentrule = Pattern.compile("\\$\\{@([\\s\\S]*)@}");
		for (Map.Entry<String, Object> document : json.entrySet()) {
			try {
				Matcher mcontent = contentrule.matcher(document.getValue() == null ? "" : document.getValue().toString());
				if(document.getKey().contains("markcol")){
					continue;
				}else if (document.getValue().toString().startsWith("$tr")) {
					String value = ((String) document.getValue()).substring(((String) document.getValue()).indexOf("@") + 1);
					Matcher m = pstr.matcher(value);
					if (m.find()) {
						value = m.replaceAll("");
					}
					JSONPath.set(resultData, docPath + "[" + row + "]." + document.getKey(), value);
				} else if(document.getValue()!=null&&document.getValue().toString().trim().equals("--")){
					JSONPath.set(resultData, docPath + "[" + row + "]." + document.getKey(), "--");
				}else if(mcontent.find()){
					JSONPath.set(resultData, docPath + "[" + row + "]." + document.getKey(), mcontent.group().replaceAll("\\$\\{@|@}",""));
				}else{
					String index = null;
					String indexstr = null;
					Matcher m = tdrule.matcher(String.valueOf(document.getValue()));
					if (m.find()) {
						indexstr = m.group();
						Matcher matcher = pattern.matcher(indexstr);
						if (matcher.find()) {
							index = matcher.group();
						}
					}
					Element td = trelement.getElementsByTag("td").get(Integer.parseInt(index)-1);
					JSONPath.set(resultData, docPath + "[" + row + "]." + document.getKey(), td.wholeText());
				}
			} catch (Exception eo) {
				error.append(docPath + "[" + row + "]." + document.getKey() + "生成报文数据异常" + eo.getMessage());
				log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "]." + document.getKey() + "生成报文数据异常" + eo.getMessage());
				eo.printStackTrace();
			}
		}
	}

	/**
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formid
	 * @param form
	 * @param error
	 * @param result
	 * @param resultData
	 * @param ruleEntity
	 * @param doc
	 * @return
	 * @description 针对减免税动态增加行实现动态行组装数据，报文内容里边分固定tr(固定数据)，动态行区段（动态增加的行），结束行信息
	 */
	public Map<String, String> combineDynamicRowsHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder error, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc,Map<String,String> ruledata) {
		Map<String, String> result1 = new HashMap<String, String>(2);
		try{
			String fixtopmarkTrs= null;String fixtoppropertyTrs = null;
			String dynamicmarkTrs= null;String dynamicpropertyTrs = null;
			String fixbtmmarkTrs= null;String fixbtmpropertyTrs = null;
			Map<String,String> rulemap = ruledata;
			String reportpath = rulemap.get("reportpath");
			String documentpath = rulemap.get("documentpath");
			String fixedtoptr = rulemap.get("fixedtoptr");
			String fixedbottomtr = rulemap.get("fixedbottomtr");
			String dynamictrstartpos = rulemap.get("dynamictrstartpos");
			String dynamictrreleations = rulemap.get("dynamictrreleations");
			String dynamictrendpos = rulemap.get("dynamictrendpos");

			//固定行处理
			if(fixedtoptr.indexOf("@")!=-1){
				fixtopmarkTrs = fixedtoptr.split("@")[0];
				fixtoppropertyTrs = fixedtoptr.split("@")[1];
			}else{
				//固定行格式不正确
			}
			if(fixedbottomtr.indexOf("@")!=-1){
				fixbtmmarkTrs = fixedbottomtr.split("@")[0];
				fixbtmpropertyTrs = fixedbottomtr.split("@")[1];
			}else{
				//固定行格式不正确
			}
			//动态增加行处理
			if(dynamictrstartpos!=null){
				dynamicmarkTrs = dynamictrstartpos;
				dynamicpropertyTrs = dynamictrreleations;
			}else{
				//动态增加行格式不正确
			}
			//固定行和动态增加行一同匹配处理
			Elements links = doc.select(documentpath + " > tr");
			int row = -1;
			boolean start = false;
			boolean skipTr = false;
			//定义查找的对象名称组
			List<String> fixtoparr = fixtopmarkTrs==null?Collections.EMPTY_LIST:new ArrayList<String>(Arrays.asList(fixtopmarkTrs.split("#")));//固定组上
			List<String> fixbtmarr = fixbtmmarkTrs==null?Collections.EMPTY_LIST:new ArrayList<String>(Arrays.asList(fixbtmmarkTrs.split("#")));//固定组下
			List<String> dynamicarr = dynamicmarkTrs==null?Collections.EMPTY_LIST:new ArrayList<String>(Arrays.asList(dynamicmarkTrs.split("#")));//固定组下
			try{
				out :for(int oooo = 0;oooo<links.size();oooo++){
					Element e=links.get(oooo);
					Elements tds = e.getElementsByTag("td");
					//查找起始位置
					skipTr = false;
					if(RuleDataFilterUtils.checkValidTrElement(e,dynamictrstartpos)){
						start = true;
						continue;
					}else if(!start) {
						continue;
					}
					if(!dynamictrendpos.trim().equals("")&&RuleDataFilterUtils.checkValidTrElement(e,dynamictrendpos)){
						start = false;
						break out;
					}
					if(start){
						//遍历top固定行
						//遍历动态行
						//遍历bottom固定行
						if(fixtopmarkTrs!=null){
							for(String tr:fixtopmarkTrs.split("#")) {
								if(RuleDataFilterUtils.checkValidTrElement(e,tr)){//固定行校验
									fixtoparr.remove(tr);
									skipTr = true;
									row = row+1;
									for(String tdproperty:fixtoppropertyTrs.split(",")){
										JSONPath.set(resultData, reportpath + "[" + row + "]." + tdproperty.split("-")[0], tds.get(Integer.valueOf(tdproperty.split("-")[1])-1).wholeText());
									}
								}
							}
						}

						if(!skipTr&&fixbtmmarkTrs!=null){
							for(String tr:fixbtmmarkTrs.split("#")) {
								if(RuleDataFilterUtils.checkValidTrElement(e,tr)){//动态行校验
									fixbtmarr.remove(tr);
									row = row+1;
									skipTr = true;
									for(String tdproperty:fixbtmpropertyTrs.split(",")){
										JSONPath.set(resultData, reportpath + "[" + row + "]." + tdproperty.split("-")[0], tds.get(Integer.valueOf(tdproperty.split("-")[1])-1).wholeText());
									}
								}
							}
						}
						if(!skipTr&&dynamicmarkTrs!=null) {
							for(String tr:dynamicmarkTrs.split("#")) {
								if(RuleDataFilterUtils.checkValidTrElement(e,tr)){//动态行校验
									dynamicarr.remove(tr);
									row = row+1;
									for(String tdproperty:dynamicpropertyTrs.split(",")){
										JSONPath.set(resultData, reportpath + "[" + row + "]." + tdproperty.split("-")[0], tds.get(Integer.valueOf(tdproperty.split("-")[1])-1).wholeText());
									}
								}
							}
						}
					}

				}
			}catch (Exception e){
				log.error("动态行获取出错"+e.getMessage());
				error.append("动态行获取出错"+e.getMessage());
			}
			//最终提示未找到的行
			if (!error.toString().equals("")) {
				result1.put("code", "error:" + form);
				result1.put("message", form + error.toString());
			} else if(dynamicarr.size()>0||fixbtmarr.size()>0||fixtoparr.size()>0){
				result1.put("code", "warn:" + form);
				result1.put("message", form + ":" + "转化成功,但有警告信息!["+ org.apache.commons.lang3.StringUtils.join(dynamicarr,"],[")+"]名称的行信息未找到");
			}else{
				result1.put("code", "success:" + form);
				result1.put("message", form + ":" + "转化成功");
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			return result1;
		}
	}


	/**
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formid
	 * @param form
	 * @param errorresult
	 * @param result
	 * @param resultData
	 * @param ruleEntity
	 * @param doc
	 * @return
	 * @description 针对减免税动态增加行实现动态行组装数据，报文内容里边分固定tr(固定数据)，动态行区段（动态增加的行），结束行信息
	 */
	public Map<String, String> combineDynamicBoolean(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc) {
//		Pattern pattern = Pattern.compile("(\\d+)");
//		Pattern pstr = Pattern.compile("\\s*|\t|\r|\n");
//		Pattern trrule = Pattern.compile("\\$tr\\{\\$td(\\d+)}@\\s*");
//		Pattern tdrule = Pattern.compile("\\$\\{td(\\d+)}");

		return null;
	}

}
package com.pangu.crawler.transfer.service;

import com.alibaba.fastjson.*;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryBusinessTransferRule;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryStockDataOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.Base64Util;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.iservice.ITransferHtmlDataService;
import com.pangu.crawler.transfer.service.iservice.ITransferJsonDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TransferRuleDataService {

	@Autowired
	ITransferJsonDataService transferJsonDataService;

	@Autowired
	ITransferHtmlDataService transferHtmlDataService;

	@Autowired
	CrawleredDataService crawleredDataServiceService;
	@Autowired
	AsyncQueryBusinessTransferRule asyncQueryBusinessTransferRule;
	@Autowired
	TaxCodeReleationService taxCodeReleationService;


	/**
	 * @param params
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception
	 * @查询规则数据
	 */
	public Paging<AsyncBusinessTransferRuleEntity> queryHistoricalData(Map<String, String> params, Integer page, Integer limit) throws Exception {
		HelpDocListQuery docListQuery = new HelpDocListQuery();
		docListQuery.setPage(page);
		docListQuery.setPageSize(limit);
		docListQuery.setObjid(params.get("id"));
		docListQuery.setNsrsbh(params.get("nsrsbh"));
		docListQuery.setNsrdq(params.get("nsrdq"));
		docListQuery.setDatasz(params.get("datasz"));
		docListQuery.setRulesz(params.get("rulesz"));
		docListQuery.setFormid(params.get("formid"));
		docListQuery.setType(params.get("type"));
		docListQuery.setStatus(params.get("status"));
		Paging<AsyncBusinessTransferRuleEntity> paging = asyncQueryBusinessTransferRule.findTransferDataList(docListQuery);
		return paging;
	}

	/**
	 * @param rules
	 * @param params
	 * @return
	 * @throws Exception
	 * @description 修改规则文件服务
	 */
	public Map<String, String> updateTransferRuleDataByid(List<String> rules, Map<String, String> params) throws Exception {
		return asyncQueryBusinessTransferRule.updateTransferRuleDataByid(rules, params);
	}

	/**
	 * @param id
	 * @param rule_status
	 * @return
	 * @throws Exception
	 * @descritiopn 更新规则禁用启用状态服务
	 */
	public Map<String, String> uploadRuleStatusByid(String id, String rule_status) throws Exception {
		return asyncQueryBusinessTransferRule.uploadRuleStatusByid(id, rule_status);
	}

	/**
	 * @param id
	 * @param nsrdq
	 * @param sz
	 * @param formid
	 * @param type
	 * @param content
	 * @param ruleobjid
	 * @return
	 * @throws Exception
	 * @description 规则管理校验调用服务
	 */
	public Map<String, Object> checkRuleLogic(String id, String nsrdq, String sz, String formid, String type, String content, String ruleobjid) throws Exception {
		Map<String, String> params = new HashMap<String, String>(4);
		Map<String, Object> result = new HashMap<String, Object>(4);
		if (StringUtils.isNotEmpty(id)) {
			params.put("id", id);
		}
		if (StringUtils.isNotEmpty(nsrdq)) {
			params.put("nsrdq", nsrdq);
		}
		/*if(StringUtils.isNotEmpty(sz)){
			params.put("sz",sz);
		}*/
		if (StringUtils.isNotEmpty(formid)) {
			params.put("formid", formid);
		}

		if (StringUtils.isNotEmpty(type)) {
			params.put("type", type);
		}
		List<AsyncQueryHistoricalDataInfoEntity> dataInfoEntitys = crawleredDataServiceService.queryHistoricalData(null, params, null, null).getData();
		if (dataInfoEntitys == null || dataInfoEntitys.size() <= 0) {
			result.put("finalresult", "数据属于该地区该税种或对应id的原始数据不存在");
			result.put("code", "fail");
			return result;
		}
		AsyncQueryHistoricalDataInfoEntity dataInfoEntity = dataInfoEntitys.get(0);
		Map<String, String> sbdata = new HashMap<String, String>(10);
		JSONObject tables = null;
		if (dataInfoEntity.getSbData() != null) {
			try {
				tables = ((JSONObject) JSON.toJSON(dataInfoEntity.getSbData()));
			} catch (Exception e) {
				e.printStackTrace();
				log.error(dataInfoEntity.getId() + "转化成JSON格式报文数据出错");//TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf1)+
				result.put("finalresult", "转化成JSON格式报文数据出错");
				result.put("code", "fail");
				return result;
			}
			for (Map.Entry<String, Object> map : tables.entrySet()) {
				sbdata.put(map.getKey(), Base64Util.decode(map.getValue() != null ? map.getValue().toString() : ""));
			}
		}
		Map<String, Object> resultVal = new HashMap<String, Object>();
		if (type != null && type.equals("json")) {
			params.clear();
			params.put("type", "json");
			params.put("nsrdq", nsrdq);
			params.put("rulesz", sz);
			params.put("id", ruleobjid);
			List<AsyncBusinessTransferRuleEntity> transferRuleEntity = queryHistoricalData(params, null, null).getData();
			if (transferRuleEntity.size() <= 0) {
				log.warn(nsrdq + "-" + sz + "没有上传对应税种规则文件");
				result.put("code", "warn");
				result.put("finalresult", "没有上传对应此税种规则文件;");
				return result;
			}
			resultVal = transferJsonDataService.majarTransferJsonData(sbdata, nsrdq, sz, formid, ruleobjid, transferRuleEntity);
			params.clear();
			params.put("id", id);
			params.put("data_out", Base64Util.encode(JSON.toJSONString(resultVal.get("data"))));
			resultVal.remove("data");
			params.put("result", Base64Util.encode(JSON.toJSONString(resultVal)));
			params.put("zhstate", "3");
			crawleredDataServiceService.updateStockDataByid(params);
			return resultVal;
		} else if (type != null && type.equals("html")) {
			params.put("rulesz", sz);
			params.put("nsrdq", nsrdq);
			params.put("type", "html");
			params.put("id", ruleobjid);
			List<AsyncBusinessTransferRuleEntity> transferRuleEntity = queryHistoricalData(params, null, null).getData();
			if (transferRuleEntity.size() <= 0) {
				log.warn(nsrdq + "-" + sz + "没有上传对应文件");
				result.put("code", "warn");
				result.put("finalresult", "没有上传此税种对应文件");
				return result;
			}
			resultVal = transferJsonDataService.majarTransferJsonData(sbdata, nsrdq, sz, formid, ruleobjid, transferRuleEntity);
			params.clear();
			params.put("id", id);
			params.put("data_out", Base64Util.encode(JSON.toJSONString(resultVal.get("data"))));
			resultVal.remove("data");
			params.put("result", JSON.toJSONString(resultVal));
			params.put("zhstate", "3");
			crawleredDataServiceService.updateStockDataByid(params);
			return resultVal;
		} else {
			return Collections.emptyMap();
		}
	}

	/**
	 * @param rules
	 * @return
	 * @description 规则分割规则方案(自适应组合识别行之后按照索引找列)
	 *//*
	private Map<String, List<String>> splitGroupMatchColsByTableRules(List<String> rules) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> listRules = new ArrayList<String>();
		String prefix = "";
		Collections.reverse(rules);
		for (String rule : rules) {
			if (StringUtils.isEmpty(rule) || rule.startsWith("//") || rule.replace(" ", "").startsWith("##version")|| (rule.startsWith("##") && rule.contains("version"))) {
				continue;
			}
			if (rule.indexOf("//") != -1) {
				if (rule.substring(0, rule.indexOf("//")).replaceAll("\\s*", "").endsWith("--")) {
					continue;
				}
			} else if (rule.replaceAll("\\s*", "").endsWith("--")) {
				continue;
			}
			if (rule.startsWith("matchtrprefix=") && rule.indexOf("//") != -1) {
				String ruleconfig = rule.substring(0, rule.indexOf("//"));
				prefix = ruleconfig.substring(ruleconfig.indexOf("=") + 1);
			} else if (rule.startsWith("matchtrprefix=")) {
				prefix = rule.substring(rule.indexOf("=") + 1);
			}
			if (!rule.startsWith("matchtrprefix=")) {
				listRules.add(rule);
			}
			if (rule.startsWith("matchtrprefix=")) {
				map.put(prefix, listRules);
				listRules = new ArrayList<String>();
			}
		}
		if (map.isEmpty()) {
			map.put(" ", listRules);
		}
		return map;
	}

	*//**
	 * @param rules
	 * @return
	 * @description 规则分割规则方案
	 *//*
	private Map<String, List<String>> splitGroupByTableRules(List<String> rules) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> listRules = new ArrayList<String>();
		String prefix = "";
		Collections.reverse(rules);


		for (String rule : rules) {
			Matcher m = pattern.matcher(rule);
			rule = m.replaceAll("");
			if (rule == null || StringUtils.isEmpty(rule) || rule.startsWith("//") || rule.startsWith("## version") || (rule.startsWith("##") && rule.contains("version"))) {
				continue;
			}
			if (rule.indexOf("//") != -1) {
				if (rule.substring(0, rule.indexOf("//")).replaceAll("\\s*", "").endsWith("--")) {
					continue;
				}
			} else if (rule.replaceAll("\\s*", "").endsWith("--")) {
				continue;
			}
			if (rule.startsWith("prefix=") && rule.indexOf("//") != -1) {
				String ruleconfig = rule.substring(0, rule.indexOf("//"));
				prefix = ruleconfig.substring(ruleconfig.indexOf("=") + 1);
			} else if (rule.startsWith("prefix=")) {
				prefix = rule.substring(rule.indexOf("=") + 1);
			}
			if (!rule.startsWith("prefix=")) {
				listRules.add(rule);
			}
			if (rule.startsWith("prefix=")) {
				map.put(prefix, listRules);
				listRules = new ArrayList<String>();
			}
		}
		if (map.isEmpty()) {
			map.put(" ", listRules);
		}
		return map;
	}


	*//**
	 * @param rules
	 * @return
	 * @description 获取有效的json报文
	 *//*
	private Object getValidJsonBefore(List<String> rules) {
		//重组josn报文json,这里只有有效值
		String key = "";
		String value = "";
		String temprule = "";
		Object resultData = new JSONObject();
		for (String rule : rules) {
			if (rule.indexOf("//") != -1) {
				temprule = rule.substring(0, rule.indexOf("//"));
			} else {
				temprule = rule;
			}
			key = temprule.substring(0, temprule.indexOf("="));
			value = temprule.substring(temprule.indexOf("=") + 1);
			JSONPath.set(resultData, key, value);
		}
		return resultData;
	}


	*//**
	 * @param dataMap
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formcode
	 * @param id
	 * @return
	 * @description 转化原始类型为josn格式的报文为标准报文
	 *//*
	public Map<String, Object> transferJsonData(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity) {
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
					log.warn(TimeUtils.getCurrentDateTime(new Date(), TimeUtils.sdf4) + nsrdq + "-" + ruleszcode + "-" + formcode + "表单内容不存在!");
					tempresult = new HashMap<String, String>();
					tempresult.put("code","fail:" + form );
					tempresult.put("message", form + ":数据源表单内容null");
					result.put(form, tempresult);

					continue;
				}
				String formid = "";
				JSONObject jsonsource = JSON.parseObject(map.getValue());//jsonstring
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
				if (ruleEntity == null) {
					log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);
					result.put("code", "warn");
					result.put("message", "没有找到对应的表单规则文件:" + form);
				} else {
					tempresult = combineJSONResultData(nsrdq, ruleszcode, formid, form, error, result, resultData, ruleEntity, jsonsource);
					result.put(form, tempresult);
				}
				*//*if(!error.toString().equals("")){
					tempresult.put("code",form+"fail");
					tempresult.put("message",form+"\n"+error.toString());
					result.put(form,tempresult);
				}else{
					tempresult.put("code","success;");
					tempresult.put("message",form+"转化成功");
					result.put(form,tempresult);
				}*//*
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


	*//**
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
	 *//*
	private Map<String, String> combineJSONResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder error, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, JSONObject jsonsource) {
		Map<String, String> tempresult = new HashMap<String, String>();
		if (ruleEntity == null) {
			log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + formid);//+formid+"<->"
			tempresult.put("code", "warn:" + formid);
			tempresult.put("message", formid + ":没有找到对应的表单规则文件");
			return tempresult;
		}

		List<String> rules = new ArrayList<String>();

		rules.addAll(Arrays.asList(ruleEntity.getContent().split("\\n")));
		Map<String, List<String>> rulegroup = splitGroupByTableRules(rules);

		String temprule = "";
//		JSONObject docObj = new JSONObject();
		String dataprefix = "";
//				rules.remove(0);
		for (Map.Entry<String, List<String>> ruleset : rulegroup.entrySet()) {
			dataprefix = ruleset.getKey();
			*//**
			 * @variable matchextenal 记录需要查找有多条数据的精准变量值
			 *//*
			String matchextenal = null;
			for (String rule : ruleset.getValue()) {
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
								log.warn(nsrdq + "-" + ruleszcode + rule + ":节点JSON选择器为空");
								error.append(dataprefix + "-> " + rule + ":节点JSON选择器为空");
								continue;
							}

							String jvpath = (StringUtils.isNotEmpty(dataprefix) ? "" : (dataprefix + ".")) + temprule.substring(rule.indexOf("=") + 1).replace("\\s*", "");
							if (jvpath == null || jvpath.equals("")) {
								log.warn(nsrdq + "-" + ruleszcode + rule + ":[error]查找数据为空,请检查数据是否可以忽略");
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
												log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点重复");
												error.append(dataprefix + "-> " + ":" + rule + ":节点重复");
												continue;
											} else {
												log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点不存在");
												error.append(dataprefix + "-> " + ":" + rule + ":节点不存在");
												continue;
											}
										}
									} else {
										log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点重复");
										error.append(dataprefix + "-> " + ":" + rule + ":节点重复");
										continue;
									}
								} else {
									log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点不存在");
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
								log.warn(nsrdq + "-" + ruleszcode + ":" + rule + ":节点不存在");
								error.append(dataprefix + "-> " + ":" + rule + ":节点不存在\n");
							} else {
								JSONPath.set(resultData, temprule.substring(0, rule.indexOf("=")), o == null ? "" : o);
							}
						} catch (Exception e) {
//							e.printStackTrace();
							log.warn(nsrdq + "-" + ruleszcode + rule + ":[error]查找数据异常,请检查数据规范填写");
							error.append(rule + ":[error]查找数据异常,请检查数据规范填写\n");

						}
					} else {
						log.warn(nsrdq + "-" + ruleszcode + rule + ":配置信息错误dataprefix:" + dataprefix + ",temprule:" + temprule);
						error.append(rule + ":配置信息错误\n");
					}
				}
			}
//			resultData.put("",docObj);
//			resultData.putAll((JSONObject)docObj);
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

	*//**
	 * @param dataMap
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formcode
	 * @param id
	 * @return
	 * @description 转化原始类型为html格式的报文为标准报文
	 *//*
	public Map<String, Object> transferHtmlData(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> tempresult  = new HashMap<String, String>();
		JSONObject resultData = new JSONObject();
		StringBuilder error = new StringBuilder();
		Paging<AsyncAreaTaxCodeReleationEntity> taxCodeReleationEntity = null;
		try {
			boolean breakoneflag = false;
			for (Map.Entry<String, String> map : dataMap.entrySet()) {
//				tempresult.clear();
				if (map.getKey().equals("zb")) {
					breakoneflag = true;
				}
				String form = map.getKey();
				//这里是增值税里边嵌套附件税的情况   一个html的节点
				if (map.getKey().equals("zzsybnsr&fjs")) {
					Map<String, String> zzsfjsMap = new HashMap<String, String>();
					zzsfjsMap.put(SzEnum.fjs.getCode(), dataMap.get(map.getKey()));
					Map<String, String> paramsfjs = new HashMap<String, String>(4);
					paramsfjs.put("type", "html");
					paramsfjs.put("nsrdq", nsrdq);
					paramsfjs.put("rulesz", SzEnum.fjs.getCode());
					List<AsyncBusinessTransferRuleEntity> transferRuleEntityfjs = queryHistoricalData(paramsfjs, null, null).getData();
					//这种情况单独处理所有表单
					Map<String, Object> resultfjs = transferMixHtml(zzsfjsMap, nsrdq, SzEnum.fjs.getCode(), formcode, id, transferRuleEntityfjs);
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
					if (!formid.equals(form)) {
						continue;
					} else {
						ruleEntity = ruleEntity1;
					}
					if (breakoneflag == true) {//zb代表只有一个数据块，该税种下每一个规则都在这里校验，完成后直接返回
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

				if (ruleEntity == null) {
					log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);
					tempresult = new HashMap<String, String>();
					tempresult.put("code", "warn:" + form);
					tempresult.put("message", "没有找到对应的表单规则文件:" + form);
					result.put(form, tempresult);
				} else {
					tempresult = new HashMap<String, String>();
					tempresult = combineHtmlResultData(nsrdq, ruleszcode, formid, form, error, result, resultData, ruleEntity, doc);
					result.put(form, tempresult);
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


	*//**
	 * @param dataMap
	 * @param nsrdq
	 * @param ruleszcode
	 * @param formcode
	 * @param id
	 * @return
	 * @description 转化原始类型为html格式的报文为标准报文
	 *//*
	public Map<String, Object> transferMixHtml(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> tempresult = new HashMap<String, String>();
		JSONObject resultData = new JSONObject();
		StringBuilder error = new StringBuilder();
		try {
			for (Map.Entry<String, String> map : dataMap.entrySet()) {
//				tempresult.clear();
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
					doc = Jsoup.parse(map.getValue());
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


	*//**
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
	 * @description 获取对应规则的报文结果
	 *//*
	private Map<String, String> combineHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc) {

		Map<String, String> tempresult = new HashMap<String, String>();
		if (ruleEntity == null) {
			log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);//+formid+"<->"
			tempresult.put("code", "warn:" + form);
			tempresult.put("message", form + ":没有找到对应的表单规则文件");
			return tempresult;
		}
//				log.info("数据源表单编号:"+form+"规则表单编号"+formid);
		List<String> rules = new ArrayList<String>();
		StringBuilder error = new StringBuilder();
		rules.addAll(Arrays.asList(ruleEntity.getContent().split("\\n")));
		Map<String, List<String>> rulegroup = splitGroupByTableRules(rules);
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
							log.warn("test1111" + temprule.substring(rule.indexOf("=") + 1) + "test");
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
//			resultData.put("",docObj);
//			resultData.putAll((JSONObject)docObj);
			tempresult.put("code", "success:" + form);
			tempresult.put("message", form + ":" + "转化成功");
		} else {
			tempresult.put("code", "error:" + form);
			tempresult.put("message", form + error.toString());
		}
		return tempresult;
	}


	Element trelement = null;

	*//**
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
	 * @description 获取对应规则的报文结果（根据掩码模糊匹配数据行tr，根据tr寻找td索引）
	 *//*
	private Map<String, String> combineMatchColsHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc) {
		Pattern pattern = Pattern.compile("(\\d+)");
		Pattern pstr = Pattern.compile("\\s*|\t|\r|\n");
		Pattern trrule = Pattern.compile("\\$tr\\{\\$td(\\d+)}@\\s*");
		Pattern tdrule = Pattern.compile("\\$\\{td(\\d+)}");
		Map<String, String> tempresult = new HashMap<String, String>();
		if (ruleEntity == null) {
			log.warn(nsrdq + "-" + ruleszcode + "没有找到对应的表单规则文件:" + form);//+formid+"<->"
			tempresult.put("code", "warn:" + form);
			tempresult.put("message", form + ":没有找到对应的表单规则文件");
			return tempresult;
		}
//				log.info("数据源表单编号:"+form+"规则表单编号"+formid);
		List<String> rules = new ArrayList<String>();
		StringBuilder error = new StringBuilder();
		rules.addAll(Arrays.asList(ruleEntity.getContent().split("\\n")));
		Map<String, List<String>> rulegroup = splitGroupMatchColsByTableRules(rules);
		Map<String, JSONObject> rulegroupJson = new HashMap<String, JSONObject>();
		for (Map.Entry<String, List<String>> ruleplist : rulegroup.entrySet()) {
			rulegroupJson.put(ruleplist.getKey(), (JSONObject) getValidJsonBefore(ruleplist.getValue()));
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
				int row = arr.indexOf(jsonobejct);
				try {
					JSONObject json = (JSONObject) jsonobejct;
					Set<String> primiry = new HashSet<String>(6);
					Set<String> items = new HashSet<String>();
					for (Map.Entry<String, Object> e3 : json.entrySet()) {
						if ((tdrule.matcher(e3.getValue() == null ? "" : e3.getValue().toString()).find()) || (trrule.matcher(e3.getValue() == null ? "" : e3.getValue().toString()).find())) {
						} else {
							error.append(docPath + "[" + row + "]." + e3.getKey() + ":[error]格式配置错误,请检查数据规范填写");
							log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "]." + e3.getKey() + ":[error]格式配置错误,请检查数据规范填写");
							continue;
						}
						if (e3.getValue().toString().startsWith("$tr")) {
							primiry.add(e3.getValue().toString());
						} else {
							items.add(e3.getValue().toString());
						}
					}
					int mincols = primiry.size() + items.size();
					if (doc != null) {
						try {
							//带有href属性的a元素
							long coutn = Arrays.asList(links.toArray(new Element[]{})).stream().filter(trlink -> {
								trelement = null;
								Elements tds = trlink.getElementsByTag("td");
								if (tds.size() >= mincols) {
									//存在符合行则行数据都存在
									for (String e1 : primiry) {
                                        String trtdindex = "";
                                        String souceName = "";
                                        String trtdstr = e1.substring(0, e1.indexOf("@"));
                                        String trtdtext = e1.substring(e1.indexOf("@") + 1).replace("\\s*", "");
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
                                            souceName = m.replaceAll("");
                                        }
                                        if (!souceName.equals(trtdtext)) {
                                            return false;
                                        }
                                    }
									trelement = trlink;
									//这种现对上海附加税处理
                                    double testvalue = tds.get(3).wholeText()==null?new Double(0.00):new Double(tds.get(3).wholeText().trim().replace(",",""));
									if(testvalue!=0.00){
                                        return true;
                                    }else{
                                        return false;
                                    }

								}
								return false;
							}).count();
							if (coutn == 1) {
								for (Map.Entry<String, Object> document : json.entrySet()) {
									try {
										if (document.getValue().toString().startsWith("$tr")) {
											String value = ((String) document.getValue()).substring(((String) document.getValue()).indexOf("@") + 1);
											Matcher m = pstr.matcher(value);
											if (m.find()) {
												value = m.replaceAll("");
											}
											JSONPath.set(resultData, docPath + "[" + row + "]." + document.getKey(), value);
										} else {
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
											Element td = trelement.getElementsByTag("td").get(Integer.parseInt(index));
											JSONPath.set(resultData, docPath + "[" + row + "]." + document.getKey(), td.wholeText());
										}
									} catch (Exception eo) {
										error.append(docPath + "[" + row + "]." + document.getKey() + "生成报文数据异常" + eo.getMessage());
										log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "]." + document.getKey() + "生成报文数据异常" + eo.getMessage());
										eo.printStackTrace();
									}
								}
							} else if (coutn > 1) {
								error.append(docPath + "[" + row + "].行找到重复行");
								log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].行找到重复行");
							} else {
								error.append(docPath + "[" + row + "].行未找到,请检查tr标识列是否填写正确【标识名称必须和税局相同】");
								log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].行未找到,请检查tr标识列是否填写正确【标识名称必须和税局相同】");
							}
						} catch (Exception e2) {
							e2.printStackTrace();
							error.append(docPath + "[" + row + "].行数据处理异常" + e2.getMessage());
							log.warn(nsrdq + "-" + ruleszcode + docPath + "[" + row + "].行数据处理异常" + e2.getMessage());
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
//			resultData.put("",docObj);
			//resultData.putAll((JSONObject)docObj);
			tempresult.put("code", "success:" + form);
			tempresult.put("message", form + ":" + "转化成功");
		} else {
			tempresult.put("code", "error:" + form);
			tempresult.put("message", form + error.toString());
		}
		return tempresult;
	}


	public static void main(String[] args) {
//        transferJsonData("");
//        transferHtmlData("");
	}*/
}
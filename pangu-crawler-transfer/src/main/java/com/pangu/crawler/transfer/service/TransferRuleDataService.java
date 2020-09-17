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

}
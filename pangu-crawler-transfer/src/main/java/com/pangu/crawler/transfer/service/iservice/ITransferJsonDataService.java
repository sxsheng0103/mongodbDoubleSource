package com.pangu.crawler.transfer.service.iservice;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/8/21 17:18
 * @Version 1.0
 **/
public interface ITransferJsonDataService {
    Map<String, Object> majarTransferJsonData(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity);


    Map<String, String> combineJSONResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder error, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, JSONObject jsonsource);
}

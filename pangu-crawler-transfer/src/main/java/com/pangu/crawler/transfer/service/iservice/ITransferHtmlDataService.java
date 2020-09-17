package com.pangu.crawler.transfer.service.iservice;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import org.jsoup.nodes.Document;

import java.util.*;

/**
 * @Author sheng.ding
 * @Date 2020/8/21 17:18
 * @Version 1.0
 **/
public interface ITransferHtmlDataService {
    Map<String, Object> majarTransferHtmlData(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity);

    Map<String, Object> transferMixHtml(Map<String, String> dataMap, String nsrdq, String ruleszcode, String formcode, String id, List<AsyncBusinessTransferRuleEntity> transferRuleEntity,JSONObject resultData);

    Map<String, String> combineDynamicRowsHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc,Map<String,String> ruledata );

    Map<String, String> combineHtmlResultData(String nsrdq, String ruleszcode, String formid, String form, StringBuilder errorresult, Map<String, Object> result, JSONObject resultData, AsyncBusinessTransferRuleEntity ruleEntity, Document doc);


    /**
     * @description 当数据格式全时标准的Css选择器，不需要通过指定标识查找时，通过这个方法来转化生成标准的报文
     * @param ruledata
     * @param error
     * @param doc
     * @param nsrdq
     * @param ruleszcode
     * @param resultData
     * @return
     */
    Map<String,String> transferNomalRule(List<String> ruledata,StringBuilder error,Document doc,String nsrdq,String ruleszcode,JSONObject resultData);
}

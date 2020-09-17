package com.pangu.crawler.transfer.service.iservice;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.utils.RuleDataFilterUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;

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
}

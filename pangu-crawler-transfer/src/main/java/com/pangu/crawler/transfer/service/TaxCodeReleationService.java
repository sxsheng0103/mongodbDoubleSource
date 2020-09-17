package com.pangu.crawler.transfer.service;

import com.alibaba.fastjson.*;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryAreaTaxCodeReleation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryBusinessTransferRule;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.Base64Util;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TaxCodeReleationService {

	@Autowired
	AsyncQueryAreaTaxCodeReleation asyncQueryAreaTaxCodeReleation;

	public Paging<AsyncAreaTaxCodeReleationEntity> queryHistoricalData(Map<String,String> params, Integer page, Integer limit) throws Exception {
		HelpDocListQuery docListQuery= new HelpDocListQuery();
		docListQuery.setPage(page);
		docListQuery.setPageSize(limit);
		docListQuery.setNsrdq(params.get("nsrdq"));
		docListQuery.setDatasz(params.get("datasz"));
		docListQuery.setRulesz(params.get("rulesz"));
		Paging<AsyncAreaTaxCodeReleationEntity>  paging= asyncQueryAreaTaxCodeReleation.findTaxCodeReleationDataList(docListQuery);
		return paging;
	}

	public Map<String,String> updateTaxReleationDataByid(Map<String, String> params) throws Exception {
		return asyncQueryAreaTaxCodeReleation.updateTaxReleationDataByid(params);
	}


	public static void main(String[] args) {
//        transferJsonData("");
//        transferHtmlData("");
	}

}
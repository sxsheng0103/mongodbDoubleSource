package com.pangu.crawler.transfer.service;

import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryStockDataOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CrawleredDataService {

	@Autowired
	AsyncQueryStockDataOperation asyncQueryStockDataOperation;

	public Paging<AsyncQueryHistoricalDataInfoEntity> queryHistoricalData(String cols,Map<String,String> params,Integer page, Integer limit) throws Exception {
		HelpDocListQuery docListQuery= new HelpDocListQuery();
		docListQuery.setPage(page);
		docListQuery.setPageSize(limit);
		docListQuery.setNsrsbh(params.get("nsrsbh"));
		docListQuery.setNsrdq(params.get("nsrdq"));
		docListQuery.setDatasz(params.get("datasz"));
		docListQuery.setRulesz(params.get("rulesz"));
		docListQuery.setSbrq(params.get("sbrq"));
		docListQuery.setSbrq(params.get("sbzt"));
		docListQuery.setObjid(params.get("crawid"));
		docListQuery.setZfbj(params.get("zfbj"));
		docListQuery.setObjid(params.get("id"));
		docListQuery.setType(params.get("type"));
		docListQuery.setStatus(params.get("status"));
		Paging<AsyncQueryHistoricalDataInfoEntity>  paging= asyncQueryStockDataOperation.findHistoricalDataList(cols,docListQuery);
		return paging;
	}

	public Map<String,String> updateStockDataByid(Map<String,String> params) throws Exception {
		return asyncQueryStockDataOperation.saveHistoricalDataById(params);
	}

}
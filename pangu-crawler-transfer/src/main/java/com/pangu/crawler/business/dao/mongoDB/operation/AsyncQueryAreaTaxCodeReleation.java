package com.pangu.crawler.business.dao.mongoDB.operation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Slf4j
public class AsyncQueryAreaTaxCodeReleation {
	@Qualifier("secondary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;
	
	private  static SimpleDateFormat sjc=new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sjt=new SimpleDateFormat("yyyyMMddHHmmss");

	public Paging<AsyncAreaTaxCodeReleationEntity> findTaxCodeReleationDataList(HelpDocListQuery docListQuer) {
		Paging<AsyncAreaTaxCodeReleationEntity> result=queryHelpDocList(docListQuer);
		JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(result.getData()));
		for (int i = 0; i < outData.size(); i++) {
			JSONObject o=outData.getJSONObject(i);
			o.put("xh", i+1);
		}
		result.setJsondata(outData);
		return result;
	}

	public Paging<AsyncAreaTaxCodeReleationEntity> queryHelpDocList(HelpDocListQuery query) {
		try {
			Paging<AsyncAreaTaxCodeReleationEntity> paging = new Paging<>();
			if (null != query.getPage() && null != query.getPageSize()) {
				paging.setPage(query.getPage());
				paging.setPageSize(query.getPageSize());
			}
			Integer page = query.getPage();
			Integer pageSize = query.getPageSize();
			Query queryparams = new Query();
			if (null != query.getNsrdq()&&StringUtils.isNotBlank(query.getNsrdq())) {
				queryparams.addCriteria(Criteria.where("nsrdq").in(query.getNsrdq().split(",")));
			}
			if (null != query.getDatasz()&&StringUtils.isNotBlank(query.getDatasz())) {
				queryparams.addCriteria(Criteria.where("dataszcode").in(query.getDatasz().split(",")));
			}
			if (null != query.getRulesz()&&StringUtils.isNotBlank(query.getRulesz())) {
				queryparams.addCriteria(Criteria.where("ruleszcode").in(query.getRulesz().split(",")));
			}
			if (null != query.getObjid()) {
				queryparams.addCriteria(Criteria.where("_id").is(query.getObjid()));
			}
			long totalCount = 0;
			List<AsyncAreaTaxCodeReleationEntity> resultdata = mongoTemplate.find(queryparams,AsyncAreaTaxCodeReleationEntity.class,"async_area_tax_code_releation");
			if(resultdata!=null&&resultdata.size()>0){
				totalCount=resultdata.size();
			}
			if(page!=null&&pageSize!=null){
				queryparams.skip((long) (page - 1) * pageSize);
				queryparams.limit(pageSize);
			}
			Sort sort = new Sort(Sort.Direction.ASC, "create_time").and(new Sort(Sort.Direction.ASC,"nsrsbh"));
			queryparams.with(sort);
			resultdata = mongoTemplate.find(queryparams,AsyncAreaTaxCodeReleationEntity.class,"async_area_tax_code_releation");
			paging.setData(resultdata);
			paging.setTotalCount(totalCount);
			return paging;
		} catch (Exception e) {
			throw e;
		}
	}


	public Map<String,String>  updateTaxReleationDataByid(Map<String,String> params) {
		Map<String,String> result = new HashMap<>(2);
		try{
			String rule_nsrdq = params.get("rule_nsrdq");
			String rule_szname = params.get("rule_szname");
			String rule_szcode = params.get("rule_szcode");
			String data_szcode = params.get("data_szcode");
			Criteria cxyj=Criteria.where("ruleszcode").is(rule_szcode);
			synchronized(cxyj){
				List<AsyncAreaTaxCodeReleationEntity> dataInfoEntity = mongoTemplate.find(Query.query(cxyj),AsyncAreaTaxCodeReleationEntity.class);
				if(dataInfoEntity.size()>0){
					for(AsyncAreaTaxCodeReleationEntity areaTaxCodeReleationEntity :dataInfoEntity){
						areaTaxCodeReleationEntity.setDataszcode(data_szcode);
						mongoTemplate.save(areaTaxCodeReleationEntity);
					}
				}else{
					AsyncAreaTaxCodeReleationEntity areaTaxCodeReleationEntity = new AsyncAreaTaxCodeReleationEntity();
					areaTaxCodeReleationEntity.setSzname(rule_szname);
					areaTaxCodeReleationEntity.setRuleszcode(rule_szcode);
					areaTaxCodeReleationEntity.setDataszcode(data_szcode);
//				areaTaxCodeReleationEntity.setNsrdq(rule_nsrdq);
					mongoTemplate.save(areaTaxCodeReleationEntity);
				}
				result.put("code","success");
				result.put("message","保存成功");
			}

		}catch (Exception e){
			result.put("code","fail");
			result.put("message","保存失败"+e.getMessage());
		}finally {
			return result;
		}
	}
}


package com.pangu.crawler.business.dao.mongoDB.operation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
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
public class AsyncQueryBusinessTransferRule {
	@Qualifier("secondary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;
	
	private  static SimpleDateFormat sjc=new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sjt=new SimpleDateFormat("yyyyMMddHHmmss");

	public Paging<AsyncBusinessTransferRuleEntity> findTransferDataList(HelpDocListQuery docListQuer) {
		Paging<AsyncBusinessTransferRuleEntity> result=queryHelpDocList(docListQuer);
		JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(result.getData()));
		for (int i = 0; i < outData.size(); i++) {
			JSONObject o=outData.getJSONObject(i);
			o.put("xh", i+1);
		}
		result.setJsondata(outData);
		return result;
	}

	public Paging<AsyncBusinessTransferRuleEntity> queryHelpDocList(HelpDocListQuery query) {
		try {
			Paging<AsyncBusinessTransferRuleEntity> paging = new Paging<>();
			if (null != query.getPage() && null != query.getPageSize()) {
				paging.setPage(query.getPage());
				paging.setPageSize(query.getPageSize());
			}
			Integer page = query.getPage();
			Integer pageSize = query.getPageSize();
			List<AggregationOperation> operations = new ArrayList<>();
			if (null != query.getNsrdq()) {
				operations.add(Aggregation.match(Criteria.where("nsrdq").is(query.getNsrdq())));
			}
			if (null != query.getType()) {
				operations.add(Aggregation.match(Criteria.where("type").is(query.getType())));
			}
			if (null != query.getFormid()) {
				operations.add(Aggregation.match(Criteria.where("formid").is(query.getFormid())));
			}

			if (null != query.getRulesz()) {
				operations.add(Aggregation.match(Criteria.where("sz").is(query.getRulesz())));
			}
			if (null != query.getObjid()) {
				operations.add(Aggregation.match(Criteria.where("_id").is(query.getObjid())));
			}
			if (null != query.getStatus()) {
				operations.add(Aggregation.match(Criteria.where("status").is(query.getStatus())));
			}
			operations.add(Aggregation.match(Criteria.where("iscurrent").is(true)));
			long totalCount = 0;
			//获取满足添加的总页数
			if (null != operations&&operations.size()>0) {
				Aggregation aggregationCount = Aggregation.newAggregation(operations);
				AggregationResults<AsyncBusinessTransferRuleEntity> resultsCount = mongoTemplate.aggregate(aggregationCount, "async_business_transfer_rule", AsyncBusinessTransferRuleEntity.class);
				totalCount = resultsCount.getMappedResults().size();
			} else {
				totalCount = mongoTemplate.count(new Query(), AsyncBusinessTransferRuleEntity.class);
			}
			if(page!=null&&pageSize!=null){
				operations.add(Aggregation.skip((long) (page - 1) * pageSize));
				operations.add(Aggregation.limit(pageSize));
			}
			operations.add(Aggregation.lookup("doc_category", "categoryCode", "code", "category"));
			operations.add(Aggregation.sort(Sort.Direction.DESC, "solveCount"));
			Aggregation aggregation = Aggregation.newAggregation(operations);
			AggregationResults<AsyncBusinessTransferRuleEntity> results = mongoTemplate.aggregate(aggregation, "async_business_transfer_rule", AsyncBusinessTransferRuleEntity.class);
			paging.setData(results.getMappedResults());
			paging.setTotalCount(totalCount);
			return paging;
		} catch (Exception e) {
			throw e;
		}
	}


	public Map<String,String> uploadRuleStatusByid(String id,String rule_status) {
		Map<String,String> result = new HashMap<>(2);
		try{
			if(StringUtils.isBlank(id)){
				throw new RuntimeException("mongodb数据记录id为空!");
			}else{
				AsyncBusinessTransferRuleEntity dataInfoEntity = mongoTemplate.findById(id,AsyncBusinessTransferRuleEntity.class);
				if(dataInfoEntity!=null){
					dataInfoEntity.setStatus(rule_status);
					mongoTemplate.save(dataInfoEntity);
					result.put("code","success");
					result.put("message","成功");
				}else{
					result.put("code","fail");
					result.put("message","没有找到规则!");
				}
			}

		}catch (Exception e){
			result.put("code","fail");
			result.put("message","保存失败"+e.getMessage());
		}finally {
			return result;
		}
	}
	private static Integer lockvariable = 0;//防止操作溢出，枷锁
	public Map<String,String> updateTransferRuleDataByid(List<String> rules,Map<String,String> params) {
		Map<String,String> result = new HashMap<>(2);
		try{
			String primaryid = "";
			String rule_nsrdq = params.get("rule_nsrdq");
			String rule_formid = params.get("rule_formid");
			String rule_sz = params.get("rule_sz");
			String rule_type = params.get("rule_type");
				synchronized(lockvariable){
					Criteria cxyj=Criteria.where("nsrdq").is(rule_nsrdq).and("sz").is(rule_sz).and("type").is(rule_type).and("formid").is(rule_formid).and("iscurrent").is(true);
					List<AsyncBusinessTransferRuleEntity> dataInfoEntity = mongoTemplate.find(Query.query(cxyj),AsyncBusinessTransferRuleEntity.class);
					if(dataInfoEntity.size()>0){
						for(AsyncBusinessTransferRuleEntity data :dataInfoEntity){
							data.setIscurrent(false);
							data.setPrimaryid(data.getId());
							mongoTemplate.save(data);
							primaryid = data.getId();
						}
					}

					AsyncBusinessTransferRuleEntity businessMappingFileEntity = new AsyncBusinessTransferRuleEntity();
					businessMappingFileEntity.setContent(StringUtils.join(rules,"\n"));
					businessMappingFileEntity.setSz(rule_sz);
					businessMappingFileEntity.setSzname("");
					businessMappingFileEntity.setNsrdq(rule_nsrdq);
					businessMappingFileEntity.setFormid(rule_formid);
					businessMappingFileEntity.setType(rule_type);
					businessMappingFileEntity.setFormname("");
					businessMappingFileEntity.setStarttime("1999-12-12");
					businessMappingFileEntity.setEndtime("9999-12-12");
					businessMappingFileEntity.setVersion(sjt.format(new Date()));
					businessMappingFileEntity.setIscurrent(true);
					businessMappingFileEntity.setStatus("0");
					mongoTemplate.save(businessMappingFileEntity);

					cxyj=Criteria.where("primaryid").is(primaryid).and("iscurrent").is(false);
					dataInfoEntity = mongoTemplate.find(Query.query(cxyj),AsyncBusinessTransferRuleEntity.class);
//			dataInfoEntity.addAll(mongoTemplate.find(Query.query(Criteria.where("primaryid").is(primaryid)),AsyncBusinessTransferRuleEntity.class));
					for(AsyncBusinessTransferRuleEntity data :dataInfoEntity){
						data.setPrimaryid(businessMappingFileEntity.getId());
						mongoTemplate.save(data);
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


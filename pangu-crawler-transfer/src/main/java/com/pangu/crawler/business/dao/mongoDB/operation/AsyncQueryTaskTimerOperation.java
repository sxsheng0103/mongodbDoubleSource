package com.pangu.crawler.business.dao.mongoDB.operation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncTaskTimerEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.transfer.service.CommonConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Slf4j
public class AsyncQueryTaskTimerOperation {
	@Qualifier("secondary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;

	@Autowired
	CommonConfigService commonConfigService;
	
	private  static SimpleDateFormat sjc=new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sjt=new SimpleDateFormat("yyyyMMddHHmmss");

	public Paging<AsyncTaskTimerEntity> findTaskTimerDataList(HelpDocListQuery docListQuer) {
		Paging<AsyncTaskTimerEntity> result=queryHelpDocList(docListQuer);
		JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(result.getData()));
		for (int i = 0; i < outData.size(); i++) {
			JSONObject o=outData.getJSONObject(i);
			o.put("xh", i+1);
			try{
				o.put("sznames",StringUtils.join(commonConfigService.getSzMap(o.getString("szs").split(",")).values(),","));
				o.put("dqnames",StringUtils.join(commonConfigService.getDqMap(o.getString("nsrdqs").split(",")).values(),","));
			}catch (Exception e){
				log.error("转换失败");
			}
			o.put("name", o.getString("name"));
			o.put("expressType", o.getString("expressType"));
			o.put("expressContent", o.getString("expressContent"));
			o.put("isdelay", o.getString("isdelay"));
			o.put("delayTime", o.getString("delayTime"));
			o.put("starttime", o.getString("starttime"));
			o.put("endtime", o.getString("endtime"));
			o.put("status", o.getString("status"));
			o.put("status_value", o.getIntValue("status")==1?"运行中":"停止中");
			o.put("expressType_value", o.getIntValue("expressType")==0?"固定频率执行":"固定时间");
			o.put("isdelay_value", (o.getString("isdelay")!=null&&o.getString("isdelay").equals("1"))?"是":"否");
		}
		result.setJsondata(outData);
		return result;
	}

	public Paging<AsyncTaskTimerEntity> queryHelpDocList(HelpDocListQuery query) {
		try {
			Paging<AsyncTaskTimerEntity> paging = new Paging<>();
			if (null != query.getPage() && null != query.getPageSize()) {
				paging.setPage(query.getPage());
				paging.setPageSize(query.getPageSize());
			}
			Integer page = query.getPage();
			Integer pageSize = query.getPageSize();
			Query queryparams = new Query();

			if (null != query.getDatasz()) {
				queryparams.addCriteria(Criteria.where("name").in(query.getDatasz()));
			}
			if (null != query.getObjid()) {
				queryparams.addCriteria(Criteria.where("_id").is(query.getObjid()));
			}
            if (null != query.getStatus()) {
				queryparams.addCriteria(Criteria.where("status").is(query.getStatus()));
            }
			long totalCount = 0;
			List<AsyncTaskTimerEntity> resultdata = mongoTemplate.find(queryparams,AsyncTaskTimerEntity.class,"async_task_timer");
			if(resultdata!=null&&resultdata.size()>0){
				totalCount=resultdata.size();
			}
			if(page!=null&&pageSize!=null){
				queryparams.skip((long) (page - 1) * pageSize);
				queryparams.limit(pageSize);
			}
			Sort sort = new Sort(Sort.Direction.ASC, "create_time").and(new Sort(Sort.Direction.ASC,"nsrsbh"));
			queryparams.with(sort);
			resultdata = mongoTemplate.find(queryparams,AsyncTaskTimerEntity.class,"async_task_timer");
			paging.setData(resultdata);
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
				AsyncTaskTimerEntity dataInfoEntity = mongoTemplate.findById(id,AsyncTaskTimerEntity.class);
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

	public Map<String,String> updateTaskDataByid(Map<String,String> params) {
		Map<String,String> result = new HashMap<>(2);
		try{
			String update = params.get("update");
			String id = params.get("id");
			String name = params.get("name");
			String expressType = params.get("expressType");
			String expressContent = params.get("expressContent");
			String isDelay = params.get("isDelay");
			String taskid = params.get("taskid");
			long delayTime = 0l;//params.get("delayTime")!=null?Long.parseLong(params.get("delayTime")):
			String startTime = params.get("startTime");
			String endTime = params.get("endTime");
			String status = params.get("status");
			String task_nsrdqs = params.get("task_nsrdqs");
			String task_szs = params.get("task_szs");
			if(update.equals("delete")){
				Criteria cxyj=Criteria.where("_id").is(id);
				List<AsyncTaskTimerEntity> dataInfoEntity = mongoTemplate.find(Query.query(cxyj),AsyncTaskTimerEntity.class);
				if(dataInfoEntity.size()>0){
					AsyncTaskTimerEntity task = dataInfoEntity.get(0);
					if(task.getStatus().equals("1")){
						result.put("code","fail");
						result.put("message","该定时任务正在启动中,请先停掉再做修改操作!");
					}else{
						mongoTemplate.remove(task);
						result.put("code","success");
						result.put("message","删除成功!");
					}
				}else{
					result.put("code","fail");
					result.put("message","对应名称定时任务的id没有获取到,请从新操作!");
				}
			}else if(update.equals("update")){
				if(StringUtils.isNotBlank(id)){
					Criteria cxyj=Criteria.where("_id").is(id);
					List<AsyncTaskTimerEntity> dataInfoEntity = mongoTemplate.find(Query.query(cxyj),AsyncTaskTimerEntity.class);
					if(dataInfoEntity.size()>0){
						AsyncTaskTimerEntity task = dataInfoEntity.get(0);
						if(name!=null){
							task.setName(name);
						}
						if(taskid!=null){
							task.setTid(taskid);
						}
						if(expressType!=null){
							task.setExpressType(expressType);
						}
						if(expressContent!=null){
							task.setExpressContent(expressContent);
						}

						if(isDelay!=null){
							task.setIsdelay(isDelay);
						}
						if(delayTime!=0l){
							task.setDelayTime(delayTime);
						}
						if(startTime!=null){
							task.setStarttime(startTime);
						}
						if(endTime!=null){
							task.setEndtime(endTime);
						}
						if(status!=null){
							task.setStatus(status);
						}
						if(task_nsrdqs!=null){
							task.setNsrdqs(task_nsrdqs);
						}
						if(task_szs!=null){
							task.setSzs(task_szs);
						}
						mongoTemplate.save(task);
						result.put("code","success");
						result.put("message","修改成功!");
					}else{
						result.put("code","fail");
						result.put("message","对应名称定时任务的id没有获取到,请从新操作!");
					}
				}else{
					result.put("code","fail");
					result.put("message","对应名称定时任务的id没有获取到,请从新操作!");
				}

			}else{
//				Criteria cxyj=Criteria.where("name").is(name);

				AsyncTaskTimerEntity businessMappingFileEntity = new AsyncTaskTimerEntity();
				businessMappingFileEntity.setName(name);
				businessMappingFileEntity.setExpressType(expressType);
				businessMappingFileEntity.setExpressContent(expressContent);
				businessMappingFileEntity.setIsdelay(isDelay);
				businessMappingFileEntity.setDelayTime(delayTime);
				businessMappingFileEntity.setStarttime(startTime);
				businessMappingFileEntity.setEndtime(endTime);
				businessMappingFileEntity.setStatus("0");
				businessMappingFileEntity.setTid(taskid);
				businessMappingFileEntity.setNsrdqs(task_nsrdqs);
				businessMappingFileEntity.setSzs(task_szs);
				mongoTemplate.save(businessMappingFileEntity);
				result.put("code","success");
				result.put("message","新增成功");
			}
		}catch (Exception e){
			result.put("code","fail");
			result.put("message","操作失败"+e.getMessage());
		}finally {
			return result;
		}
	}

}


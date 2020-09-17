package com.pangu.crawler.business.dao.mongoDB.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.HelpDocListQuery;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.Base64Util;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Slf4j
public class AsyncQueryStockDataOperation {

	@Qualifier("primary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;
	
	static SimpleDateFormat sjc=new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat sjctime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public  void save(AsyncQueryHistoricalDataInfoEntity historicalDataInfoEntity) {
		mongoTemplate.save(historicalDataInfoEntity);
	}

	public Paging<AsyncQueryHistoricalDataInfoEntity> findHistoricalDataList(String cols,HelpDocListQuery docListQuer) {
		Paging<AsyncQueryHistoricalDataInfoEntity> result=queryHelpDocList(cols,docListQuer);
		JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(result.getData()));
		Map<String, String> sbdata = null;
		for (int i = 0; i < outData.size(); i++) {
			JSONObject o=outData.getJSONObject(i);
			o.put("xh", i+1);
			o.put("createTime", o.getLong("createTime")==null?null:sjc.format(o.getLong("createTime")));
			o.put("updateTime", o.getLong("updateTime")==null?null:sjc.format(o.getLong("updateTime")));
			o.put("skssqq", o.getLong("skssqq")==null?null:sjc.format(o.getLong("skssqq")));
			o.put("skssqz", o.getLong("skssqz")==null?null:sjc.format(o.getLong("skssqz")));
			o.put("sbrq", o.getLong("sbrq")==null?null:sjc.format(o.getLong("sbrq")));
			o.put("zhTime", o.getString("zhTime")==null?"":o.getString("zhTime"));
			o.put("dataOut_value", o.getString("dataOut")==null?"":Base64Util.decode(o.getString("dataOut")));
			o.put("result_value", o.getString("result")==null?"":Base64Util.decode(o.getString("result")));
			if(o.get("sbData")!=null){
				try{
					sbdata = (Map<String,String>) JSON.toJSON(o.get("sbData"));
				}catch (Exception e){
					e.printStackTrace();
					log.error(o.get("id")+"转化成JSON格式报文数据出错");//TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf1)+
				}
				for(Map.Entry<String,String> map :sbdata.entrySet()){
					sbdata.put(map.getKey(), Base64Util.decode(map.getValue()).replace("\\",""));
				}
				o.put("sbData_value",JSON.toJSONString(sbdata));
			}else{
				o.put("sbData_value","");
			}
			if(NumberUtils.isNumber(o.getString("xgrq"))) {
				o.put("xgrq", sjc.format(o.getLong("xgrq")));
			}
		}
		result.setJsondata(outData);
		return result;
	}

	public Paging<AsyncQueryHistoricalDataInfoEntity> queryHelpDocList(String cols,HelpDocListQuery query) {
		try {
			Paging<AsyncQueryHistoricalDataInfoEntity> paging = new Paging<>();
			if (null != query.getPage() && null != query.getPageSize()) {
				paging.setPage(query.getPage());
				paging.setPageSize(query.getPageSize());
			}
			Query queryparams = new Query();
			Integer page = query.getPage();
			Integer pageSize = query.getPageSize();
			if (null != query.getNsrsbh()) {
				queryparams.addCriteria(Criteria.where("nsrsbh").is(query.getNsrsbh()));
			}
			if (null != query.getObjid()) {
				queryparams.addCriteria(Criteria.where("_id").is(query.getObjid()));
			}
			if (null != query.getNsrdq()) {
				queryparams.addCriteria(Criteria.where("nsrdq").in(query.getNsrdq().split(",")));
			}
			if (null != query.getDatasz()) {
				queryparams.addCriteria(Criteria.where("szdm").in(query.getDatasz().split(",")));
			}
			if (null != query.getSbrq()) {
				queryparams.addCriteria(Criteria.where("sbrq").is(query.getSbrq()));
			}
			if (null != query.getSbzt()) {
				queryparams.addCriteria(Criteria.where("sbzt").is(query.getSbzt()));
			}
			if (null != query.getZfbj()) {
				queryparams.addCriteria(Criteria.where("zfbj").is(query.getZfbj()));
			}
			if(null != query.getType()){
				queryparams.addCriteria(Criteria.where("dataType").is(query.getType()));
			}
			if(null != query.getStatus()&&!query.getStatus().equals("")){
				queryparams.addCriteria(Criteria.where("zhState").is(Integer.valueOf(query.getStatus())));
			}
			if(cols == null){
				//定时任务
			}else if(StringUtils.isNotBlank(cols)){
				for(String col: cols.split(",")){
					if(StringUtils.isNotBlank(col)){
						queryparams.fields().include(col);
					}
				}
			}else if(cols.equals("")){
				queryparams.fields().include("id").include("createTime").include("updateTime").include("nsrsbh").include("nsrdq").
						include("szdm").include("bbmc").include("bbbh").include("skssqq").include("skssqz")
						.include("sbrq").include("xgrq").include("ytbse").include("sbfs")
						.include("state").include("zfbj").include("dataType").include("sbList")
						.include("zhState").include("zhTime");
			}
            long totalCount = 0;


            List<AsyncQueryHistoricalDataInfoEntity> resultdata = mongoTemplate.find(queryparams,AsyncQueryHistoricalDataInfoEntity.class,"async_query_historical_data_info");
			if(resultdata!=null&&resultdata.size()>0){
                totalCount=resultdata.size();
            }
			if(page!=null&&pageSize!=null){
				queryparams.skip((long) (page - 1) * pageSize);
				queryparams.limit(pageSize);
			}
            Sort sort = new Sort(Sort.Direction.ASC, "create_time").and(new Sort(Sort.Direction.ASC,"nsrsbh"));
            queryparams.with(sort);
			resultdata = mongoTemplate.find(queryparams,AsyncQueryHistoricalDataInfoEntity.class,"async_query_historical_data_info");
			paging.setData(resultdata);
			paging.setTotalCount(totalCount);
			return paging;
		} catch (Exception e) {
			throw e;
		}
	}


	public Map<String,String> saveHistoricalDataById(Map<String,String> params) {
		Map<String,String> result = new HashMap<>(2);
		try{
			String id = params.get("id");
			if(StringUtils.isBlank(id)){
				throw new RuntimeException("mongodb数据记录id为空!");
			}else{
				AsyncQueryHistoricalDataInfoEntity dataInfoEntity = mongoTemplate.findById(id,AsyncQueryHistoricalDataInfoEntity.class);
				if(params.get("data_out")!=null){
					dataInfoEntity.setDataOut(params.get("data_out")==null?"":params.get("data_out"));
				}
				if(params.get("result")!=null){
					dataInfoEntity.setResult(params.get("result")==null?"":params.get("result"));
				}
				if(params.get("zhState")!=null&&!dataInfoEntity.getZhState().equals("0")){
					dataInfoEntity.setZhState(params.get("zhState")==null?101:Integer.valueOf(params.get("zhState")));
				}
				if(params.get("zhTime")!=null){
					dataInfoEntity.setZhTime(params.get("zhTime"));
				}
				mongoTemplate.save(dataInfoEntity);
			}
			result.put("code","success");
			result.put("message","保存成功");
		}catch (Exception e){
			result.put("code","fail");
			result.put("message","保存失败"+e.getMessage());
		}finally {
			return result;
		}
	}
}


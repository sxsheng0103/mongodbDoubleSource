package com.pangu.crawler.business.dao.mongoDB.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryFaPiaoXinXiEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryRegisterRequestEntity;

import lombok.extern.slf4j.Slf4j;
@Repository
@Slf4j
public class AsyncQueryRegisterRequestOperation {
	@Qualifier("primary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;

	@Value("${pangu.servername}")
	private String servername;

	public void save(AsyncQueryRegisterRequestEntity data) {
		data.setServername(servername);
		mongoTemplate.save(data);
	}
//	public void updateStatic(String nsrsbh,String lsh,int state) {
//		mongoTemplate.updateFirst(Query.query(Criteria.where("nsrsbh").is(nsrsbh).and("lsh").is(lsh)), update,
//				AsyncQueryRegisterRequestEntity.class);
//	}
	
	public JSONArray find(Criteria cxyj) {
		List<AsyncQueryRegisterRequestEntity> data=mongoTemplate.find(Query.query(cxyj), AsyncQueryRegisterRequestEntity.class);
		 JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(data));
		 for (int i = 0; i < outData.size(); i++) {
			 JSONObject o=outData.getJSONObject(i);
			 o.remove("id");
//			 o.remove("updateTime");
//			 o.remove("createTime");
		 }
		 return outData;
	}
	
	public Criteria checkAndAssemble(JSONObject data) throws Exception {	
		Criteria cxyj=null;
		for (String key: data.keySet()) {
			if(cxyj==null) {
				cxyj=Criteria.where(key).is(StringEscapeUtils.escapeHtml4(data.getString(key)));
			}else {
				cxyj=cxyj.and(key).is(StringEscapeUtils.escapeHtml4(data.getString(key)));
			}
		}
		return cxyj;
	}
	
	public AsyncQueryRegisterRequestEntity getOneByState(int num) {
		Criteria cxyj=Criteria.where("state").is(num).and("servername").is(servername);
		return mongoTemplate.findOne(Query.query(cxyj), AsyncQueryRegisterRequestEntity.class);
	}
	
	public AsyncQueryRegisterRequestEntity getOneByStateExcludeNsdq(int num,ArrayList<String> excludeNsrdqs) {
		Criteria cxyj=Criteria.where("state").is(num).and("servername").is(servername);
		
		if(excludeNsrdqs!=null&&excludeNsrdqs.size()>0) {
			cxyj=cxyj.and("nsrdq").nin(excludeNsrdqs);
		}
		
		return mongoTemplate.findOne(Query.query(cxyj), AsyncQueryRegisterRequestEntity.class);
	}
	
	public AsyncQueryRegisterRequestEntity getOneByStateAppointNsdq(int num,ArrayList<String> appointNsrdqs) {
		Criteria cxyj=Criteria.where("state").is(num).and("servername").is(servername);
		
		if(appointNsrdqs!=null&&appointNsrdqs.size()>0) {
			cxyj=cxyj.and("nsrdq").in(appointNsrdqs);
		}
		
		return mongoTemplate.findOne(Query.query(cxyj), AsyncQueryRegisterRequestEntity.class);
	}
	
	public AsyncQueryRegisterRequestEntity findByLsh(String lsh) {
		return mongoTemplate.findOne(Query.query(Criteria.where("lsh").is(lsh).and("servername").is(servername)), AsyncQueryRegisterRequestEntity.class);
	}
	
	public AsyncQueryRegisterRequestEntity getOneByStateAndLoginMethod(int state,ArrayList<String> login_method) {
		Criteria cxyj=Criteria.where("login_method").in(login_method).and("state").is(state).and("servername").is(servername);
		List<AsyncQueryRegisterRequestEntity> data=mongoTemplate.find(Query.query(cxyj), AsyncQueryRegisterRequestEntity.class);
		if(data==null||data.size()==0) {
			return null;
		}else {
			return data.get(RandomUtils.nextInt(0, data.size()));
		}
		 
	}
	
	@Transactional
	public HashMap<String,String> batchTask(String password,String customerid,String nsrsbh,
			String login_method,HashMap<String, HashMap<String, String>> signAndRequestparam,
			String nsrdq,int state){
		HashMap<String,String> lshs=new HashMap<String,String>();
		List<AsyncQueryRegisterRequestEntity> data=new ArrayList<AsyncQueryRegisterRequestEntity>();
		for (String key:signAndRequestparam.keySet()) {
			AsyncQueryRegisterRequestEntity a=new AsyncQueryRegisterRequestEntity();
			a.setPassword(password);
			String lsh=UUID.randomUUID().toString().replaceAll("-","");
			a.setLsh(lsh);
			lshs.put(key,lsh);
			
			a.setCustomerid(customerid);
			a.setNsrsbh(nsrsbh);
			a.setLogin_method(login_method);
			a.setSign(key);
			a.setNsrdq(nsrdq);
			a.setRequestParam(signAndRequestparam.get(key));
			a.setState(state);
			
			data.add(a);
		}
		mongoTemplate.insertAll(data);
		
		return lshs;
	}

}

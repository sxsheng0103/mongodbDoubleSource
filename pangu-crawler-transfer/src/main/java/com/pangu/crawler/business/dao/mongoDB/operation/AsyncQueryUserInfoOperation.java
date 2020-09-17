package com.pangu.crawler.business.dao.mongoDB.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryUserInfoEntity;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AsyncQueryUserInfoOperation {
	@Qualifier("primary")
	@Autowired(required = false)
    private MongoTemplate mongoTemplate;
	
	public void saveOrUpdate(JSONObject data) {
		String nsrsbh=data.getString("nsrsbh");
		String customerid=data.getString("customerid");
		
		AsyncQueryUserInfoEntity old=findAsyncQueryUserInfo(nsrsbh, customerid);
		if(old==null) {
			AsyncQueryUserInfoEntity newEntity=new AsyncQueryUserInfoEntity();
			newEntity.setCustomerid(customerid);
			newEntity.setNsrsbh(nsrsbh);
			if(data.containsKey("djxx")) {
				newEntity.setDjxx(Base64Utils.encodeToString(data.getString("djxx").getBytes()));
			}else {
				newEntity.setDjxx(Base64Utils.encodeToString(data.getString("sfzrdxx").getBytes()));
			}
			
			mongoTemplate.save(newEntity);
		}else {
			if(data.containsKey("djxx")) {
				old.setDjxx(Base64Utils.encodeToString(data.getString("djxx").getBytes()));
			}else {
				old.setSfzrdxx(Base64Utils.encodeToString(data.getString("sfzrdxx").getBytes()));
			}
			mongoTemplate.save(old);
		}
		
	}
	
	
	public AsyncQueryUserInfoEntity findAsyncQueryUserInfo(String nsrsbh,String customerid) {
		return mongoTemplate.findOne(Query.query(Criteria.where("nsrsbh").is(nsrsbh).and("customerid").is(customerid)), AsyncQueryUserInfoEntity.class);
	}
}
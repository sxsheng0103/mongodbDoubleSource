package com.pangu.crawler.business.dao.mongoDB.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.NeedLoginEntity;
import com.pangu.crawler.business.service.async.BusinessException;

import lombok.extern.slf4j.Slf4j;
@Repository
@Slf4j
public class NeedLoginOperation {
	@Qualifier("primary")
	@Autowired(required = false)
    private MongoTemplate mongoTemplate;
	
	public synchronized NeedLoginEntity findNoLogin(String customerid,String nsrdq){
		Criteria cxyj=Criteria.where("customerid").in(customerid).and("state").is(1).and("nsrdq").is(nsrdq);
		List<NeedLoginEntity> data=mongoTemplate.find(Query.query(cxyj), NeedLoginEntity.class);
		if(data==null||data.size()==0) {
			return null;
		}else {
			NeedLoginEntity d=data.get(RandomUtils.nextInt(0, data.size()));
			Update update = new Update();
			update.set("state", 2);
			mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(d.getId())),update,NeedLoginEntity.class);
			return d;
		}
	}
	
	
	public void updateState(String lsh,int state) {
		Update update = new Update();
		update.set("state", state);
		mongoTemplate.updateFirst(Query.query(Criteria.where("lsh").is(lsh)),update,NeedLoginEntity.class);
	}
	
	public JSONObject ObjToJson(NeedLoginEntity n){
		JSONObject data=JSONObject.parseObject(JSONObject.toJSONString(n));
		data.remove("id");
		data.remove("updateTime");
		data.remove("createTime");
		data.remove("customerid");
		data.remove("nsrdq");
		data.remove("state");
		if(data.getJSONObject("requestParam")!=null &&!data.getJSONObject("requestParam").isEmpty()) {
			for(String k:data.getJSONObject("requestParam").keySet()) {
				data.put(k, data.getJSONObject("requestParam").getString(k));
			}
			data.remove("requestParam");
		}
		return data;
	}
	
	public boolean LSHexistence(String lsh) {
		Criteria cxyj=Criteria.where("lsh").in(lsh);
		List<NeedLoginEntity> data=mongoTemplate.find(Query.query(cxyj), NeedLoginEntity.class);
		if(data!=null&&!data.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}
	
	public NeedLoginEntity findLSH(String lsh) {
		Criteria cxyj=Criteria.where("lsh").in(lsh);
		List<NeedLoginEntity> data=mongoTemplate.find(Query.query(cxyj), NeedLoginEntity.class);
		if(data!=null&&!data.isEmpty()) {
			return data.get(0);
		}else {
			return null;
		}
	}
	
	
	public void saveNeedLogin(JSONObject jsonObject) {
		NeedLoginEntity n=new NeedLoginEntity();

		n.setLsh(jsonObject.getString("lsh"));
		jsonObject.remove("lsh");
		n.setCustomerid(jsonObject.getString("customerid"));
		jsonObject.remove("customerid");
		n.setNsrsbh(jsonObject.getString("nsrsbh"));
		jsonObject.remove("nsrsbh");
		n.setPassword(jsonObject.getString("password"));
		jsonObject.remove("password");
		n.setNsrdq(jsonObject.getString("nsrdq"));
		jsonObject.remove("nsrdq");
		if(!jsonObject.isEmpty()) {
			HashMap<String,String> d=new HashMap<String,String>();
			for(String k:jsonObject.keySet()) {
				d.put(k, jsonObject.getString(k));
			}
			n.setRequestParam(d);
		}
		
		n.setState(1);

		mongoTemplate.save(n);
	}
	
	public void saveErrorInfo(String lsh,String errorInfo,int state) {
		NeedLoginEntity data=mongoTemplate.findOne(Query.query(Criteria.where("lsh").in(lsh)), NeedLoginEntity.class);
		if(data==null) {
			throw new BusinessException("lsh没有查询到内容");
		}
		data.setState(state);
		data.setErrorInfo(errorInfo);
		mongoTemplate.save(data);
	}
	
	public JSONObject getByNsrsbh(String nsrsbh) {
		List<NeedLoginEntity> data=mongoTemplate.find(Query.query(Criteria.where("nsrsbh").in(nsrsbh)), NeedLoginEntity.class);
		if(data==null||data.isEmpty()) {
			throw new BusinessException("nsrsbh没有查询到内容");
		}
		
		JSONObject outData= JSONObject.parseObject(JSONObject.toJSONString(data.get(data.size()-1)));

		outData.remove("id");
		outData.remove("updateTime");
		outData.remove("createTime");
		return outData;
	}

}

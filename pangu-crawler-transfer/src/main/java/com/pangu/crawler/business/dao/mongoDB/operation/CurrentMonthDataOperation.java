package com.pangu.crawler.business.dao.mongoDB.operation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryRegisterRequestEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.CurrentMonthDataEntity;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CurrentMonthDataOperation {
	@Qualifier("primary")
	@Autowired(required = false)
    private MongoTemplate mongoTemplate;
	
	public void save(String lsh,JSON data) {
		CurrentMonthDataEntity c=new CurrentMonthDataEntity();
		c.setLsh(lsh);
		c.setData(Base64Utils.encodeToString(data.toJSONString().getBytes()));
		LocalDate date=LocalDate.now();
		c.setYf(date.getYear()+"-"+date.getMonthValue());
		mongoTemplate.save(c);
	}
	
	public CurrentMonthDataEntity findByLSH(String lsh) {
		CurrentMonthDataEntity original=mongoTemplate.findOne(Query.query(Criteria.where("lsh").is(lsh)), CurrentMonthDataEntity.class);
//		JSONObject outData= JSONObject.parseObject(JSONObject.toJSONString(original));
//
//		outData.remove("id");
//		outData.remove("createTime");
		return original;
	}
	
	@Transactional
	public ArrayList<String> ClearCurrentMonthData(String yf) {
		List<CurrentMonthDataEntity> original=mongoTemplate.find(Query.query(Criteria.where("yf").is(yf)), CurrentMonthDataEntity.class);
		
		ArrayList<String> deleteLsh=new ArrayList<String>();
		for (CurrentMonthDataEntity i : original) {
			deleteLsh.add(i.getLsh());
		}
		if(deleteLsh.isEmpty()) {
			return null;
		}
//		List<AsyncQueryRegisterRequestEntity>  registers=mongoTemplate.find(Query.query(Criteria.where("lsh").in(deleteLsh)), AsyncQueryRegisterRequestEntity.class);
		
		mongoTemplate.remove(Query.query(Criteria.where("lsh").in(deleteLsh)), AsyncQueryRegisterRequestEntity.class);
		
		mongoTemplate.remove(Query.query(Criteria.where("lsh").in(deleteLsh)), CurrentMonthDataEntity.class);
		return deleteLsh;
	}
	
	

}

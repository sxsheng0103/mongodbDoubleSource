package com.pangu.crawler.business.dao.mongoDB.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryCallRecordEntity;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AsyncQueryCallRecordOperation {
	@Qualifier("primary")
	@Autowired(required = false)
	private static MongoTemplate mongoTemplate;
//    @Autowired
	/*public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}*/
    
    
    public static void saveInfo(String customerid,String nsrsbh,int state,String sign,String returnInfo) {
    	AsyncQueryCallRecordEntity n=new AsyncQueryCallRecordEntity();
    	n.setCustomerid(customerid);
    	n.setNsrsbh(nsrsbh);
    	n.setSign(sign);
    	n.setReturnInfo(returnInfo);
    	n.setState(state);
    	mongoTemplate.save(n);
    }
}

package com.pangu.crawler.business.dao.mongoDB.operation;

import java.util.Arrays;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryErrorEntity;

import lombok.extern.slf4j.Slf4j;
@Repository
@Slf4j
public class AsyncQueryErrorOperation {
	@Qualifier("primary")
	@Autowired(required = false)
	private static MongoTemplate mongoTemplate;
    /*@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
    */
    public static void saveInfo(String lsh,String errorType,HashMap params,Throwable e){
		try {
			StringBuffer error=new StringBuffer();
			if(e.getCause()!=null) {
				error.append(e.getCause().toString());
				error.append(System.getProperty("line.separator"));
				error.append(Arrays.toString(e.getCause().getStackTrace()));
				error.append(System.getProperty("line.separator"));
			}
			error.append(e.toString());
			error.append(System.getProperty("line.separator"));
			error.append(Arrays.toString(e.getStackTrace()));
			saveInfo(lsh,errorType,params, error.toString());
		} catch (Exception e2) {
		log.error("AsyncQueryErrorOperation储存:发生意外:",e2);
		}
	}


	public static void saveInfo(String lsh, String errorType,HashMap params ,String errorInfo){
		AsyncQueryErrorEntity o=new AsyncQueryErrorEntity();
		o.setErrorType(errorType);
		o.setLsh(lsh);
		if(params!=null&&params.size()>0) {
			o.setParams(params);
		}
		o.setErrorInfo(errorInfo);
		
		mongoTemplate.save(o);
	}
}

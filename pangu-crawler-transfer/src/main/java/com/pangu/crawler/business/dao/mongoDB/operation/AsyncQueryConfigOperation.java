package com.pangu.crawler.business.dao.mongoDB.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryConfigEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AsyncQueryConfigOperation {
	@Qualifier("primary")
	@Autowired(required = false)
	private static MongoTemplate mongoTemplate;
   /* @Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
    */
    static JSONObject ASYNCQUERYCONFIG=new JSONObject();
    
    
    public static void updateCache() {
    	for (AsyncQueryConfigEntity i : mongoTemplate.findAll(AsyncQueryConfigEntity.class)) {
    		if(i.getValue().startsWith("[")) {
    			ASYNCQUERYCONFIG.put(i.getKey(), JSON.parseArray(i.getValue()));
    		}else if (i.getValue().startsWith("{")) {
    			ASYNCQUERYCONFIG.put(i.getKey(), JSON.parseObject(i.getValue()));
			}else {
				ASYNCQUERYCONFIG.put(i.getKey(), i.getValue());
			}
    		
		}
    }
    
    public static JSONArray getList(String key) {
    	if(ASYNCQUERYCONFIG.containsKey(key)) {
    		return ASYNCQUERYCONFIG.getJSONArray(key);
    	}else {
    		updateCache();
    		return ASYNCQUERYCONFIG.getJSONArray(key);
		}
    }
    
    public static JSONObject getJSONObject(String key) {
    	if(ASYNCQUERYCONFIG.containsKey(key)) {
    		return ASYNCQUERYCONFIG.getJSONObject(key);
    	}else {
    		updateCache();
    		return ASYNCQUERYCONFIG.getJSONObject(key);
		}
    }
    
    public static String getString(String key) {
    	if(ASYNCQUERYCONFIG.containsKey(key)) {
    		return ASYNCQUERYCONFIG.getString(key);
    	}else {
    		updateCache();
    		return ASYNCQUERYCONFIG.getString(key);
		}
    }

}

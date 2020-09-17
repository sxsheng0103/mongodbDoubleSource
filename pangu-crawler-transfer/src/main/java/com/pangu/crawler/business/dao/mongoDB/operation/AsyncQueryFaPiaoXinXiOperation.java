package com.pangu.crawler.business.dao.mongoDB.operation;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryFaPiaoXinXiEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AsyncQueryFaPiaoXinXiOperation {
	
	public static SimpleDateFormat sjc=new SimpleDateFormat("yyyy-MM-dd");
	@Qualifier("primary")
	@Autowired(required = false)
    private MongoTemplate mongoTemplate;
	
	public void save(JSONArray data) {
		for (int i = 0; i < data.size(); i++) {
			AsyncQueryFaPiaoXinXiEntity o=JSONObject.toJavaObject(data.getJSONObject(i), AsyncQueryFaPiaoXinXiEntity.class);
			List<AsyncQueryFaPiaoXinXiEntity> old=mongoTemplate.find(Query.query(
					Criteria.where("fphm").is(o.getFphm()).and("fpdm").is(o.getFpdm())), AsyncQueryFaPiaoXinXiEntity.class);
			if(old==null||old.size()==0) {
				
				mongoTemplate.save(o);
			}else {
				AsyncQueryFaPiaoXinXiEntity n=old.get(0);
				if(StringUtils.isNotBlank(o.getFpje())) {
					n.setFpje(o.getFpje());
				}
				if(StringUtils.isNotBlank(o.getSe())) {
					n.setSe(o.getSe());
				}
				if(StringUtils.isNotBlank(o.getGfmc())) {
					n.setGfmc(o.getGfmc());
				}
				mongoTemplate.save(n);
			}
			
		}
		
	}
	
	public Criteria checkAndAssemble(String xfsh,String gfsh,String fplx,
			String kprqq,String kprqz) throws Exception {	
		
		Criteria cxyj=Criteria.where("fplx").is(StringEscapeUtils.escapeHtml4(fplx))
				.andOperator(Criteria.where("kprq").lte(sjc.parse(kprqz).getTime()),Criteria.where("kprq").gte(sjc.parse(kprqq).getTime()));	
		if(StringUtils.isNotBlank(gfsh)) {
			cxyj=cxyj.and("gfsh").is(StringEscapeUtils.escapeHtml4(gfsh));
		}else {
			cxyj=cxyj.and("xfsh").is(StringEscapeUtils.escapeHtml4(xfsh));
		}
		
		return cxyj;
	}
	
	
	public JSONArray find(Criteria cxyj) {
		 List<AsyncQueryFaPiaoXinXiEntity>  data=mongoTemplate.find(Query.query(cxyj), AsyncQueryFaPiaoXinXiEntity.class);
		 JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(data));
		 for (int i = 0; i < outData.size(); i++) {
			 JSONObject o=outData.getJSONObject(i);
			 o.remove("id");
			 o.remove("updateTime");
			 o.remove("createTime");
			 o.put("kprq", sjc.format(o.getLong("kprq")));
		 }
		 return outData;
	}
	
	
	

}

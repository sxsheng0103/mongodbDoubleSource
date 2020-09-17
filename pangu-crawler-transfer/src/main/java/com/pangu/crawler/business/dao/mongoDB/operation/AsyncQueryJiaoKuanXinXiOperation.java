package com.pangu.crawler.business.dao.mongoDB.operation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryJiaoKuanXinXiEntity;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AsyncQueryJiaoKuanXinXiOperation {

	@Qualifier("primary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;
	
	static SimpleDateFormat sjc=new SimpleDateFormat("yyyy-MM-dd");

	public void save(JSONArray data) {
		for (int i = 0; i < data.size(); i++) {
			AsyncQueryJiaoKuanXinXiEntity o=JSONObject.toJavaObject(data.getJSONObject(i),AsyncQueryJiaoKuanXinXiEntity.class);
			List<AsyncQueryJiaoKuanXinXiEntity>  old=mongoTemplate.find(Query.query(
					Criteria.where("nsrsbh").is(o.getNsrsbh())
					.and("dzsph").is(o.getDzsph())
					.and("zsxm").is(o.getZsxm())
					.and("zspm").is(o.getZspm())
					.and("skssqq").is(o.getSkssqq())
					.and("skssqz").is(o.getSkssqz())
					),AsyncQueryJiaoKuanXinXiEntity.class);

			if(old==null||old.size()==0) {
				mongoTemplate.save(o);
			}else {
				AsyncQueryJiaoKuanXinXiEntity n=old.get(0);
				n.setJksj(o.getJksj());
				n.setSe(o.getSe());
				n.setKkzt(o.getKkzt());
				n.setKkfs(o.getKkfs());
				mongoTemplate.save(n);
			}
		}
	}

	public Criteria checkAndAssemble(String nsrsbh,String jkfqrq,String jkfqrz) throws ParseException {
		Criteria cxyj=Criteria.where("nsrsbh").is(StringEscapeUtils.escapeHtml4(nsrsbh))
				.andOperator(
						Criteria.where("jksj").lte(sjc.parse(jkfqrz).getTime()),
						Criteria.where("jksj").gte(sjc.parse(jkfqrq).getTime())
								);
		return cxyj;

	}
	
	public JSONArray findList(Criteria cxyj) {
		List<AsyncQueryJiaoKuanXinXiEntity> data=mongoTemplate.find(Query.query(cxyj), AsyncQueryJiaoKuanXinXiEntity.class);
		JSONArray outData= JSONArray.parseArray(JSONArray.toJSONString(data));
		 for (int i = 0; i < outData.size(); i++) {
			 JSONObject o=outData.getJSONObject(i);
			 o.remove("id");
			 o.remove("updateTime");
			 o.remove("createTime");
			 //o.put("skssqq", sjc.format(o.getLong("skssqq")));
		 }
		 
		 return outData;
	}

}

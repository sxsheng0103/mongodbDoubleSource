package com.pangu.crawler.transfer.sbptpicture.mongo;

import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class AsyncQueryBusinessPictureOperation {

	@Qualifier("secondary")
	@Autowired(required = false)
	private MongoTemplate mongoTemplate;

	public Map<String,Object> save(Map<String, String> params) {
		Map<String,Object> result = new HashMap<String,Object>(2);
		AsyncQueryBusinessPictureEntity businessPictureEntity = new AsyncQueryBusinessPictureEntity();
		businessPictureEntity.setCreateTime(TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf1));
		businessPictureEntity.setJglx(params.get("jglx"));
		businessPictureEntity.setName(params.get("name"));
		businessPictureEntity.setLsh(params.get("lsh"));
		businessPictureEntity.setReleationid(params.get("releationid"));
		businessPictureEntity.setScreenbase64(params.get("screenbase64"));
		businessPictureEntity.setIp(params.get("ip"));
		businessPictureEntity.setComputername(params.get("computername"));
		mongoTemplate.save(businessPictureEntity);
		result.put("code","success");
		result.put("message","保存成功!");
		result.put("data",businessPictureEntity);
		return result;
	}

	
	public List<AsyncQueryBusinessPictureEntity> findByParam(Criteria cxyj) {
		List<AsyncQueryBusinessPictureEntity> data=mongoTemplate.find(Query.query(cxyj), AsyncQueryBusinessPictureEntity.class);
		return data;
	}

}

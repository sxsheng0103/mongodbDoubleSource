package com.pangu.crawler.sbptpicture.mongo;

import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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
		try{
			AsyncQueryBusinessPictureEntity businessPictureEntity = new AsyncQueryBusinessPictureEntity();
			businessPictureEntity.setCreateTime(TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf1));
			businessPictureEntity.setIp(params.get("ip"));
			businessPictureEntity.setSz(params.get("sz"));
			businessPictureEntity.setLsh(params.get("lsh"));
			businessPictureEntity.setJglx(params.get("jglx"));
			businessPictureEntity.setName(params.get("name"));
			businessPictureEntity.setNsrsbh(params.get("nsrsbh"));
			businessPictureEntity.setNsrsbh(params.get("nsrdq"));
			businessPictureEntity.setNsrmc(params.get("nsrmc"));
			businessPictureEntity.setBusiness(params.get("business"));
			businessPictureEntity.setReleationid(params.get("releationid"));
			businessPictureEntity.setScreenbase64(params.get("screenbase64"));
			businessPictureEntity.setComputername(params.get("computername"));
			mongoTemplate.save(businessPictureEntity);
			result.put("code","success");
			result.put("message","保存成功!");
			businessPictureEntity.setScreenbase64("length:"+(params.get("screenbase64")==null?"0":params.get("screenbase64").length()));
		}catch (DataAccessResourceFailureException e){
            result.put("code","fail");
            result.put("message","保存失败,服务调用数据库超时!"+e.getMessage());
        }catch (Exception e){
			result.put("code","fail");
			result.put("message","保存失败!"+e.getMessage());
		}
//		result.put("data",businessPictureEntity);
		return result;
	}

	
	public List<AsyncQueryBusinessPictureEntity> findByParam(Criteria cxyj) {
	    try{
            List<AsyncQueryBusinessPictureEntity> data=mongoTemplate.find(Query.query(cxyj), AsyncQueryBusinessPictureEntity.class);
            return data;
        }catch (QueryTimeoutException e){
            log.error("查询申报图片相关结果超时!");
            return null;
        }
	}

}

package com.pangu.crawler.business.dao.mongoDB.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "async_call_record_config")
public class AsyncQueryCallRecordEntity {
	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;

	@Field("create_time")
	@CreatedDate
	@ApiModelProperty(value = "录入时间", hidden = true)
	public Long createTime;

	@Field("update_time")
	@LastModifiedDate
	@ApiModelProperty(value = "修改时间", hidden = true)
	public Long updateTime;
	
	@Field("nsrsbh")
	@ApiModelProperty(value = "nsrsbh", hidden = true)
	public String nsrsbh;
	
	@Field("customerid")
	@ApiModelProperty(value = "fuxi传来的客户id", hidden = true)
	public String customerid;
	
	@Field("sign")
	@ApiModelProperty(value = "接口url", hidden = true)
	public String sign;
	
	@Field("returnInfo")
	@ApiModelProperty(value = "返回信息", hidden = true)
	public String returnInfo;
	
	@Field("state")
	@ApiModelProperty(value = "通保存请求中的一样", hidden = true)
	public int state;
}

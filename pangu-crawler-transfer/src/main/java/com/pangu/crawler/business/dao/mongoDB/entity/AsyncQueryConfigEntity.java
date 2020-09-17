package com.pangu.crawler.business.dao.mongoDB.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "async_query_config")
public class AsyncQueryConfigEntity {
	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;

	@Field("create_time")
	@CreatedDate
	@ApiModelProperty(value = "录入时间", hidden = true)
	public Long createTime;

	@Field("key")
	@ApiModelProperty(value = "配置名称", hidden = true)
	public String key;
	
	@Field("value")
	@ApiModelProperty(value = "配置的值", hidden = true)
	public String value;
	
}

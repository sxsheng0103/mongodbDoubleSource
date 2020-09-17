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
@Document(collection = "async_query_errorInfo")
public class AsyncQueryErrorEntity {
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
	
	@Field("lsh")
	@ApiModelProperty(value = "lsh", hidden = true)
	public String lsh;
	
	@Field("params")
	@ApiModelProperty(value = "params", hidden = true)
	public HashMap params;
	
	@Field("errorType")
	@ApiModelProperty(value = "errorType", hidden = true)
	public String errorType;
	
	@Field("errorInfo")
	@ApiModelProperty(value = "errorInfo", hidden = true)
	public String errorInfo;

}

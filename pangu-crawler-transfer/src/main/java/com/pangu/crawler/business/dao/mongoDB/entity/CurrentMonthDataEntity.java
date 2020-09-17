package com.pangu.crawler.business.dao.mongoDB.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "current_month_data")
public class CurrentMonthDataEntity {

	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;

	@Field("create_time")
	@CreatedDate
	@ApiModelProperty(value = "录入时间", hidden = true)
	public Long createTime;
	
	@Field("lsh")
	@ApiModelProperty(value = "查询用流水号", hidden = true)
	public String lsh;
	
	@Field("data")
	@ApiModelProperty(value = "数据", hidden = true)
	public String data;
	
	@Field("yf")
	@ApiModelProperty(value = "月份 格式2020-03", hidden = true)
	public String yf;
	

}

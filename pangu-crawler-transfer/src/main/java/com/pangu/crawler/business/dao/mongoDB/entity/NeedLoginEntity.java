package com.pangu.crawler.business.dao.mongoDB.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.DBObject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "need_login")
public class NeedLoginEntity{
	
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
	@ApiModelProperty(value = "查询用流水号", hidden = true)
	public String lsh;
	
	@Field("customerid")
	@ApiModelProperty(value = "客户id", hidden = true)
	public String customerid;

	@Field("nsrsbh")
	@ApiModelProperty(value = "nsrsbh", hidden = true)
	public String nsrsbh;
	
	@Field("password")
	@ApiModelProperty(value = "密码", hidden = true)
	public String password;
	
	@Field("nsrdq")
	@ApiModelProperty(value = "地区", hidden = true)
	public String nsrdq;
	
	@Field("request_param")
	@ApiModelProperty(value = "请求参数", hidden = true)
	public HashMap<String, String> requestParam;

	@Field("state")
	@ApiModelProperty(value = "处理状态1没有被拿过2已经拿过", hidden = true)
	public int state;
	
	@Field("errorInfo")
	@ApiModelProperty(value = "异常信息", hidden = true)
	public String errorInfo;

}

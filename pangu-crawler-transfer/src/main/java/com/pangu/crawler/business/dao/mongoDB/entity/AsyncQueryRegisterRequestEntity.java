package com.pangu.crawler.business.dao.mongoDB.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@Document(collection = "async_query_register_request")
public class AsyncQueryRegisterRequestEntity {
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

	@Field("servername")
	@ApiModelProperty(value = "服务器名称", hidden = true)
	public String servername;
	
	@Field("password")
	@ApiModelProperty(value = "密码", hidden = true)
	public String password;
	
	@Field("lsh")
	@ApiModelProperty(value = "查询用流水号", hidden = true)
	public String lsh;
	
	@Field("customerid")
	@ApiModelProperty(value = "fuxi传来的客户id", hidden = true)
	public String customerid;

	@Field("nsrsbh")
	@ApiModelProperty(value = "nsrsbh", hidden = true)
	public String nsrsbh;
	
	@Field("login_method")
	@ApiModelProperty(value = "登陆方法", hidden = true)
	public String login_method;

	@Field("sign")
	@ApiModelProperty(value = "接口url", hidden = true)
	public String sign;

	@Field("nsrdq")
	@ApiModelProperty(value = "地区", hidden = true)
	public String nsrdq;

	@Field("request_param")
	@ApiModelProperty(value = "请求参数", hidden = true)
	public HashMap<String, String> requestParam;

	@Field("state")
	@ApiModelProperty(value = "处理状态1等待检查cookie2等待登陆3处理登陆4登陆完成5开始处理任务6处理完成   10为失败", hidden = true)
	public int state;
	
	@Field("errorInfo")
	@ApiModelProperty(value = "异常信息", hidden = true)
	public String errorInfo;
	

}

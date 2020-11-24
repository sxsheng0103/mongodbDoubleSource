package com.pangu.crawler.business.dao.mongoDB.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
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
	
	@Field("liantongpingtai_lsh")
	@ApiModelProperty(value = "联通平台lsh", hidden = true)
	public String liantongpingtai_lsh;

	@Field("request_param")
	@ApiModelProperty(value = "请求参数", hidden = true)
	public HashMap<String, String> requestParam;

	@Field("state")
	@ApiModelProperty(value = "处理状态1等待检查cookie2等待登陆3调用其他接口进行登录4等待其他接口登录完毕或者本地登录5登陆完成6开始处理任务7处理完成   10为失败", hidden = true)
	public int state;
	
	@Field("errorInfo")
	@ApiModelProperty(value = "异常信息", hidden = true)
	public String errorInfo;
	

}

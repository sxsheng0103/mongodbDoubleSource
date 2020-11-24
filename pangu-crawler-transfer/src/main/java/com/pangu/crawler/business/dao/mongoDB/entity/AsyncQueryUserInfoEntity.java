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
@Document(collection = "async_query_user_info")
public class AsyncQueryUserInfoEntity {
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
	
	@Field("customerid")
	@ApiModelProperty(value = "fuxi传来的客户id", hidden = true)
	public String customerid;

	@Field("nsrsbh")
	@ApiModelProperty(value = "nsrsbh", hidden = true)
	public String nsrsbh;
	
	@Field("djxx")
	@ApiModelProperty(value = "登记信息", hidden = true)
	public String djxx;
	
	@Field("sfzrdxx")
	@ApiModelProperty(value = "税费认定信息", hidden = true)
	public String sfzrdxx;
	
	@Field("fphd")
	@ApiModelProperty(value = "发票核定", hidden = true)
	public String fphd;
	
	@Field("zgxx")
	@ApiModelProperty(value = "资格信息", hidden = true)
	public String zgxx;
	
	@Field("hdzs")
	@ApiModelProperty(value = "核定征收", hidden = true)
	public String hdzs;
	
}

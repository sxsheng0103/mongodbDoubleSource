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
@Document(collection = "async_query_fa_piao_xin_xi")
public class AsyncQueryFaPiaoXinXiEntity {
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

	@Field("fplx")
	@ApiModelProperty(value = "发票类型", hidden = true)
	public String fplx;

	@Field("kprq")
	@ApiModelProperty(value = "开票日期", hidden = true)
	public Long kprq;
	
	@Field("fpdm")
	@ApiModelProperty(value = "发票代码", hidden = true)
	public String fpdm;
	
	@Field("wlfph")
	@ApiModelProperty(value = "网络发票号(电子发票号)", hidden = true)
	public String wlfph;
	
	@Field("fphm")
	@ApiModelProperty(value = "发票号码", hidden = true)
	public String fphm;
	
	@Field("fpje")
	@ApiModelProperty(value = "发票金额", hidden = true)
	public String fpje;
	
	@Field("sl")
	@ApiModelProperty(value = "税率", hidden = true)
	public String sl;
	
	@Field("se")
	@ApiModelProperty(value = "发票税额", hidden = true)
	public String se;
	
	@Field("gfmc")
	@ApiModelProperty(value = "购方名称", hidden = true)
	public String gfmc;
	
	@Field("gfsh")
	@ApiModelProperty(value = "购方税号", hidden = true)
	public String gfsh;
	
	@Field("xfsh")
	@ApiModelProperty(value = "销方税号", hidden = true)
	public String xfsh;
	
	@Field("xfmc")
	@ApiModelProperty(value = "销方名称", hidden = true)
	public String xfmc;

	@Field("fpzt")
	@ApiModelProperty(value = "发票状态", hidden = true)
	public String fpzt;
}

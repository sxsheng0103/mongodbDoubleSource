package com.pangu.crawler.sbptpicture.mongo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "async_query_businesspicture")
public class AsyncQueryBusinessPictureEntity {
	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;

	@Field("create_time")
	@CreatedDate
	@ApiModelProperty(value = "录入时间", hidden = true)
	public String createTime;
	
	@Field("lsh")
	@ApiModelProperty(value = "流水号", hidden = true)
	public String lsh;

	@Field("nsrsbh")
	@ApiModelProperty(value = "纳税人编号", hidden = true)

	public String nsrsbh;

	@Field("sz")
	@ApiModelProperty(value = "税种", hidden = true)
	public String sz;

	@Field("business")
	@ApiModelProperty(value = "业务类型", hidden = true)
	public String business;

	@Field("jglx")
	@ApiModelProperty(value = "结果类型", hidden = true)
	public String jglx;

	@Field("releationid")
	@ApiModelProperty(value = "关系id", hidden = true)
	public String releationid;

	@Field("screenbase64")
	@ApiModelProperty(value = "图片base64加密", hidden = true)
	public String screenbase64;

	@Field("name")
	@ApiModelProperty(value = "名称", hidden = true)
	public String name;

	@Field("ip")
	@ApiModelProperty(value = "ip", hidden = true)
	public String ip;

	@Field("computername")
	@ApiModelProperty(value = "机器名称", hidden = true)
	public String computername;
}

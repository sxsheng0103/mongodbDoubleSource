package com.pangu.crawler.business.dao.mongoDB.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 报文转换规则
 */
@Data
@Document(collection = "async_business_transfer_rule")
public class AsyncBusinessTransferRuleEntity {

	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;


	@Field("nsrdq")
	@ApiModelProperty(value = "纳税人地区", hidden = true)
	public String nsrdq;

	@Field("sz")
	@ApiModelProperty(value = "税种编码", hidden = true)
	public String sz;

	@Field("szname")
	@ApiModelProperty(value = "税种名称", hidden = true)
	public String szname;


	@Field("formid")
	@ApiModelProperty(value = "表单ID", hidden = true)
	public String formid;

	@Field("formname")
	@ApiModelProperty(value = "表单名称", hidden = true)
	public String formname;


	@Field("content")
	@ApiModelProperty(value = "文件内容", hidden = true)
	public String content;


	@Field("starttime")
	@ApiModelProperty(value = "开始时间", hidden = true)
	public String starttime;

	@Field("endtime")
	@ApiModelProperty(value = "结束时间", hidden = true)
	public String endtime;

	@Field("version")
	@ApiModelProperty(value = "版本号", hidden = true)
	public String version;

	@Field("iscurrent")
	@ApiModelProperty(value = "当前版本", hidden = true)
	public boolean iscurrent;

	@Field("type")
	@ApiModelProperty(value = "解析类型(0,html;1,json;2;pdf)", hidden = true)
	public String type;

	@Field("status")
	@ApiModelProperty(value = "启用状态", hidden = true)
	public String status;

}

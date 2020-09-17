package com.pangu.crawler.business.dao.mongoDB.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 报文转换规则
 */
@Data
@Document(collection = "async_area_tax_code_releation")
public class AsyncAreaTaxCodeReleationEntity {
	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;

	@Field("nsrdq")
	@ApiModelProperty(value = "纳税人地区", hidden = true)
	public String nsrdq;

	@Field("szname")
	@ApiModelProperty(value = "税种名称", hidden = true)
	public String szname;

	@Field("ruleszcode")
	@ApiModelProperty(value = "规则税种编码", hidden = true)
	public String ruleszcode;

	@Field("dataszcode")
	@ApiModelProperty(value = "数据税种编码", hidden = true)
	public String dataszcode;

}

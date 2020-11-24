package com.pangu.crawler.business.dao.mongoDB.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@Document(collection = "async_query_historical_data_info")
public class AsyncQueryHistoricalDataInfoEntity {
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

	@Field("nsrsbh")
	@ApiModelProperty(value = "nsrsbh", hidden = true)
	public String nsrsbh;

	@Field("nsrdq")
	@ApiModelProperty(value = "地区", hidden = true)
	public String nsrdq;

	@Field("szdm")
	@ApiModelProperty(value = "税种", hidden = true)
	public String szdm;

	@Field("bbmc")
	@ApiModelProperty(value = "报表名称", hidden = true)
	public String bbmc;
	
	@Field("bbbh")
	@ApiModelProperty(value = "报表编号", hidden = true)
	public String bbbh;


	@Field("skssqq")//不可以包含时分秒2020-03-14
	@ApiModelProperty(value = "税款所属期起", hidden = true)
	public Long skssqq;

	@Field("skssqz")//不可以包含时分秒2020-03-14
	@ApiModelProperty(value = "税款所属期止", hidden = true)
	public Long skssqz;

	@Field("sbrq")//不可以包含时分秒2020-03-14
	@ApiModelProperty(value = "申报日期", hidden = true)
	public Long sbrq;

	@Field("xgrq")//不可以包含时分秒2020-03-14
	@ApiModelProperty(value = "修改日期", hidden = true)
	public Long xgrq;
	
	@Field("ybtse")
	@ApiModelProperty(value = "应补退税额", hidden = true)
	public String ybtse;

	@Field("sbfs")
	@ApiModelProperty(value = "申报方式", hidden = true)
	public String sbfs;

	@Field("state")
	@ApiModelProperty(value = "申报状态", hidden = true)
	public String state;

	@Field("zfbj")
	@ApiModelProperty(value = "作废标记", hidden = true)
	public String zfbj;

	@Field("dataType")
	@ApiModelProperty(value = "数据类型", hidden = true)
	public String dataType;

	@Field("sbList")
	@ApiModelProperty(value = "存放报表名称", hidden = true)
	public ArrayList<String> sbList;

	@Field("sbData")
	@ApiModelProperty(value = "存放报表", hidden = true)
	public HashMap<String, String> sbData;

	@Field("dataOut")
	@ApiModelProperty(value = "存放转换完成的统一报文", hidden = true)
	public String dataOut;

	@Field("zhState")
	@ApiModelProperty(value = "转换报文的状态,0不需要转换1未转换2正在转换3转换完成100以上为错误", hidden = true)
	public Integer zhState;

	@Field("result")
	@ApiModelProperty(value = "转化结果", hidden = true)
	public String result;

	@Field("zhTime")
	@ApiModelProperty(value = "转化时间", hidden = true)
	public String zhTime;

}

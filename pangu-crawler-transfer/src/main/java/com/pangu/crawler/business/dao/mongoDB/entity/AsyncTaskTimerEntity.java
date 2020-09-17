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
@Document(collection = "async_task_timer")
public class AsyncTaskTimerEntity {

	@Id
	@ApiModelProperty(value = "Object_Id主键", hidden = true)
	public String id;

	@Field("name")
	@ApiModelProperty(value = "任务名称", hidden = true)
	public String name;

	@Field("tid")
	@ApiModelProperty(value = "任务id", hidden = true)
	public String tid;

	@Field("expressType")
	@ApiModelProperty(value = "执行类型", hidden = true)
	public String expressType;

	@Field("expressContent")
	@ApiModelProperty(value = "执行周期", hidden = true)
	public String expressContent;

	@Field("nsrdqs")
	@ApiModelProperty(value = "地区", hidden = true)
	public String nsrdqs;

	@Field("szs")
	@ApiModelProperty(value = "税种", hidden = true)
	public String szs;

	@Field("是否推迟")
	@ApiModelProperty(value = "是否推迟", hidden = true)
	public String isdelay;

	@Field("delayTime")
	@ApiModelProperty(value = "推迟时间", hidden = true)
	public long delayTime;


	@Field("starttime")
	@ApiModelProperty(value = "开始时间", hidden = true)
	public String starttime;

	@Field("endtime")
	@ApiModelProperty(value = "结束时间", hidden = true)
	public String endtime;

	@Field("status")
	@ApiModelProperty(value = "启用状态", hidden = true)
	public String status;

}

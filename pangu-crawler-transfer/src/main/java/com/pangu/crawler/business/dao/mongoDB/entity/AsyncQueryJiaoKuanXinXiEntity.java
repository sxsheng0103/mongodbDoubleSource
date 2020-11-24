package com.pangu.crawler.business.dao.mongoDB.entity;

import java.util.HashMap;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "async_query_jiao_kuan_xin_xi")
public class AsyncQueryJiaoKuanXinXiEntity {
	
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
	
	@Field("dzsph")
	@ApiModelProperty(value = "电子税票号", hidden = true)
	public String dzsph;
	
	@Field("yzpz")
	@ApiModelProperty(value = "应征凭证", hidden = true)
	public String yzpz;

	@Field("zsxm")
	@ApiModelProperty(value = "征收项目", hidden = true)
	public String zsxm;

	@Field("zspmdm")
	@ApiModelProperty(value = "征收品目代码", hidden = true)
	public String zspmdm;
	
	@Field("zsxmdm")
	@ApiModelProperty(value = "征收项目代码", hidden = true)
	public String zsxmdm;
	
	@Field("zspm")
	@ApiModelProperty(value = "征收品目", hidden = true)
	public String zspm;
	
	@Field("skssqq")
	@ApiModelProperty(value = "税款所属时期起", hidden = true)
	public String skssqq;
	
	@Field("skssqz")
	@ApiModelProperty(value = "税款所属时期止", hidden = true)
	public String skssqz;

	@Field("jkqx")
	@ApiModelProperty(value = "缴款期限", hidden = true)
	public String jkqx;

	@Field("jksj")
	@ApiModelProperty(value = "缴款时间", hidden = true)
	public Long jksj;

	@Field("se")
	@ApiModelProperty(value = "税额", hidden = true)
	public String se;

	@Field("sjje")
	@ApiModelProperty(value = "实缴金额", hidden = true)
	public String sjje;
	
	@Field("sl")
	@ApiModelProperty(value = "税率", hidden = true)
	public String sl;
	
	@Field("jsyj")
	@ApiModelProperty(value = "计税依据", hidden = true)
	public String jsyj;

	@Field("kkfs")
	@ApiModelProperty(value = "扣款方式", hidden = true)
	public String kkfs;

	@Field("kkzt")
	@ApiModelProperty(value = "扣款状态", hidden = true)
	public String kkzt;

	@Field("sksxmc")
	@ApiModelProperty(value = "税款属性名称", hidden = true)
	public String sksxmc;

	@Field("skzlmc")
	@ApiModelProperty(value = "税款种类名称", hidden = true)
	public String skzlmc;

}

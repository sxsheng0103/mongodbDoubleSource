package com.pangu.unicom.base.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SzEnum implements BaseSz {
    // 附加税
    fjs("fjs", FjsFormItemEnum.values(), "doc.fjsData.sbList.", "doc.fjsData.", true),
    // 增值税一般纳税人
    zzsybnsr("zzsybnsr", ZzsybnsrFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 增值税小规模查账征收
    zzsxgmczzs("zzsxgmczzs", ZzsxgmczzsFormEnum.values(), "doc.xgmData.sbList.", "doc.xgmData.", true),
    // 附加税小规模
    fjsxgm("fjsxgm", FjsxgmFormEnum.values(), "doc.xgmData.sbList.", "doc.fjsData.", true),
    // 增值税小规模核定征收
    zzsxgmhdzs("zzsxgmhdzs", ZzsxgmczzsFormEnum.values(), "doc.xgmData.sbList.", "doc.xgmData.", true),
    // 企业所得税A月季报
    qysdsayjb("qysdsayjb", QysdsayjbFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 财务报表（企业会计制度）
    cwbbqykjzd("cwbbqykjzd", CwbbFormEnum.values(), "doc.cwbbData.sbList.", "doc.cwbbData.", true),
    // 财务报表（企业会计准则（适用于已执行新金融准则、新收入准则和新租赁准则））
    cwbbqykjzzyzx("cwbbqykjzzyzx", CwbbFormEnum.values(), "doc.cwbbData.sbList.", "doc.cwbbData.", true),
    // 财务报表（企业会计准则（适用于未执行新金融准则、新收入准则和新租赁准则））
    cwbbqykjzzwzx("cwbbqykjzzwzx", CwbbFormEnum.values(), "doc.cwbbData.sbList.", "doc.cwbbData.", true),
    // 财务报表（小企业会计准则）
    cwbbxqykjzz("cwbbxqykjzz", CwbbFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 印花税
    yhsyspzdraqhzjn("yhsyspzdraqhzjn", NullFormEnum.values(), null, "doc.yhsData.", true),
    // 文化建设费
    whsyjsf("whsyjsf", WhsyjsfFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 印花税(按月申报)
    yhs("yhs", YhsFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 印花税(按次申报)
    yhsac("yhsac", YhsacFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 通用申报（工会经费）
    tysb("tysb", TysbFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 山东涂料消费税
    shandongTlXfs("shandongTlXfs", ShandongTlXfsFormEnum.values(), "doc.tlXfsData.sbList.", "doc.tlXfsData.", true),
    // 增值税预缴
    zzsyjskb("zzsyjskb", ZzsyjskbFormEnum.values(), "doc.zzsyjskbData.sbList.", "doc.zzsyjskbData.", true),
    // 土地增值税1类
    tdzzsyz("tdzzsyz", TdzzsyzFormEnum.values(), "doc.tdzzsyzData.sbList.", "doc.tdzzsyzData.", true),
    // 山东成品油消费税
    shandongCpyXfs("shandongCpyXfs", ShandongCpyXfsFormEnum.values(), "doc.cpyXfsData.sbList.", "doc.cpyXfsData.", true),
    // 残疾人就业保障金
    cjrjybzj("cjrjybzj", CbjFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 城镇土地使用税房产税
    cztdsysfcs("cztdsysfcs", CztdsysfcsFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 环境保护税
    hjbhsa("hjbhsa", HjbhsaFormEnum.values(), "doc.hjbhsaData.sbList.", "doc.hjbhsaData.", true),
    // 其他消费税
    qtxfs("qtxfs", QtxfsFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 工会经费
    ghjf("ghjf", HenanGhjfFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 地方各项基金垃圾处理费
    dfgxjjfljclf("dfgxjjfljclf", DfgxjjfljclfFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
    // 地方各项基金工费经费
    gfjf("gfjf", GfjfFormEnum.values(), "doc.ybData.sbList.", "doc.ybData.", true),
	
    szysb("szysb", SzysbFormEnum.values(), "doc.szysbData.sbList.", "doc.szysbData.", true),
    szysa("szysa", SzysaFormEnum.values(), "doc.szysaData.sbList.", "doc.szysaData.", true);
    private String code;

    private String showName;

    private List<BaseForm> formList;

    private String sbListJpath;

    private String bwDataPrefixJpath;

    private boolean autoMappingable;

    SzEnum(String code, BaseForm[] forms, String sbListJpath, String bwDataPrefixJpath, boolean autoMappingable) {
        this.code = code;
        this.formList = new ArrayList<>();
        this.formList.addAll(Arrays.asList(forms));
        this.sbListJpath = sbListJpath;
        this.bwDataPrefixJpath = bwDataPrefixJpath;
        this.autoMappingable = autoMappingable;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getShowName() {
        return showName;
    }

    @Override
    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public List<BaseForm> getFormList() {
        return Collections.unmodifiableList(formList);
    }

    public String getSbListJpath() {
        return sbListJpath;
    }

    public String getBwDataPrefixJpath() {
        return bwDataPrefixJpath;
    }

    public boolean isAutoMappingable() {
        return autoMappingable;
    }

    @Override
    public String toString() {
        return "SzEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                ", formList=" + formList +
                ", sbListJpath='" + sbListJpath + '\'' +
                ", bwDataPrefixJpath='" + bwDataPrefixJpath + '\'' +
                ", autoMappingable=" + autoMappingable +
                '}';
    }
}

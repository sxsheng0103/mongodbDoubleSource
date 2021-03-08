package com.pangu.unicom.base.domain;

/**
 * @Author wangjx
 * @Date 2020/6/9 12:24
 * @Version 1.0
 */
public enum CompareResult   {
    //第一列
    sqldse1("sqldse1", "上期留抵税额#本月数#一般项目","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[0].sqldse"),
    //第三列
    sqldse3("sqldse3", "上期留抵税额#本月数#即征即退项目","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[2].sqldse"),
    //第一列
    ynse1("ynse1", "应纳税额#本月数#一般项目","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[0].ynse"),
    //第三列
    ynse3("ynse3", "应纳税额#本月数#即征即退项目","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[2].ynse"),
    //第一列
    bqybtse1("bqybtse1", "本期应补(退)税额#本月数#一般项目","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[0].bqybtse"),
    //第三列
    bqybtse3("bqybtse3", "本期应补(退)税额#本月数#即征即退项目","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[2].bqybtse"),


    asysljsxse("asysljsxse", "（一）按适用税率计税销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].asysljsxse"),
    yshwxse("yshwxse", "其中：应税货物销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].yshwxse"),
    yslwxse("yslwxse", "应税劳务销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].yslwxse"),
    syslNsjctzxse("syslNsjctzxse", "纳税检查调整的销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].syslNsjctzxse"),
    ajybfjsxse("ajybfjsxse", "（二）按简易办法计税销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].ajybfjsxse"),
    jybfNsjctzxse("jybfNsjctzxse", "其中：纳税检查调整的销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].jybfNsjctzxse"),
    mdtbfckxse("mdtbfckxse", "（三）免、抵、退办法出口销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].mdtbfckxse"),
    msxse("msxse", "（四）免税销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].msxse"),
    mshwxse("mshwxse", "其中：免税货物销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].mshwxse"),
    mslwxse("mslwxse", "免税劳务销售额-一般项目本年累计","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[1].mslwxse"),

    qmldse("qmldse", "期末留抵税额-一般项目本月数","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[0].qmldse"),
    ynsehj("ynsehj", "应纳税额合计-一般项目本月数","doc.ybData.zzssyyybnsrzb.zbGrid.zbGridlbVO[0].ynsehj"),

    qmye("qmye", "增值税税控系统专用设备费及技术维护费-期末余额","doc.ybData.zzssyyybnsr04bqjxsemxb.bqjxsemxbGrid.bqjxsemxbGridlbVO[0].qmye"),
    qmye2("qmye2", "一般项目加计抵减额计算-期末余额","doc.ybData.zzssyyybnsr04bqjxsemxb.bqjxsemxbGrid.bqjxsemxbGridlbVO[5].qmye"),
    qmye3("qmye3", "一般项目加计抵减额计算-期末余额","doc.ybData.zzssyyybnsr04bqjxsemxb.bqjxsemxbGrid.bqjxsemxbGridlbVO[6].qmye"),



    //附加税
    bqybtsehj("bqybtsehj", "本期应补（退）税（费）额#合计","doc.fjsData.fjssbb.sbxxGrid.sbxxhjGridlbVO.bqybtsehj"),

    cswhjssphjmse("cswhjssphjmse", "城市维护建设税_本期增值税小规模纳税人减征额","doc.fjsData.cswhjss.phjmse"),
    dfjyfjphjmse("dfjyfjphjmse", "地方教育附加_本期增值税小规模纳税人减征额","doc.fjsData.dfjyfj.phjmse"),
    jyffjphjmse("jyffjphjmse", "教育费附加_本期增值税小规模纳税人减征额","doc.fjsData.jyffj.phjmse"),

    cswhjssbqybtse("cswhjssbqybtse", "城市维护建设税_本期应补（退）税（费）额","doc.fjsData.cswhjss.bqybtse"),
    dfjyfjbqybtse("dfjyfjbqybtse", "地方教育附加_本期应补（退）税（费）额","doc.fjsData.dfjyfj.bqybtse"),
    jyffjbqybtse("jyffjbqybtse", "教育费附加_本期应补（退）税（费）额","doc.fjsData.jyffj.bqybtse"),

    //财务报表
    qmyeZc("qmyeZc", "流动资产合计 _期末余额资产","doc.cwbbData.zcfzData.zcfzGrid.zcfzGridVo[13].qmyeZc"),
    ncyeZc("ncyeZc", "流动资产合计 _年初余额资产","doc.cwbbData.zcfzData.zcfzGrid.zcfzGridVo[13].ncyeZc"),
    qmyeZc1("qmyeZc1", "非流动资产合计 _期末余额资产","doc.cwbbData.zcfzData.zcfzGrid.zcfzGridVo[31].qmyeZc"),
    ncyeZc1("ncyeZc1", "非流动资产合计 _年初余额资产","doc.cwbbData.zcfzData.zcfzGrid.zcfzGridVo[31].ncyeZc"),
    qmyeQy("qmyeQy", "负债合计 _期末余额权益","doc.cwbbData.zcfzData.zcfzGrid.zcfzGridVo[59].qmyeQy"),
    ncyeQy("ncyeQy", "负债合计 _年初余额权益","doc.cwbbData.zcfzData.zcfzGrid.zcfzGridVo[59].ncyeQy"),

    sqje1("sqje1", "营业收入 _上期金额","doc.cwbbData.lrbData.lrbGrid.lrbGridVo[0].sqje1"),
    bqje1("bqje1", "营业收入 _本期金额","doc.cwbbData.lrbData.lrbGrid.lrbGridVo[0].bqje"),
    sqje2("sqje2", "减:营业成本 _上期金额","doc.cwbbData.lrbData.lrbGrid.lrbGridVo[1].sqje1"),
    bqje2("bqje2", "减:营业成本 _本期金额","doc.cwbbData.lrbData.lrbGrid.lrbGridVo[1].bqje"),

    sqje3("sqje3", "三、利润总额(亏损总额以“–“号填列) _上期金额","doc.cwbbData.lrbData.lrbGrid.lrbGridVo[14].sqje1"),
    bqje3("bqje3", "三、利润总额(亏损总额以“–“号填列) _本期金额","doc.cwbbData.lrbData.lrbGrid.lrbGridVo[14].bqje"),

    //消费税
    qcmjse("qcmjse", "期初未缴税额","doc.ybData.qtysxfpxfsnssbb.qtysxfpxfsnssbbForm.qcmjse"),
    bqjnqqynse("bqjnqqynse", "本期缴纳前期应纳税额","doc.ybData.qtysxfpxfsnssbb.qtysxfpxfsnssbbForm.bqjnqqynse"),
    bqybtse("bqybtse", "本期应补（退）税额","doc.ybData.qtysxfpxfsnssbb.qtysxfpxfsnssbbForm.bqybtse"),

    //小规模增值税
    yzzzsbhsxse("yzzzsbhsxse", "应征增值税不含税销售额（3%征收率）_本期数_货物及劳务","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[0].yzzzsbhsxse"),
    swjgdkdzzszyfpbhsxse("swjgdkdzzszyfpbhsxse", "税务机关代开的增值税专用发票不含税销售额_本期数_货物及劳务","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[0].swjgdkdzzszyfpbhsxse"),
    skqjkjdptfpbhsxse("skqjkjdptfpbhsxse", "税控器具开具的普通发票不含税销售额_本期数_货物及劳务","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[0].skqjkjdptfpbhsxse"),

    yzzzsbhsxse1("yzzzsbhsxse1", "应征增值税不含税销售额（3%征收率）_本期数_服务、不动产和无形资产","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[1].yzzzsbhsxse"),
    swjgdkdzzszyfpbhsxse1("swjgdkdzzszyfpbhsxse1", "税务机关代开的增值税专用发票不含税销售额_本期数_不动产和无形资产","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[1].swjgdkdzzszyfpbhsxse"),
    skqjkjdptfpbhsxse1("skqjkjdptfpbhsxse1", "税控器具开具的普通发票不含税销售额_本期数_不动产和无形资产","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[1].skqjkjdptfpbhsxse"),

    xsczbdcbhsxse("xsczbdcbhsxse", "应税增值税不含税销售额（5%征收率）_本期数_服务、不动产和无形资产","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[1].xsczbdcbhsxse"),
    swjgdkdzzszyfpbhsxse2("swjgdkdzzszyfpbhsxse2", "税务机关代开的增值税专用发票不含税销售额_本期数_服务、不动产和无形资产","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[1].swjgdkdzzszyfpbhsxse1"),
    skqjkjdptfpbhsxse2("skqjkjdptfpbhsxse2", "税控器具开具的普通发票不含税销售额_本期数_服务、不动产和无形资产","doc.xgmData.zzssyyxgmnsr.zzsxgmGrid.zzsxgmGridlb[1].skqjkjdptfpbhsxse2"),

    //所得税
    ybtsdseLj("ybtsdseLj", "本期应补（退）所得税额（11-12-13-14） 税务机关确定的本期应纳所得税额#本年累计金额","doc.ybData.a200000Ywbd.sbbxxForm.ybtsdseLj"),
    zjgybtsdseBq("zjgybtsdseBq", "总机构本期分摊应补（退）所得税额（17+18+19）#总机构填报","doc.ybData.a200000Ywbd.sbbxxForm.zjgybtsdseBq"),
    fpbl("fpbl", "分支机构本期分摊比例#分支机构填报","doc.ybData.a200000Ywbd.sbbxxForm.fpbl"),
    fzjgfpsdseBq("fzjgfpsdseBq", "分支机构本期分摊应补（退）所得税额#分支机构填报","doc.ybData.a200000Ywbd.sbbxxForm.fzjgfpsdseBq");

    private String code;
    private String name;
    private String jpath;
    CompareResult(String code, String name,String jpath) {
        this.code = code;
        this.name = name;
        this.jpath = jpath;
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getJpath() {
        return jpath;
    }

    public static String getComName(String jpath){
        for (CompareResult compareResult:CompareResult.values()) {
            if (compareResult.getJpath().equals(jpath)){
                return compareResult.name;
            }
        }
        return null;
    }
    public static String getComJpath(String jpath){
        for (CompareResult compareResult:CompareResult.values()) {
            if (compareResult.getJpath().equals(jpath)){
                return compareResult.jpath;
            }
        }
        return null;
    }
}

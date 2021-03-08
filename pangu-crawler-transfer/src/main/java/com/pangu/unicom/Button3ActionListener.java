/**
 * @PackgeName: com.pangu.selenium
 * @ClassName: Button3ActionListener
 * @Author: guoqiang
 * Date: 2021/3/8 9:41
 * @Version:
 * @Description:
 */
package com.pangu.unicom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.unicom.base.domain.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Button3ActionListener implements ActionListener {
    File file = new File(".","沃发票纳税申报平台secret.txt");
    private static final Logger logger = LogManager.getLogger("import event");
    ImportClearEnterprise importinfo = null;

    Button3ActionListener(ImportClearEnterprise o) {
        this.importinfo = o;
    }

    public void actionPerformed(ActionEvent e) {
        String  secritString = "";
        try {
            secritString = FileUtils.readFileToString(file, "utf-8");
        } catch (IOException ioException) {
            String info = ioException.fillInStackTrace().toString();
            JOptionPane.showMessageDialog((Component)null, info, "警告", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String buttonText = ((JButton)e.getSource()).getText();
        logger.info("开始导入" + buttonText);
        String nsrdq = this.importinfo.areaComb.getItemV();
        String urlString = "https://gec.10010.com/wtd-api/ui/taxmain/web/initBasicSource?nsrDq=" + nsrdq;
        String result = null;
        JSONArray formList = null;

        JSONObject jsonObject;
        try {
            result = HttpsImportPickData.getconnbyget(urlString,secritString);
            jsonObject = JSONObject.parseObject(result);
            formList = (JSONArray)((JSONObject)jsonObject.get("data")).get("formList");
        } catch (Exception var81) {
            String info = var81.fillInStackTrace().toString();//sessionStorage.token
            JOptionPane.showMessageDialog((Component)null, info, "提示", 2);
            return;
        }

        Map<String, String> zzsybnsrjsonMap = new HashMap();
        Iterator var15 = formList.iterator();
        while(true) {
            while(var15.hasNext()) {
                Object ee = var15.next();
                JSONObject sz = (JSONObject)ee;
                Iterator var18;
                Object o;
                JSONObject jsono;
                JSONArray jsonArray = null;
                if (sz.get("code").equals(SzEnum.zzsybnsr.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                } else if (sz.get("code").equals(SzEnum.fjs.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                } else if (sz.get("code").equals(SzEnum.cwbbqykjzd.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                } else if (sz.get("code").equals(SzEnum.cwbbqykjzzwzx.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                } else if (sz.get("code").equals(SzEnum.cwbbqykjzzyzx.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                } else if (sz.get("code").equals(SzEnum.qysdsayjb.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                } else if (sz.get("code").equals(SzEnum.qtxfs.getCode())) {
                    jsonArray = (JSONArray)sz.get("formOrderList");
                    var18 = jsonArray.iterator();

                    while(var18.hasNext()) {
                        o = var18.next();
                        jsono = (JSONObject)o;
                        zzsybnsrjsonMap.put(jsono.getString("code"), jsono.getString("sz"));
                    }
                }
            }

            String customp = this.importinfo.customparent.getItemV();
            String customc = this.importinfo.customChild.getItemV();
            File file = this.importinfo.excelJFileChooser.getSelectedFile();
            XSSFWorkbook sheets = null;

            try {
                sheets = new XSSFWorkbook(new FileInputStream(file.getAbsoluteFile()));
            } catch (IOException var80) {
                var80.printStackTrace();
            }

            XSSFSheet sheet = sheets.getSheet(sheets.getSheetName(0));
            int rows = sheet.getPhysicalNumberOfRows();
            if (!sheet.getRow(1).getCell(0).convertCellValueToString().trim().equals("序号")) {
                logger.warn("第1列不是序号列,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(1).convertCellValueToString().trim().equals("省市名称")) {
                logger.warn("第2列不是省市名称,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(2).convertCellValueToString().trim().equals("纳税人名称")) {
                logger.warn("第3列不是纳税人名称,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(3).convertCellValueToString().trim().equals("纳税人识别号")) {
                logger.warn("第4列不是纳税人识别号,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(4).convertCellValueToString().trim().contains("公司分类")) {
                logger.warn("第5列不是公司分类,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(5).convertCellValueToString().trim().contains("主管税务机关")) {
                logger.warn("第6列不是主管税务机关,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(6).convertCellValueToString().trim().equals("企业联系人")) {
                logger.warn("第7列不是企业联系人,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(7).convertCellValueToString().trim().equals("联系电话")) {
                logger.warn("第8列不是联系电话,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(8).convertCellValueToString().trim().equals("账号")) {
                logger.warn("第9列不是账号,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(9).convertCellValueToString().trim().equals("密码")) {
                logger.warn("第10列不是密码,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(10).convertCellValueToString().trim().equals("办税人姓名")) {
                logger.warn("第11列不是办税人姓名,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(11).convertCellValueToString().trim().contains("办税人身份")) {
                logger.warn("第12列不是办税人身份,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(12).convertCellValueToString().trim().equals("手机号")) {
                logger.warn("第13列不是手机号,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(13).convertCellValueToString().trim().contains("中间号")) {
                logger.warn("第14列不是中间号,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(14).convertCellValueToString().trim().equals("项目组业务对接人")) {
                logger.warn("第15列不是项目组业务对接人,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(15).convertCellValueToString().trim().equals("增值税传递单")) {
                logger.warn("第16列不是增值税传递单,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(16).convertCellValueToString().trim().contains("电子税局短信验证码")) {
                logger.warn("第17列不是电子税局短信验证码,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(17).convertCellValueToString().trim().equals("接收税局短信验证码的手机号是否唯一")) {
                logger.warn("第18列不是接收税局短信验证码的手机号是否唯一,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(18).convertCellValueToString().trim().equals("局方推送短信号码")) {
                logger.warn("第19列不是局方推送短信号码,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(19).convertCellValueToString().trim().equals("税局是否自动扣款")) {
                logger.warn("第20列不是税局是否自动扣款,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(20).convertCellValueToString().trim().equals("月/季")) {
                logger.warn("第21列不是月/季,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(21).convertCellValueToString().trim().equals("主表")) {
                logger.warn("第22列不是主表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(22).convertCellValueToString().trim().equals("附表一")) {
                logger.warn("第23列不是附表一,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(23).convertCellValueToString().trim().equals("附表二")) {
                logger.warn("第24列不是附表二,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(24).convertCellValueToString().trim().equals("附表三")) {
                logger.warn("第25列不是附表三,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(25).convertCellValueToString().trim().equals("附表四")) {
                logger.warn("第26列不是附表四,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(26).convertCellValueToString().trim().equals("减免税申报明细")) {
                logger.warn("第27列不是减免税申报明细,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(27).convertCellValueToString().trim().equals("纳税信息传递单")) {
                logger.warn("第28列不是纳税信息传递单,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(28).convertCellValueToString().trim().equals("其他扣税凭证明细")) {
                logger.warn("第29列不是其他扣税凭证明细,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(29).convertCellValueToString().trim().equals("增值税分配表")) {
                logger.warn("第30列不是增值税分配表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(30).convertCellValueToString().trim().equals("代扣代缴税收通用缴款书")) {
                logger.warn("第31列不是代扣代缴税收通用缴款书,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(31).convertCellValueToString().trim().equals("月/季")) {
                logger.warn("第32列不是月/季,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(32).convertCellValueToString().trim().equals("附加税征收项目")) {
                logger.warn("第33列不是附加税征收项目,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(33).convertCellValueToString().trim().equals("月/季")) {
                logger.warn("第34列不是月/季,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(34).convertCellValueToString().trim().equals("利润表")) {
                logger.warn("第35列不是利润表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(35).convertCellValueToString().trim().equals("现金流量表")) {
                logger.warn("第36列不是现金流量表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(36).convertCellValueToString().trim().equals("资产负债表")) {
                logger.warn("第37列不是资产负债表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(37).convertCellValueToString().trim().equals("所有者权益表")) {
                logger.warn("第38列不是所有者权益表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(38).convertCellValueToString().trim().equals("千户集团")) {
                logger.warn("第39列不是千户集团,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(39).convertCellValueToString().trim().equals("月/季")) {
                logger.warn("第40列不是月/季,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(40).convertCellValueToString().trim().contains("企业所得税")) {
                logger.warn("第41列不是企业所得税主表,不能正确导入,请谨慎操作!contains企业所得税", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(41).convertCellValueToString().trim().equals("免税收入、减计收入、所得减免等优惠明细表")) {
                logger.warn("第42列不是免税收入、减计收入、所得减免等优惠明细表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(42).convertCellValueToString().trim().equals("固定资产加速折旧(扣除)优惠明细表")) {
                logger.warn("第43列不是固定资产加速折旧(扣除)优惠明细表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(43).convertCellValueToString().trim().equals("减免所得税优惠明细表")) {
                logger.warn("第44列不是减免所得税优惠明细表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(44).convertCellValueToString().trim().equals("企业所得税汇总纳税分支机构所得税分配表")) {
                logger.warn("第45列不是企业所得税汇总纳税分支机构所得税分配表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(45).convertCellValueToString().trim().equals("居民企业参股外国企业信息报告表")) {
                logger.warn("第46列不是居民企业参股外国企业信息报告表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(46).convertCellValueToString().trim().equals("技术成果投资入股企业所得税递延纳税备案表")) {
                logger.warn("第47列不是技术成果投资入股企业所得税递延纳税备案表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(47).convertCellValueToString().trim().contains("财务报表类型")) {
                logger.warn("第48列不是财务报表类型,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(48).convertCellValueToString().trim().contains("历史数据格式")) {
                logger.warn("第49列不是历史数据格式,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(49).convertCellValueToString().trim().equals("财务报表申报时是否需要上传附件")) {
                logger.warn("第50列不是财务报表申报时是否需要上传附件,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(50).convertCellValueToString().trim().contains("财务报表申报时需要上传附件的格式")) {
                logger.warn("第51列不是财务报表申报时需要上传附件的格式,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(51).convertCellValueToString().trim().equals("电子税局展示所有的申报表还是只展示企业需要填写的申报表")) {
                logger.warn("第52列不是电子税局展示所有的申报表还是只展示企业需要填写的申报表,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(52).convertCellValueToString().trim().equals("备注")) {
                logger.warn("第52列不是备注,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(53).convertCellValueToString().trim().contains("增值税主表声明人姓名")) {
                logger.warn("第53列不是增值税主表声明人姓名,不能正确导入,请谨慎操作!contains", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(54).convertCellValueToString().trim().equals("附加税经办人姓名")) {
                logger.warn("第54列不是附加税经办人姓名,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(55).convertCellValueToString().trim().equals("附加税经办人身份证号")) {
                logger.warn("第55列不是附加税经办人身份证号,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(56).convertCellValueToString().trim().equals("附加税经办人联系电话")) {
                logger.warn("第56列不是附加税经办人联系电话,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(57).convertCellValueToString().trim().equals("企业所得税经办人姓名")) {
                logger.warn("第57列不是企业所得税经办人姓名,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(1).getCell(58).convertCellValueToString().trim().equals("企业所得税经办人身份证号")) {
                logger.warn("第58列不是企业所得税经办人身份证号,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            if (!sheet.getRow(0).getCell(59).convertCellValueToString().trim().equals("特殊情况说明")) {
                logger.warn("第59列不是特殊情况说明,不能正确导入,请谨慎操作!equals", new Object[0]);
                return;
            }

            ArrayList<String> zzscods = new ArrayList();
            ArrayList<String> fjscods = new ArrayList();
            ArrayList<String> sdscods = new ArrayList();
            ArrayList<String> cwbbcods = new ArrayList();

            for(int i = 2; i < rows; ++i) {
                String nsrsbh = sheet.getRow(1).getCell(3).convertCellValueToString().trim();
                String nsrmc = sheet.getRow(1).getCell(2).convertCellValueToString().trim();
                String zgswjg = sheet.getRow(1).getCell(5).convertCellValueToString().trim();
                String gsfl = sheet.getRow(1).getCell(4).convertCellValueToString().trim();
                String enterprisemobile = sheet.getRow(1).getCell(7).convertCellValueToString().trim();
                String enterpriseperson = sheet.getRow(1).getCell(6).convertCellValueToString().trim();
                String enterpersonmail = "";
                String account = sheet.getRow(1).getCell(8).convertCellValueToString().trim();
                String password = sheet.getRow(1).getCell(9).convertCellValueToString().trim();
                String bsrxm = sheet.getRow(1).getCell(10).convertCellValueToString().trim();
                String bsrsf = sheet.getRow(1).getCell(11).convertCellValueToString().trim();
                String bsrmobile = sheet.getRow(1).getCell(12).convertCellValueToString().trim();
                String zzszq = sheet.getRow(1).getCell(20).convertCellValueToString().trim();
                String zzszb = sheet.getRow(1).getCell(21).convertCellValueToString().trim();
                String zzszbsmcxm = sheet.getRow(1).getCell(53).convertCellValueToString().trim();
                String zzsfb1 = sheet.getRow(1).getCell(22).convertCellValueToString().trim();
                String zzsfb2 = sheet.getRow(1).getCell(23).convertCellValueToString().trim();
                String zzsfb3 = sheet.getRow(1).getCell(24).convertCellValueToString().trim();
                String zzsfb4 = sheet.getRow(1).getCell(25).convertCellValueToString().trim();
                String zzsjmsb = sheet.getRow(1).getCell(26).convertCellValueToString().trim();
                String zzscdd = sheet.getRow(1).getCell(27).convertCellValueToString().trim();
                String zzsqtkk = sheet.getRow(1).getCell(28).convertCellValueToString().trim();
                String zzsfpb = sheet.getRow(1).getCell(29).convertCellValueToString().trim();
                String zzsdkdj = sheet.getRow(1).getCell(30).convertCellValueToString().trim();
                if (sheet.getRow(i).getCell(21).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.zzssyyybnsrzb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.zzssyyybnsrzb.getCode());
                    } else {
                        logger.warn("zzsybnsr-zzssyyybnsrzb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(22).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.zzssyyybnsr01bqxsqkmxb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.zzssyyybnsr01bqxsqkmxb.getCode());
                    } else {
                        logger.warn("zzsybnsr-zzssyyybnsr01bqxsqkmxb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(23).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.zzssyyybnsr02bqjxsemxb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.zzssyyybnsr02bqjxsemxb.getCode());
                    } else {
                        logger.warn("zzsybnsr-zzssyyybnsr02bqjxsemxb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(24).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.zzssyyybnsr03ysfwkcxmmx.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.zzssyyybnsr03ysfwkcxmmx.getCode());
                    } else {
                        logger.warn("zzsybnsr-zzssyyybnsr03ysfwkcxmmx error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(25).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.zzssyyybnsr04bqjxsemxb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.zzssyyybnsr04bqjxsemxb.getCode());
                    } else {
                        logger.warn("zzsybnsr-zzssyyybnsr04bqjxsemxb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(26).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.zzsjmssbmxb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.zzsjmssbmxb.getCode());
                    } else {
                        logger.warn("zzsybnsr-zzsjmssbmxb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(27).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.dxqyfzjgzzshznsxxcdd.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.dxqyfzjgzzshznsxxcdd.getCode());
                    } else {
                        logger.warn("zzsybnsr-dxqyfzjgzzshznsxxcdd error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(28).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.qtkspzmxb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.qtkspzmxb.getCode());
                    } else {
                        logger.warn("zzsybnsr-qtkspzmxb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(29).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.hznsqyzzsfpb.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.hznsqyzzsfpb.getCode());
                    } else {
                        logger.warn("zzsybnsr-hznsqyzzsfpb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(30).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(ZzsybnsrFormEnum.dkdjsstyjksdkqd.getCode())).equals(SzEnum.zzsybnsr.getCode())) {
                        zzscods.add(ZzsybnsrFormEnum.dkdjsstyjksdkqd.getCode());
                    } else {
                        logger.warn("zzsybnsr-dkdjsstyjksdkqd error  please check;", new Object[0]);
                    }
                }

                String fjszq = sheet.getRow(1).getCell(31).convertCellValueToString().trim();
                String fjszspm = sheet.getRow(1).getCell(32).convertCellValueToString().trim();
                String fjsjbrxm = sheet.getRow(1).getCell(54).convertCellValueToString().trim();
                String fjsjbrmobile = sheet.getRow(1).getCell(56).convertCellValueToString().trim();
                String fjssfzh = sheet.getRow(1).getCell(55).convertCellValueToString().trim();
                if (sheet.getRow(i).getCell(32).convertCellValueToString().replace("地方教育费附加", "").trim().contains("教育费附加")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.jyffj.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.jyffj.getCode());
                    } else {
                        logger.warn("fjs-jyffj error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().trim().contains("地方教育费附加")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.dfjyfj.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.dfjyfj.getCode());
                    } else {
                        logger.warn("fjs-dfjyfj error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().trim().contains("城建税")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.cswhjss.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.cswhjss.getCode());
                    } else {
                        logger.warn("fjs-cswhjss error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().replace("水利建设专项收入消费税附加", "").trim().contains("水利建设专项收入")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.sljszxsy.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.sljszxsy.getCode());
                    } else {
                        logger.warn("fjs-sljszxsy error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().trim().contains("城市维护建设税消费税附加")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.cswhjssxfsfj.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.cswhjssxfsfj.getCode());
                    } else {
                        logger.warn("fjs-cswhjssxfsfj error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().trim().contains("教育费附加消费税附加")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.jyffjxfsfj.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.jyffjxfsfj.getCode());
                    } else {
                        logger.warn("fjs-jyffjxfsfj error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().trim().contains("地方教育附加消费税附加")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.dfjyfjxfsfj.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.dfjyfjxfsfj.getCode());
                    } else {
                        logger.warn("fjs-dfjyfjxfsfj error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(32).convertCellValueToString().trim().contains("水利建设专项收入消费税附加")) {
                    if (((String)zzsybnsrjsonMap.get(FjsFormItemEnum.sljszxsyxfsfj.getCode())).equals(SzEnum.fjs.getCode())) {
                        fjscods.add(FjsFormItemEnum.sljszxsyxfsfj.getCode());
                    } else {
                        logger.warn("fjs-sljszxsyxfsfj error  please check;", new Object[0]);
                    }
                }

                String ccbblx = sheet.getRow(i).getCell(47).convertCellValueToString().trim();
                String cwbbzq = sheet.getRow(1).getCell(33).convertCellValueToString().trim();
                String cwbblrb = sheet.getRow(1).getCell(34).convertCellValueToString().trim();
                String cwbbxjllb = sheet.getRow(1).getCell(35).convertCellValueToString().trim();
                String cwbbzcfzb = sheet.getRow(1).getCell(36).convertCellValueToString().trim();
                String cwbbsyzqyb = sheet.getRow(1).getCell(37).convertCellValueToString().trim();
                String cwbbqhjt = sheet.getRow(1).getCell(38).convertCellValueToString().trim();
                String cwbblxcode = "";
                if (ccbblx.contains("一般企业会计准则")) {
                    cwbblxcode = SzEnum.cwbbqykjzzyzx.getCode();
                } else if (ccbblx.contains("企业会计制度")) {
                    cwbblxcode = SzEnum.cwbbqykjzd.getCode();
                } else if (ccbblx.contains("小企业会计准则")) {
                    cwbblxcode = SzEnum.cwbbxqykjzz.getCode();
                }

                if (sheet.getRow(i).getCell(34).convertCellValueToString().trim().equals("Y")) {
                    if (zzsybnsrjsonMap.get(CwbbFormEnum.lrbData.getCode()) != null) {
                        cwbbcods.add(CwbbFormEnum.lrbData.getCode());
                    } else {
                        logger.warn(cwbblxcode + "-lrbData error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(35).convertCellValueToString().trim().equals("Y")) {
                    if (zzsybnsrjsonMap.get(CwbbFormEnum.xjllData.getCode()) != null) {
                        cwbbcods.add(CwbbFormEnum.xjllData.getCode());
                    } else {
                        logger.warn(cwbblxcode + "-xjllData error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(36).convertCellValueToString().trim().equals("Y")) {
                    if (zzsybnsrjsonMap.get(CwbbFormEnum.zcfzData.getCode()) != null) {
                        cwbbcods.add(CwbbFormEnum.zcfzData.getCode());
                    } else {
                        logger.warn(cwbblxcode + "-zcfzData error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(37).convertCellValueToString().trim().equals("Y")) {
                    if (zzsybnsrjsonMap.get(CwbbFormEnum.syzqyData.getCode()) != null) {
                        cwbbcods.add(CwbbFormEnum.syzqyData.getCode());
                    } else {
                        logger.warn(cwbblxcode + "-syzqyData error  please check;", new Object[0]);
                    }
                }

                String qhqy = sheet.getRow(i).getCell(38).convertCellValueToString().trim();
                if (sheet.getRow(i).getCell(38).convertCellValueToString().trim().equals("Y")) {
                }

                String qysdszq = sheet.getRow(1).getCell(39).convertCellValueToString().trim();
                String qysdszb = sheet.getRow(1).getCell(40).convertCellValueToString().trim();
                String qysdsmssr = sheet.getRow(1).getCell(41).convertCellValueToString().trim();
                String qysdsgdzc = sheet.getRow(1).getCell(42).convertCellValueToString().trim();
                String qysdsjmsds = sheet.getRow(1).getCell(43).convertCellValueToString().trim();
                String qysdshznsfzjgfpb = sheet.getRow(1).getCell(44).convertCellValueToString().trim();
                String qysdsjmqycgbgb = sheet.getRow(1).getCell(45).convertCellValueToString().trim();
                String qysdsjscgtzr = sheet.getRow(1).getCell(46).convertCellValueToString().trim();
                if (sheet.getRow(i).getCell(40).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.a200000Ywbd.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.a200000Ywbd.getCode());
                    } else {
                        logger.warn("qysdsayjb-a200000Ywbd error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(41).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.a201010Ywbd.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.a201010Ywbd.getCode());
                    } else {
                        logger.warn("qysdsayjb-a201010Ywbd error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(42).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.a201020Ywbd.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.a201020Ywbd.getCode());
                    } else {
                        logger.warn("qysdsayjb-a201020Ywbd error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(43).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.a201030Ywbd.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.a201030Ywbd.getCode());
                    } else {
                        logger.warn("qysdsayjb-a201030Ywbd error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(44).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.a202000Ywbd.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.a202000Ywbd.getCode());
                    } else {
                        logger.warn("qysdsayjb-a202000Ywbd error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(45).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.jmqycgwgqyxxbgb.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.jmqycgwgqyxxbgb.getCode());
                    } else {
                        logger.warn("qysdsayjb-jmqycgwgqyxxbgb error  please check;", new Object[0]);
                    }
                }

                if (sheet.getRow(i).getCell(46).convertCellValueToString().trim().equals("Y")) {
                    if (((String)zzsybnsrjsonMap.get(QysdsayjbFormEnum.jscgtzrgqysdsdynsbab.getCode())).equals(SzEnum.qysdsayjb.getCode())) {
                        sdscods.add(QysdsayjbFormEnum.jscgtzrgqysdsdynsbab.getCode());
                    } else {
                        logger.warn("qysdsayjb-jscgtzrgqysdsdynsbab error  please check;", new Object[0]);
                    }
                }

                Map<String, Object> zzsparams = new HashMap();
                zzsparams.put("jbrName", "");
                zzsparams.put("jbrTelno", "");
                zzsparams.put("jbrSfzhm", "");
                zzsparams.put("jkfs", "sfxy");
                zzsparams.put("zzsybnsrZbSmrName", sheet.getRow(i).getCell(53).convertCellValueToString().trim());
                zzsparams.put("sz", SzEnum.zzsybnsr.getCode());
                String sbpc = sheet.getRow(i).getCell(20).convertCellValueToString();
                if (sbpc.contains("季")) {
                    sbpc = "jb";
                } else if (sbpc.contains("月")) {
                    sbpc = "yb";
                } else if (sbpc.contains("次")) {
                    sbpc = "cb";
                } else if (sbpc.contains("半年")) {
                    sbpc = "bn";
                } else if (sbpc.contains("年")) {
                    sbpc = "nb";
                }

                zzsparams.put("sbpc", sbpc);
                zzsparams.put("fileType", "");
                zzsparams.put("sbbds", StringUtils.join(zzscods, ","));
                zzsparams.put("qhqy", qhqy.equals("Y") ? "1" : "0");
                zzsparams.put("isNeedZzsSmrName", !sheet.getRow(i).getCell(53).convertCellValueToString().trim().equals("") ? "1" : "0");
                Map<String, Object> fjsparams = new HashMap();
                fjsparams.put("jbrName", sheet.getRow(i).getCell(54).convertCellValueToString().trim());
                fjsparams.put("jbrTelno", sheet.getRow(i).getCell(56).convertCellValueToString().trim());
                fjsparams.put("jbrSfzhm", sheet.getRow(i).getCell(55).convertCellValueToString() == null ? "" : sheet.getRow(i).getCell(55).convertCellValueToString().trim());
                fjsparams.put("jkfs", "sfxy");
                fjsparams.put("zzsybnsrZbSmrName", "");
                fjsparams.put("sz", SzEnum.fjs.getCode());
                sbpc = sheet.getRow(i).getCell(31).convertCellValueToString();
                if (sbpc.contains("季")) {
                    sbpc = "jb";
                } else if (sbpc.contains("月")) {
                    sbpc = "yb";
                } else if (sbpc.contains("次")) {
                    sbpc = "cb";
                } else if (sbpc.contains("半年")) {
                    sbpc = "bn";
                } else if (sbpc.contains("年")) {
                    sbpc = "nb";
                }

                fjsparams.put("sbpc", sbpc);
                fjsparams.put("fileType", "");
                fjsparams.put("sbbds", StringUtils.join(fjscods, ","));
                fjsparams.put("qhqy", qhqy.equals("Y") ? "1" : "0");
                fjsparams.put("isNeedZzsSmrName", "");
                Map<String, Object> qysdsparams = new HashMap();
                qysdsparams.put("jbrName", "");
                qysdsparams.put("jbrTelno", "");
                qysdsparams.put("jbrSfzhm", "");
                qysdsparams.put("jkfs", "sfxy");
                qysdsparams.put("zzsybnsrZbSmrName", "");
                qysdsparams.put("sz", SzEnum.qysdsayjb.getCode());
                sbpc = sheet.getRow(i).getCell(39).convertCellValueToString();
                if (sbpc.contains("季")) {
                    sbpc = "jb";
                } else if (sbpc.contains("月")) {
                    sbpc = "yb";
                } else if (sbpc.contains("次")) {
                    sbpc = "cb";
                } else if (sbpc.contains("半年")) {
                    sbpc = "bn";
                } else if (sbpc.contains("年")) {
                    sbpc = "nb";
                }

                qysdsparams.put("sbpc", sbpc);
                qysdsparams.put("fileType", "");
                qysdsparams.put("sbbds", StringUtils.join(sdscods, ","));
                qysdsparams.put("qhqy", "");
                qysdsparams.put("isNeedZzsSmrName", "");
                Map<String, Object> cwbbparams = new HashMap();
                cwbbparams.put("jbrName", "");
                cwbbparams.put("jbrTelno", "");
                cwbbparams.put("jbrSfzhm", "");
                cwbbparams.put("jkfs", "sfxy");
                cwbbparams.put("zzsybnsrZbSmrName", "");
                cwbbparams.put("sz", cwbblxcode);
                sbpc = sheet.getRow(i).getCell(33).convertCellValueToString();
                if (sbpc.contains("季")) {
                    sbpc = "jb";
                } else if (sbpc.contains("月")) {
                    sbpc = "yb";
                } else if (sbpc.contains("次")) {
                    sbpc = "cb";
                } else if (sbpc.contains("半年")) {
                    sbpc = "bn";
                } else if (sbpc.contains("年")) {
                    sbpc = "nb";
                }

                cwbbparams.put("sbpc", sbpc);
                cwbbparams.put("fileType", "");
                cwbbparams.put("sbbds", StringUtils.join(cwbbcods, ","));
                cwbbparams.put("qhqy", "");
                cwbbparams.put("isNeedZzsSmrName", "");
                ArrayList sbqcList = new ArrayList();
                if (!zzsparams.get("sbbds").equals("")) {
                    sbqcList.add(zzsparams);
                }

                if (!fjsparams.get("sbbds").equals("")) {
                    sbqcList.add(fjsparams);
                }

                if (!cwbbparams.get("sbbds").equals("")) {
                    sbqcList.add(cwbbparams);
                }

                if (!qysdsparams.get("sbbds").equals("")) {
                    sbqcList.add(qysdsparams);
                }

                Map<String, Object> paramrequest = new HashMap();
                paramrequest.put("nsrsbh", sheet.getRow(i).getCell(3).convertCellValueToString().trim());
                paramrequest.put("nsrmc", sheet.getRow(i).getCell(2).convertCellValueToString());
                paramrequest.put("nsrdq", nsrdq);
                paramrequest.put("customer", customp);
                paramrequest.put("zgswjgmc", sheet.getRow(i).getCell(5).convertCellValueToString().trim());
                String nsztType = "";
                if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("集团附属")) {
                    nsztType = "jtfs";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("集团")) {
                    nsztType = "jt";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("地市公司")) {
                    nsztType = "ds";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("区县公司")) {
                    nsztType = "qx";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("省分公司")) {
                    nsztType = "sf";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("省")) {
                    nsztType = "sf";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("区")) {
                    nsztType = "qx";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("县")) {
                    nsztType = "qx";
                } else if (sheet.getRow(i).getCell(4).convertCellValueToString().trim().contains("市")) {
                    nsztType = "ds";
                }

                paramrequest.put("nsztType", nsztType);
                paramrequest.put("nsztfzrName", sheet.getRow(i).getCell(6).convertCellValueToString());
                paramrequest.put("nsztfzrTelno", sheet.getRow(i).getCell(7).convertCellValueToString().trim());
                paramrequest.put("nsztfzrEmail", enterpersonmail);
                paramrequest.put("loginInfo", "{\"user_password_type\":{\"account\":\"" + sheet.getRow(i).getCell(8).convertCellValueToString().trim() + "\",\"password\":\"" + sheet.getRow(i).getCell(9).convertCellValueToString().trim() + "\"}}");
                paramrequest.put("enterprise", customc);
                paramrequest.put("sbqcList", sbqcList);
                paramrequest.put("operation", "rgxz");
                String saveurl = "https://gec.10010.com/wtd-api/ui/taxmain/web/saveNsrInfoFromWeb";
                HttpsImportPickData.requestActionPost(saveurl,
                        JSONObject.toJSONString(paramrequest),secritString,sheet.getRow(i).getCell(3).convertCellValueToString().trim());
            }

            return;
        }
    }
}
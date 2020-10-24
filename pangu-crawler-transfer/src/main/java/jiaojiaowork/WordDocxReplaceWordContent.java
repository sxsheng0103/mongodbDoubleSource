package jiaojiaowork;
/**
 * @Author sheng.ding
 * @Date 2020/10/23 22:05
 * @Version 1.0
 **/
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
 /**
   * 文件数据替换
   * @author 23  *
  */
          public class WordDocxReplaceWordContent {

              public static void getWord(Map<String, Object> data, List<List<String[]>> tabledataList, Map<String, Object> picmap,String xh,String outpath)
              throws Exception {
                 try {
                         FileInputStream is = new FileInputStream(path);
                      XWPFDocument document = new XWPFDocument(is);
                         // 替换掉表格之外的文本(仅限文本)
                         changeText(document, data);
                         // 替换表格内的文本对象
                         changeTableText(document, data);
                         // 替换图片
//                         changePic(document, picmap);
                         // 替换表格内的图片对象
//                         changeTablePic(document, picmap);
                         long time = System.currentTimeMillis();// 获取系统时间
                         System.out.println(time); // 打印时间
                // 使用try和catch关键字捕获异常
                 final String outpath1 = outpath;
                  try (
                          FileOutputStream out = new FileOutputStream( "C:\\Users\\Lenovo\\Desktop\\万达\\"+outpath1+"\\"+xh+"-12询价函及报价函标准文件（2016版）(模板）" + ".docx")
                  ){
                      document.write(out);
                  }
                  } catch (Exception e) {
                    e.printStackTrace();
                 }
      }
      /**
     * 替换段落文本
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     *
     */
      public static void changeText(XWPFDocument document, Map<String, Object> textMap) {
         // 获取段落集合
         // 返回包含页眉或页脚文本的段落
         List<XWPFParagraph> paragraphs = document.getParagraphs();
         // 增强型for循环语句，前面一个为声明语句，后一个为表达式
         for (XWPFParagraph paragraph : paragraphs) {
                 // 判断此段落是否需要替换
                 String text = paragraph.getText();// 检索文档中的所有文本
                 if (checkText(text)) {
                         List<XWPFRun> runs = paragraph.getRuns();
                         for (XWPFRun run : runs) {
                                 // 替换模板原来位置
                                 Object ob = changeValue(run.toString(), textMap);
                                 if (ob instanceof String) {
                                         if (textMap.containsKey(run.toString())) {
                                                 run.setText((String) ob, 0);
                                             }
                                     }
                             }
                     }
             }
     }

     /* 检查文本中是否包含指定的字符(此处为“$”)，并返回值 */
     public static boolean checkText(String text) {
         boolean check = false;
         if (text.contains("$")) {
                 check = true;
             }
         return check;
     }

     /**
* 替换图片
* @param document
* @param textMap
* @throws Exception
*/

     public static void changePic(XWPFDocument document, Map<String, Object> textMap) throws Exception {
         // 获取段落集合
         List<XWPFParagraph> paragraphs = document.getParagraphs();

         for (XWPFParagraph paragraph : paragraphs) {
                 // 判断此段落是否需要替换
                 String text = paragraph.getText();
                 if (checkText(text)) {
                         List<XWPFRun> runs = paragraph.getRuns();
                         for (XWPFRun run : runs) {
                                 // 替换模板原来位置
                                 Object ob = changeValue(run.toString(), textMap);
                                 if (ob instanceof String) {
                                         if (textMap.containsKey(run.toString())) {
                                                 run.setText("", 0);
                                                 try (FileInputStream is = new FileInputStream((String) ob)) {
                                                         run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, (String) ob, Units.toEMU(100),
                                                                         Units.toEMU(100));
                                                     }
                                             }
                                     }
                             }
                     }
             }
     }

     public static void changeTableText(XWPFDocument document, Map<String, Object> data) {
         // 获取文件的表格
         List<XWPFTable> tableList = document.getTables();

         // 循环所有需要进行替换的文本，进行替换
         for (int i = 0; i < tableList.size(); i++) {
                 XWPFTable table = tableList.get(i);
                 if (checkText(table.getText())) {
                         List<XWPFTableRow> rows = table.getRows();
                         // 遍历表格，并替换模板
                         eachTable(document, rows, data);
                     }
             }
     }

     public static void changeTablePic(XWPFDocument document, Map<String, Object> pic) throws Exception {
         List<XWPFTable> tableList = document.getTables();

         // 循环所有需要替换的文本，进行替换
         for (int i = 0; i < tableList.size(); i++) {
                 XWPFTable table = tableList.get(i);
                 if (checkText(table.getText())) {
                         List<XWPFTableRow> rows = table.getRows();
                         System.out.println("简单表格替换：" + rows);
                         // 遍历表格，并替换模板
                         eachTablePic(document, rows, pic);
                     }
             }
     }

     public static void eachTablePic(XWPFDocument document, List<XWPFTableRow> rows, Map<String, Object> pic)
     throws Exception {
         for (XWPFTableRow row : rows) {
                 List<XWPFTableCell> cells = row.getTableCells();
                 for (XWPFTableCell cell : cells) {
                         // 判断单元格是否需要替换
                         if (checkText(cell.getText())) {
                                 List<XWPFParagraph> paragraphs = cell.getParagraphs();
                                 for (XWPFParagraph paragraph : paragraphs) {
                                         List<XWPFRun> runs = paragraph.getRuns();
                                         for (XWPFRun run : runs) {
                                                 Object ob = changeValue(run.toString(), pic);
                                                 if (ob instanceof String) {

                                                                                   System.out.println("run" + "'" + run.toString() + "'");
                                                         if (pic.containsKey(run.toString())) {
                                                                 System.out.println("run" + run.toString() + "替换为" + ob);
                                                                 run.setText("", 0);
                                                                 try (FileInputStream is = new FileInputStream((String) ob)) {
               run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, (String) ob, Units.toEMU(100),
                       Units.toEMU(100));
                                                                     }
                                                             } else {
                                                                 System.out.println("'" + run.toString() + "' 不匹配");
                                                             }
                                                     }
                                             }
                                     }
                             }
                     }
             }
     }

     public static Object changeValue(String value, Map<String, Object> textMap) {
         Set<Map.Entry<String, Object>> textSets = textMap.entrySet();
         Object valu = "";
         for (Map.Entry<String, Object> textSet : textSets) {
                 // 匹配模板与替换值 格式${key}
                 String key = textSet.getKey();
                 if (value.contains(key)) {
                         valu = textSet.getValue();
                     }
             }
         return valu;
     }

     public static void eachTable(XWPFDocument document, List<XWPFTableRow> rows, Map<String, Object> textMap) {
         for (XWPFTableRow row : rows) {
                 List<XWPFTableCell> cells = row.getTableCells();
                 for (XWPFTableCell cell : cells) {
                         // 判断单元格是否需要替换
                         if (checkText(cell.getText())) {
                                 // System.out.println("cell:" + cell.getText());
                                 List<XWPFParagraph> paragraphs = cell.getParagraphs();
                                 for (XWPFParagraph paragraph : paragraphs) {
                                         List<XWPFRun> runs = paragraph.getRuns();
                                         for (XWPFRun run : runs) {

                                                 Object ob = changeValue(run.toString(), textMap);
                                                 if (ob instanceof String) {

                                                         System.out.println("run:" + "'" + run.toString() + "'");
                                                         if (textMap.containsKey(run.toString())) {
                                                                 System.out.println("run:" + run.toString() + "替换为" + ob);
                                                                 run.setText((String) ob, 0);
                                                             } else {
                                                                 System.out.println("'" + run.toString() + "'不匹配");
                                                             }
                                                     }
                                             }
                                     }
                             }
                     }
             }
     }




     public static String path = "C:\\Users\\Lenovo\\Desktop\\万达\\询价函及报价函标准文件（2016版）(模板）.docx";

     public static void fillword(String time1,String time2,String time3,String time4,String time5,String xh,String xmmc,
                                 String xmtzms,String dw,String sl,String dj,String zj,String tianshu,String note,String outpath) throws Exception {
         Map<String, Object> data = new HashMap<>();
         Map<String, Object> pic = new HashMap<>();
         data.put("${name}", xmmc);
         data.put("${time1}", time1);
         data.put("${time2}", time2);
         data.put("${time3}",time3 );
         data.put("${time4}",time4 );
         data.put("${time5}",time5 );

         data.put("${namea}",xmmc );
         data.put("${nameb}",xmtzms );
         data.put("${namec}", dw);
         data.put("${named}", sl);
         data.put("${namee}", dj);
         data.put("${namef}",zj );
         data.put("${nameg}", note);

         data.put("${name1}", xmmc);
         data.put("${name11}", xmmc);
         data.put("${time11}", time1);
         data.put("${time12}", time2);
         data.put("${time13}",time3 );
         data.put("${time14}",time4 );
         data.put("${time15}",time5 );
         data.put("${namea1}",xmmc );
         data.put("${nameb1}",xmtzms );
         data.put("${namec1}", dw);
         data.put("${named1}", sl);
         data.put("${namee1}", dj);
         data.put("${namef1}",zj );
         data.put("${nameg1}", note);



         // 列表(List)是对象的有序集合
         List<List<String[]>> tabledataList = new ArrayList<>();
         getWord(data, tabledataList, pic,xh,outpath);
     }

     public static void main(String[] args) throws Exception {
         String time1="1"; String time2="1"; String time3="1"; String time4="1"; String time5="1"; String xh="1"; String xmmc="1";
                 String xmtzms="1"; String dw="1"; String sl="1"; String dj="1"; String zj="1"; String tianshu="1"; String note="1"; String outpath="1";
         Map<String, Object> data = new HashMap<>();
         Map<String, Object> pic = new HashMap<>();
         data.put("${name}", xmmc);
         data.put("${time1}", time1);
         data.put("${time2}", time2);
         data.put("${time3}",time3 );
         data.put("${time4}",time4 );
         data.put("${time5}",time5 );

         data.put("${namea}",xmmc );
         data.put("${nameb}",xmtzms );
         data.put("${namec}", dw);
         data.put("${named}", sl);
         data.put("${namee}", dj);
         data.put("${namef}",zj );
         data.put("${nameg}", note);

         data.put("${name1}", xmmc);
         data.put("${name11}", xmmc);
         data.put("${time11}", time1);
         data.put("${time12}", time2);
         data.put("${time13}",time3 );
         data.put("${time14}",time4 );
         data.put("${time15}",time5 );
         data.put("${namea1}",xmmc );
         data.put("${nameb1}",xmtzms );
         data.put("${namec1}", dw);
         data.put("${named1}", sl);
         data.put("${namee1}", dj);
         data.put("${namef1}",zj );
         data.put("${nameg1}", note);



         // 列表(List)是对象的有序集合
         List<List<String[]>> tabledataList = new ArrayList<>();
         getWord(data, tabledataList, pic,xh,outpath);
     }

 }
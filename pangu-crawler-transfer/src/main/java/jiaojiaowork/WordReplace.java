package jiaojiaowork;

//import org.apache.poi.POIXMLDocument;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.util.*;

/**
 * 通过word模板生成新的word工具类
 * @author zhiheng
 */
public class WordReplace {

    /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     * @param inputUrl 模板存放地址
     * @param outputUrl 新文档存放地址
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     * @return 成功返回true,失败返回false
     */
    public static boolean changWord(String inputUrl, String outputUrl,
                                    Map<String, String> textMap, List<String[]> tableList) {
        //模板转换默认成功
        boolean changeFlag = true;
        try {
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(inputUrl));
            FileInputStream  input = new FileInputStream(inputUrl);
//            XWPFDocument  document = new XWPFDocument(input);
            //解析替换文本段落对象
            changeText(document, textMap);
            //解析替换表格对象
//            changeTable(document, textMap, tableList);
            //生成新的word
            File file = new File(outputUrl);
            FileOutputStream stream = new FileOutputStream(file);
            document.write(stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            changeFlag = false;
        }
        return changeFlag;
    }

    /**
     * 替换段落文本
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     */
    public static void changeText(XWPFDocument document, Map<String, String> textMap){
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            //判断此段落时候需要进行替换
            String text = paragraph.getText();
            if(checkText(text)){
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    //替换模板原来位置
                    run.setText(changeValue(run.toString(), textMap),0);
                }
            }
        }
    }

    /**
     * 替换表格对象方法
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     */
    public static void changeTable(XWPFDocument document, Map<String, String> textMap,
                                   List<String[]> tableList){
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            //只处理行数大于等于2的表格，且不循环表头
            XWPFTable table = tables.get(i);
            if(table.getRows().size()>1){
                //判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
                if(checkText(table.getText())){
                    List<XWPFTableRow> rows = table.getRows();
                    //遍历表格,并替换模板
                    eachTable(rows, textMap);
                }else{
                    insertTable(table, tableList);
                }
            }
        }
    }

    /**
     * 遍历表格
     * @param rows 表格行对象
     * @param textMap 需要替换的信息集合
     */
    public static void eachTable(List<XWPFTableRow> rows ,Map<String, String> textMap){
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                //判断单元格是否需要替换
                if(checkText(cell.getText())){
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (XWPFParagraph paragraph : paragraphs) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (XWPFRun run : runs) {
                            run.setText(changeValue(run.toString(), textMap),0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 为表格插入数据，行数不够添加新行
     * @param table 需要插入数据的表格
     * @param tableList 插入数据集合
     */
    public static void insertTable(XWPFTable table, List<String[]> tableList){
        //创建行,根据需要插入的数据添加新行，不处理表头
        for(int i = 1; i < tableList.size(); i++){
            XWPFTableRow row =table.createRow();
        }
        //遍历表格插入数据
        List<XWPFTableRow> rows = table.getRows();
        for(int i = 1; i < rows.size(); i++){
            XWPFTableRow newRow = table.getRow(i);
            List<XWPFTableCell> cells = newRow.getTableCells();
            for(int j = 0; j < cells.size(); j++){
                XWPFTableCell cell = cells.get(j);
                cell.setText(tableList.get(i-1)[j]);
            }
        }
    }



    /**
     * 判断文本中时候包含$
     * @param text 文本
     * @return 包含返回true,不包含返回false
     */
    public static boolean checkText(String text){
        boolean check  =  false;
        if(text.indexOf("$")!= -1){
            check = true;
        }
        return check;
    }

    /**
     * 匹配传入信息集合与模板
     * @param value 模板需要替换的区域
     * @param textMap 传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    public static String changeValue(String value, Map<String, String> textMap){
        Set<Map.Entry<String, String>> textSets = textMap.entrySet();
        for (Map.Entry<String, String> textSet : textSets) {
            //匹配模板与替换值 格式${key}
            String key = "${"+textSet.getKey()+"}";
            if(value.indexOf(key)!= -1){
                value = textSet.getValue();
            }
        }
        //模板未匹配到区域替换为空
        if(checkText(value)){
            value = "";
        }
        return value;
    }

    /**
     *
     *@templetStream 文档的输入流
     *@data 要替换的key,value的map，将文档中的@key@替换为value值
     *
     **/
    public static byte[] genWord2003ByTempletCell(InputStream templetStream, Map<String, String> data) throws IOException {
        XWPFDocument doc = new XWPFDocument(templetStream);

        // 替换段落中的指定文字
        Iterator<XWPFParagraph> itPara = doc.getParagraphsIterator();
        while (itPara.hasNext()) {
            XWPFParagraph paragraph = (XWPFParagraph) itPara.next();
            List<XWPFRun> runs = paragraph.getRuns();
            String beforeOneparaString="";
            for (int i=0; runs!=null&&i<runs.size(); i++) {
                String oneparaString = runs.get(i).getText(
                        runs.get(i).getTextPosition());
                for (Map.Entry<String, String> entry : data
                        .entrySet()) {
                    if(oneparaString != null){
                        if(oneparaString.contains(String.format("@%s@", entry.getKey()))){
                            oneparaString = oneparaString.replace(String.format("@%s@", entry.getKey()),
                                    Strings.nullToEmpty(entry.getValue()));
                        }else if(oneparaString.contains(entry.getKey()) && beforeOneparaString.endsWith("@")){
                            oneparaString = oneparaString.replace(entry.getKey(), Strings.nullToEmpty(entry.getValue()));
                            runs.get(i-1).setText(beforeOneparaString.substring(0,beforeOneparaString.length()-1), 0);
                            if(i+1<runs.size()){
                                String afterOneparaString=runs.get(i+1).getText(runs.get(i+1).getTextPosition());
                                runs.get(i+1).setText(afterOneparaString.substring(0,beforeOneparaString.length()-1), 0);
                            }
                        }

                    }
                }
                runs.get(i).setText(oneparaString, 0);
                beforeOneparaString=oneparaString;
            }
        }

        // 替换表格中的指定文字
        Iterator<XWPFTable> itTable = doc.getTablesIterator();
        while (itTable.hasNext()) {
            XWPFTable table = (XWPFTable) itTable.next();
            int rcount = table.getNumberOfRows();
            for (int i = 0; i < rcount; i++) {
                XWPFTableRow row = table.getRow(i);
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    //表格中处理段落（回车）
                    List<XWPFParagraph> cellParList= cell.getParagraphs();
                    for(int p=0; cellParList!=null&&p<cellParList.size();p++){ //每个格子循环
                        List<XWPFRun> runs = cellParList.get(p).getRuns(); //每个格子的内容都要单独处理
                        String beforeOneparaString="";
                        for (int q = 0; runs!=null&&q<runs.size(); q++) {
                            String oneparaString = runs.get(q).getText(runs.get(q).getTextPosition());
                            for (Map.Entry<String, String> entry : data.entrySet()) {
                                if(oneparaString!=null){
                                    if(oneparaString.contains(String.format("@%s@", entry.getKey()))){
                                        oneparaString = oneparaString.replace(String.format("@%s@", entry.getKey()),
                                                Strings.nullToEmpty(entry.getValue()));
                                    }else if(oneparaString.contains(entry.getKey()) && beforeOneparaString.endsWith("@")){
                                        oneparaString = oneparaString.replace(entry.getKey(),
                                                Strings.nullToEmpty(entry.getValue()));
                                        runs.get(q-1)
                                                .setText(beforeOneparaString.substring(0,beforeOneparaString.length()-1), 0);
                                        if(q+1<runs.size()){
                                            String afterOneparaString=runs
                                                    .get(q+1).getText(runs.get(q+1).getTextPosition());
                                            runs.get(q+1)
                                                    .setText(afterOneparaString.substring(0,beforeOneparaString.length()-1), 0);
                                        }
                                    }

                                }
                            }
                            runs.get(q).setText(oneparaString, 0);
                            beforeOneparaString=oneparaString;
                        }
                    }
                }
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (doc != null) {
            doc.write(os);
        }
        byte[] b = os.toByteArray();

        Closeables.close(templetStream, true);
        Closeables.close(os, true);
        return b;
    }

    /**
          *
          * @param srcPath word模板数据源路径
	 * @param destPath word导出路径
	 * @param map 关键字键值对映射
	 * @throws Exception
	 */
    public static void replaceWord(String srcPath, String destPath,Map<String, String> map) throws Exception {
        FileOutputStream out = null;
        FileInputStream input = null;
        try {
            if("doc".equals(srcPath.split("\\.")[1])) {
                input = new FileInputStream(new File(srcPath));
                HWPFDocument document = new HWPFDocument(input);
                Range range = document.getRange();
                for(Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getValue() == null) {
                        entry.setValue("  ");
                    }
                    range.replaceText(entry.getKey(), entry.getValue());
                }
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                out = new FileOutputStream(new File(destPath));
                document.write(out);
                out.write(ostream.toByteArray());
                out.flush();
            }else {
                XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(srcPath));
                // 替换段落中的指定文字
                Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
                while (itPara.hasNext()) {
                    XWPFParagraph paragraph = itPara.next();
                    List<XWPFRun> runs = paragraph.getRuns();
                    for (XWPFRun run : runs) {
                        String oneparaString = run.getText(run.getTextPosition());
                        if (StringUtils.isBlank(oneparaString)){
                            continue;
                        }
                        for (Map.Entry<String, String> entry :
                                map.entrySet()) {
                            oneparaString = oneparaString.replace(entry.getKey(), entry.getValue());
                        }
                        run.setText(oneparaString, 0);
                    }
                }
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                out = new FileOutputStream(new File(destPath));
                document.write(out);
                out.write(ostream.toByteArray());
                out.flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                out.close();
            }
            if(input != null) {
                input.close();
            }
        }
    }

    /**
     * 支持没有表格的数据doc
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        // 模板文件地址
        String inputUrl = "C:\\Users\\Lenovo\\Desktop\\万达\\12询价函及报价函标准文件（2016版）(模板）.doc";
        // 新生产的模板文件
        String outputUrl = "D:/tempalte.doc";
        Map<String,String> map = new HashMap<>();
        map.put("${name}","这个半成品");
        replaceWord(inputUrl,outputUrl,map);
//      InputStream input = new FileInputStream(new File(inputUrl));
//      genWord2003ByTempletCell(input,map);
//      Map<String, String> testMap = new HashMap<String, String>();
//      testMap.put("name", "杭州拱墅万达广场2020年下半年消防疏散演习舞台搭建");
//      List<String[]> testList = new ArrayList<String[]>();
//      changWord(inputUrl, outputUrl, testMap, testList);
    }
}


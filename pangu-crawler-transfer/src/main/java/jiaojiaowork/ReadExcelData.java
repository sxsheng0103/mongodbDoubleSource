package jiaojiaowork;

/**
 * @Author sheng.ding
 * @Date 2020/10/23 21:30
 * @Version 1.0
 **/
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * Author: 灵枢
 * Date: 2018/12/05
 * Time: 17:21
 * Description:读取Excel数据
 */
public class ReadExcelData {
    private static XSSFSheet sheet;
    /**
     * 构造函数，初始化excel数据
     * @param filePath  excel路径
     * @param sheetName sheet表名
     */
    ReadExcelData(String filePath, String sheetName){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
            sheet = sheets.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //打印excel数据
    public static void readExcelData(){
        //获取行数
        int rows = sheet.getPhysicalNumberOfRows();
        for(int i=0;i<rows;i++){
            //获取列数
            String rowstr = ""+(i+1)+"--";
            XSSFRow row = sheet.getRow(i);
            int columns = row.getPhysicalNumberOfCells();
            String xh = "";
            String xmmc = "";
            String xmtzms = "";
            String dw = "";
            String sl  = "";
            String dj = "";
            String zj = "";
            String tianshu = "";
            String note ="";
            for(int j=0;j<columns;j++){
                String cell = row.getCell(j).toString();
                if(j==0){
                    xh = cell.substring(0,cell.indexOf("."));
                }else if(j==1){
                    xmmc = cell;
                }else if(j==2){
                    xmtzms = cell;
                }else if(j==3){
                    dw = cell;
                }else if(j==4){
                    sl = cell.equals("")?"":cell.substring(0,cell.indexOf("."));
                }else if(j==5){
                    dj = cell;
                }else if(j==6){
                    zj = cell.equals("")?"":String.valueOf(new BigDecimal(new Double(Integer.valueOf(sl)*Double.valueOf(dj))).setScale(2, RoundingMode.UP));
                }else if(j==7){
                    tianshu = cell.equals("")?"":cell.substring(0,cell.indexOf("."));
                }else if(j==9){
                    note = cell.equals("")?"":cell;
                }
            }
            xmmc = xmmc;
            xmtzms = xmtzms;
            dw = dw;
            sl = sl;
            dj = dj;
            zj = zj;
            note = note;
            try{
               /* String outpath = "一、1号厅舞台（10.30-11.1）";
                String startm = "10";
                String startd = "30";
                String endm = "11";
                String endd = "01";
               String outpath = "二、1号门（10.30-11.1）";
                String startm = "10";
                String startd = "30";
                String endm = "11";
                String endd = "01";
                 String outpath = "三、1号门（11.6-8）";
                String startm = "11";
                String startd = "06";
                String endm = "11";
                String endd = "08";
                 String outpath = "四、3号门（10.30-11.1）";
                String startm = "10";
                String startd = "30";
                String endm = "11";
                String endd = "01";
                 String outpath = "五、3号门（11.6-8）";
                String startm = "11";
                String startd = "06";
                String endm = "11";
                String endd = "08";
                 String outpath = "六、演绎";
                String startm = "10";
                String startd = "30";
                String endm = "11";
                String endd = "01";*/
             /*   String outpath = "七、SP";
                String startm = "09";
                String startd = "17";
                String endm = "09";
                String endd = "20";*/
                String outpath = "七、SP";
                String startm = "09";
                String startd = "18";
                String endm = "09";
                String endd = "20";
                WordDocxReplaceWordContent.fillword(startm,startd,endm,endd,tianshu,xh,xmmc,xmtzms,dw,sl,dj,zj,tianshu,note,outpath);
            }catch (Exception e){
                System.out.println("转化失败1111111");
            }
            System.out.println(xh+":"+xmmc);
        }
    }
    //测试方法
    public static void main(String[] args){
        new ReadExcelData("C:\\Users\\Lenovo\\Desktop\\万达\\10.30-11.1日活动订单.xlsx", "Sheet1");
        readExcelData();
    }
}

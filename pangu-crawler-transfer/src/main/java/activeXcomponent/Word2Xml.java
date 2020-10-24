package activeXcomponent;

/**
 * @Author sheng.ding
 * @Date 2020/10/24 14:17
 * @Version 1.0
 **/
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Word2Xml {

    /**
     *
     * @Description:
     * @param filePath word目录
     * @param xmlFilePath 生成xml存放路径
     * @author Administrator
     */
    public static void wordToXml(String filePath,String xmlFilePath){
        try {
            ActiveXComponent app = new ActiveXComponent( "Word.Application"); //启动word
            app.setProperty("Visible", new Variant(false)); //为false时设置word不可见，为true时是可见要不然看不到Word打打开文件的过程
            Dispatch docs = app.getProperty("Documents").toDispatch();
            //打开编辑器
            Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] {filePath, new Variant(false), new Variant(true)} , new int[1]).toDispatch(); //打开word文档
            Dispatch.call(doc, "SaveAs", xmlFilePath, 11);//xml文件格式宏11
            Dispatch.call(doc, "Close", false);
            app.invoke("Quit",0);
            System.out.println("---------word转换完成--------");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

  public static void main(String[] args) {

    wordToXml(
        "C:\\Users\\Lenovo\\Desktop\\万达\\询价函及报价函标准文件（2016版）(模板）.docx",
        "C:\\Users\\Lenovo\\Desktop\\万达\\询价函及报价函标准文件（2016版）(模板）.ftl");
    //
  }
}
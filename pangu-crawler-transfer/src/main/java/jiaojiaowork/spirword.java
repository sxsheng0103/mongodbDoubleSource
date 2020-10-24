//package jiaojiaowork;
//
//import com.gargoylesoftware.htmlunit.javascript.host.dom.TextRange;
//import org.apache.poi.xwpf.usermodel.Document;
//import com.spire.doc.*;
//import com.spire.doc.documents.TextSelection;
//import com.spire.doc.fields.DocPicture;
//import com.spire.doc.fields.TextRange;
///**
// * @Author sheng.ding
// * @Date 2020/10/23 23:38
// * @Version 1.0
// **/
//public class spirword {
//
//    public static void replaceword {
//        //加载文档
//        Document doc = new Document("test.docx");
//        //查找需要替换的字符串
//        TextSelection[] textSelection = doc.findAllString("系统测试",true,false);
//        int index ;
//
//        //加载图片替换文本字符串
//        for (Object obj : textSelection) {
//            TextSelection Selection = (TextSelection)obj;
//            DocPicture pic = new DocPicture(doc);
//            pic.loadImage("tp.png");
//            TextRange range = Selection.getAsOneRange();
//            index = range.getOwnerParagraph().getChildObjects().indexOf(range);
//            range.getOwnerParagraph().getChildObjects().insert(index,pic);
//            range.getOwnerParagraph().getChildObjects().remove(range);
//        }
//        //保存文档
//        doc.saveToFile("ReplaceTextWithImage.docx", FileFormat.Docx_2013);
//        doc.dispose();
//    }
//}

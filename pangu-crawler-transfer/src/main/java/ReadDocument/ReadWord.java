package ReadDocument;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadWord {
	
	
	/**
	 * ��ȡword�ļ�����
	 * 
	 * @param path
	 * @return buffer
	 */
 
	public static String readWord(String path) {
		String buffer = "";
		try {
			if (path.endsWith(".doc")) {
				InputStream is = new FileInputStream(new File(path));
				WordExtractor ex = new WordExtractor(is);
				buffer = ex.getText();
//				ex��close()
			} else if (path.endsWith("docx")) {
				OPCPackage opcPackage = POIXMLDocument.openPackage(path);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
				buffer = extractor.getText();
//				extractor.close();
			} else if(path.endsWith(".txt")){
				String encoding="GBK";
                File file=new File(path);
                if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//���ǵ������ʽ
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	buffer+=lineTxt;
                    }
                    read.close();
                }
			} else if(path.endsWith(".pdf")){
	            PDDocument document = PDDocument.load(new File(path));
		        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		        stripper.setSortByPosition( true );
		        Rectangle2D rect = new Rectangle( 0, 0, 1575, 156000 );
		        stripper.addRegion( "class1", rect );
		        PDPageTree allPages = document.getDocumentCatalog().getPages();
		        PDPage firstPage  = null;
		        for(int i = 0;i<allPages.getCount();i++) {
		        	firstPage = (PDPage)allPages.get(i);
		        	stripper.extractRegions( firstPage );
		        }
		        buffer = stripper.getTextForRegion( "class1" );
		}
		} catch (Exception e) {
			System.out.println(path);
			e.printStackTrace();
		}
 
		return buffer;
	}
	
	
	/**

	    * ��ȡPDF�ļ�����������

	    * @param pdfPath

	    * @throws Exception

	    */

//	   public static String getTextFromPdf(String pdfPath) throws Exception {
//
//	      // �Ƿ�����
//
//	      boolean sort = false;
//
//	      // ��ʼ��ȡҳ��
//
//	      int startPage = 1;
//
//	      // ������ȡҳ��
//
//	      int endPage = Integer.MAX_VALUE;    
//
//	     
//
//	      String content = null;
//
//	      InputStream input = null;
//
//	      File pdfFile = new File(pdfPath);
//
//	      PDDocument document = null;
//
//	      try {
//
//	         input = new FileInputStream(pdfFile);
//
//	         // ���� pdf �ĵ�
//
//	         PDFParser parser = new PDFParser(input);
//
//	         parser.parse();
//
//	         document = parser.getPDDocument();
//
//	         // ��ȡ������Ϣ
//
//	         PDFTextStripper pts = new PDFTextStripper();
//
//	         pts.setSortByPosition(sort);
//
//	         endPage = document.getNumberOfPages();
//
//	         System.out.println("Total Page: " + endPage);
//
//	         pts.setStartPage(startPage);
//
//	         pts.setEndPage(endPage);
//
//	         try {
//
//	            content = pts.getText(document);
//
//	         } catch (Exception e) {
//
//	            throw e;
//
//	         }
//
//	         System.out.println("Get PDF Content ...");
//
//	      } catch (Exception e) {
//
//	         throw e;
//
//	      } finally {
//
//	         if (null != input)
//
//	            input.close();
//
//	         if (null != document)
//
//	            document.close();
//
//	      }
//
//	     System.out.println(content);
//
//	      return content;
//
//	   }
	
//	public static String readpdf(String inputFile){
//		
//        //�����ĵ�����
//        PDDocument doc =null;
//        String content="";
//        try {
//            //����һ��pdf����
//            doc =PDDocument.load(new File(inputFile));
//            //��ȡһ��PDFTextStripper�ı��������  
//            PDFTextStripper textStripper =new PDFTextStripper("GBK");
//            content=textStripper.getText(doc);
//            
//            System.out.println(content);
//            //�ر��ĵ�
//            doc.close();
//            return content;  
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//		return content;
//    }
 
	
//	public static Map<String ,Object> readPdf(File file){
//		Map<String ,Object> map=new HashMap<String ,Object>();
//		List<String> pdfLines=new ArrayList<String>();
//	try {
//		PDDocument document = PDDocument.load(file);
//		int pages = document.getNumberOfPages();
//		//�Լ��������䣬���ļ�����ȫ����������
//		Rectangle rectBase = new Rectangle(0, 0, 682, 800);
//		List<PDPage> allPages = (List) document.getDocumentCatalog().getPages();
//		for (int i = 0; i < pages; i++) {
////			PDPage page = document.getpage(i);
//			PDPage page = allPages.get(i);
//			PDFTextStripperByArea stripper;
//			stripper = new PDFTextStripperByArea();
//			stripper.setSortByPosition(true);
//			stripper.addRegion("base", rectBase);
//			stripper.extractRegions(page);
//			//��ȡÿһҳ��������Ϣ
//			String lines = stripper.getTextForRegion("base");
//			pdfLines.add(lines);
//		}
//		System.out.println(pdfLines);
//		map.put("ok", true);
//		map.put("pdfLines", pdfLines);
//	}catch (Exception e) {
//		e.printStackTrace();
//	}finally {
//		return map;
//	}
//	}
	
	
//	public static  String getTextFromPDF(String pdfFilePath)   
//    {  
//		
//        String result = null;  
//        FileInputStream is = null;  
//        PDDocument document = null;  
//        try {  
//            is = new FileInputStream(pdfFilePath);
//            PDFParser parser = new PDFParser(is);
//            parser.parse();
//            document = parser.getPDDocument();
//            PDFTextStripper stripper = new PDFTextStripper();
//            result = stripper.getText(document);
//            System.out.println(result);
//        } catch (FileNotFoundException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        } catch (IOException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        } catch (Exception e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        }finally {  
//            if (is != null) {
//                try {  
//                    is.close();  
//                } catch (IOException e) {  
//                    // TODO Auto-generated catch block  
//                    e.printStackTrace();  
//                }  
//            }  
//            if (document != null) {  
//                try {  
//                    document.close();  
//                } catch (IOException e) {  
//                    // TODO Auto-generated catch block  
//                    e.printStackTrace();  
//                }  
//            }  
//        }  
//        return result;  
//    }

		 
	    public  List<String> list = new ArrayList<String>();
	    public  List<String> listPattern = new ArrayList<String>();
	 
	  
	 
	
//	    public static  Map<String, String> convertReportToDb(File file,
//	            boolean override) throws Exception {
//	        if (null == file || !file.exists()) {
//	            return null;
//	        }
//	        Map<String, String> paramMap = null;
//	        try {
//	            String text = extractText(file);
//	            if (StringUtils.isBlank(text)) {
//	                return null;
//	            }
//	            paramMap = extractParams(file.getName(), text);
//	            //�������õļ�
//	            if(paramMap.containsKey("�������������ල")){
//	                String remove = paramMap.remove("�������������ල");
//	            }
//	            if(paramMap.containsKey("������Ƭ")){
//	                String remove = paramMap.remove("������Ƭ");
//	            }
////	            System.out.println(paramMap);
//	            System.out.println(paramMap);
//	            if (null == paramMap) {
//	                return null;
//	            }
//	            return paramMap;
//	        } catch (Exception ex) {
//	            // ��doc�ļ�������ȡʧ�ܣ�����jacob��ʽ�ٴγ�����ȡ������
//	        }
//	        return null;
//	    }
	 
	    
//	    public static  String extractPdfText(File file) throws Exception {
//	        if (null == file || !file.exists()) {
//	            return null;
//	        }
//	        FileInputStream is = new FileInputStream(file);
//	        PDDocument pdfDocument = null;
//	        String text = null;
//	        try {
//	            PDFTextStripper stripper = new PDFTextStripper();
//	            pdfDocument = PDDocument.load(is);
//	            StringWriter writer = new StringWriter();
//	            stripper.writeText(pdfDocument, writer);
//	            text = writer.getBuffer().toString();
//	            if (null == text) {
//	                return null;
//	            }
//	            text = text.trim();
//	            is.close();
//	            System.out.println(text);
//	        } catch (java.io.IOException ex) {
//	            throw ex;
//	        } finally {
//	            if (pdfDocument != null) {
//	                // org.pdfbox.cos.COSDocument cos = pdfDocument.getDocument();
//	                // cos.close();
//	                pdfDocument.close();
//	            }
//	        }
//	        // �����пհ��滻Ϊ�����ո�
//	        Pattern blankPattern = Pattern.compile("\\s+|\t|\r|\n");
//	        Matcher m = blankPattern.matcher(text);
//	        String strNoBlank = m.replaceAll(" ");
//	        return strNoBlank;
//	    }
//	    public static Map<String, String> extractParams(String filename, String text)
//	            throws Exception {
//	        if (StringUtils.isBlank(text)) {
//	            return null;
//	        }
//	        Map<String, String> result = new LinkedHashMap<String, String>();
//	        result.put("filename", filename);
//	        return result;
//	    }
	 

}

package utils;

//import org.apache.batik.transcoder.Transcoder;
//import org.apache.batik.transcoder.TranscoderException;
//import org.apache.batik.transcoder.TranscoderInput;
//import org.apache.batik.transcoder.TranscoderOutput;
//import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;

/**
 * @Author sheng.ding
 * @Date 2020/11/23 22:09
 * @Version 1.0
 **/
public class SvgToPngUtil {

    //svg转为png
    /*public static void convertSvg2Png(File svg, File png) throws IOException, TranscoderException
    {

        InputStream in = new FileInputStream(svg);
        OutputStream out = new FileOutputStream(png);
        out = new BufferedOutputStream(out);

        Transcoder transcoder = new PNGTranscoder();
        try {
            TranscoderInput input = new TranscoderInput(in);
            try {
                TranscoderOutput output = new TranscoderOutput(out);
                transcoder.transcode(input, output);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
    public static void convertSvg2Png1(TranscoderInput input, File png) throws IOException, TranscoderException
    {
                try{
                    System.out.println(png.getAbsolutePath());
                    OutputStream out = new FileOutputStream(png);
                    out = new BufferedOutputStream(out);

                    Transcoder transcoder = new PNGTranscoder();
                    TranscoderOutput output = new TranscoderOutput(out);
                    transcoder.transcode(input, output);
                }catch (Exception e){

                }

    }*/

    public static void transferSVGTOPNG(String path,String svg)throws UnsupportedEncodingException,Exception{
        /*byte[] bytes = svg.getBytes("utf-8");
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
        convertSvg2Png1(input, new File(path));*/

       /* File f=new File("E:/Pinterest_pinterest30.svg");
        File destFile=new File("E:/Pinterest_pinterest30.png");

        try {
            convertSvg2Png(f, destFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TranscoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

    }
}

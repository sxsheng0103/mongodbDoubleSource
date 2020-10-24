package ScreenShot;

import com.google.common.base.Preconditions;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import lombok.extern.slf4j.Slf4j;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.*;
@Slf4j
public class ScreenCapture {
    /**
     * 获取剪贴板中的内容
     * @return
     */
    public static void getSysClipboardText(){
        String ret = "";
        /*Robot robot = new Robot();
        robot.delay(100);*/     //可以使键盘时间推迟100ms
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(ret);
    }


    private ScreenCapture() {
    }
    private static final String DEFAULT_IMAGE_FORMAT = "jpg";
    private static GraphicsDevice GRAPH_DEVICE = null;
    private static Robot ROBOT = null;

    static {
        try {
            long start = System.currentTimeMillis();
            Preconditions.checkArgument(System.currentTimeMillis() - start < 1000l);
            GRAPH_DEVICE = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            ROBOT = new Robot(GRAPH_DEVICE);
        } catch (Exception e) {
            log.error("GRAPH_DEVICE/ROBOT CREATE FAIL!!!");
        }
    }

    public static String capture() throws Exception {
        if (GRAPH_DEVICE == null) {
            throw new IllegalStateException("GRAPH_DEVICE IS NULL!!!");
        }
        if (ROBOT == null) {
            throw new IllegalStateException("ROBOT IS NULL!!!");
        }
        BufferedImage image = ROBOT.createScreenCapture(
                new Rectangle(0, 0, GRAPH_DEVICE.getDisplayMode().getWidth(), GRAPH_DEVICE.getDisplayMode().getHeight()));
        String fileName = String.format("%s_%s_%s_%s.%s",
                "nsrsbh", "sz","business", "now", DEFAULT_IMAGE_FORMAT);
        String USER_HOME = System.getProperty("user.home");

         String SCREENS = USER_HOME + File.separator + "pangu-screens";
        String FILES = USER_HOME + File.separator + "pangu-files";
        File dir = new File(FILES);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
            } else {
            }
        }else{
            new File(SCREENS + File.separator + fileName);
        }
        File file = new File(FILES + File.separator + fileName);
        if (file == null) {
            throw new Exception("CAPTURE FILE IS NULL!!!");
        }
        try{
            byteToFile(wirteJPEGBytes(image),file.getAbsolutePath());
//            ImageIO.write(image, captureImageFormat, file);
        }catch (Exception e){
            throw new RuntimeException("截屏发生了异常信息"+e.getClass().getName());
        }
        return Base64ToFile.get(file);
    }


    public static byte[] wirteJPEGBytes(BufferedImage source){
        try{
            if(null==source)
                throw new NullPointerException();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            JPEGImageEncoder jencoder = JPEGCodec.createJPEGEncoder(output);
            JPEGEncodeParam param = jencoder.getDefaultJPEGEncodeParam(source);
            param.setQuality(0.75f, true);
            jencoder.setJPEGEncodeParam(param);
            try {
                jencoder.encode(source);
            } catch (ImageFormatException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return output.toByteArray();
        }catch (Exception e){
            throw new RuntimeException("图片写入文件流出错!");
        }

    }

    public static void byteToFile(byte[] bytes, String path)
    {
        try
        {
            File localFile = new File(path);
            if (!localFile.exists())
            {
                localFile.createNewFile();
            }
            OutputStream os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();
        }
        catch (Exception e)
        {
            new RuntimeException("文件流转化异常!");
        }
    }

  public static void main(String[] args) throws Exception{
    //
      String capture = ScreenCapture.capture();
  }
}
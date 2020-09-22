package com.pangu.crawler.transfer.utils;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用javacv进行录屏
 */
public class VideoRecode {
    //线程池 screenTimer
    private ScheduledThreadPoolExecutor screenTimer;
    //获取屏幕尺寸
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private Rectangle rectangle;
    //视频类 FFmpegFrameRecorder
    private FFmpegFrameRecorder recorder;
    private Robot robot;
    //线程池 exec
    private ScheduledThreadPoolExecutor exec;
    private TargetDataLine line;
    private AudioFormat audioFormat;
    private DataLine.Info dataLineInfo;
    private boolean isHaveDevice = true;
    private long startTime = 0;
    private long videoTS = 0;
    private long pauseTime = 0;
    private double frameRate = 24;

    public VideoRecode(String fileName, boolean isHaveDevice,Rectangle rectangle1) {
        rectangle = rectangle1;
        // TODO Auto-generated constructor stub
        System.out.println(rectangle1.getWidth());
        recorder = new FFmpegFrameRecorder(fileName + ".mp4", (int)rectangle1.getWidth(), (int)rectangle1.getHeight());
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H265); // 28
        // recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // 28
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
        recorder.setFormat("mp4");
        // recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
        recorder.setSampleRate(44100);
        recorder.setFrameRate(frameRate);

        recorder.setVideoQuality(0);
        recorder.setVideoOption("crf", "23");
        // 2000 kb/s, 720P视频的合理比特率范围
        recorder.setVideoBitrate(1000000);
        /**
         * 权衡quality(视频质量)和encode speed(编码速度) values(值)： ultrafast(终极快),superfast(超级快),
         * veryfast(非常快), faster(很快), fast(快), medium(中等), slow(慢), slower(很慢),
         * veryslow(非常慢)
         * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
         * 参考：https://trac.ffmpeg.org/wiki/Encode/H.264 官方原文参考：-preset ultrafast as the
         * name implies provides for the fastest possible encoding. If some tradeoff
         * between quality and encode speed, go for the speed. This might be needed if
         * you are going to be transcoding multiple streams on one machine.
         */
        recorder.setVideoOption("preset", "slow");
        recorder.setPixelFormat(0); // yuv420p = 0
        recorder.setAudioChannels(2);
        recorder.setAudioOption("crf", "0");
        // Highest quality
        recorder.setAudioQuality(0);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            recorder.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.print("*******************************");
        }
        this.isHaveDevice = isHaveDevice;
    }

    /**
     * 开始录制
     */
    public void start() {

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        if (pauseTime == 0) {
            pauseTime = System.currentTimeMillis();
        }
        // 如果有录音设备则启动录音线程
        if (isHaveDevice) {
            new Thread(new Runnable() {

                public void run() {
                    // TODO Auto-generated method stub
//                    caputre();
                }
            }).start();

        }

        // 录屏
        screenTimer = new ScheduledThreadPoolExecutor(1);
        screenTimer.scheduleAtFixedRate(new Runnable() {

            public void run() {

                // 将screenshot对象写入图像文件
                // try {
                // ImageIO.write(screenCapture, "JPEG", f);
                // videoGraphics.drawImage(screenCapture, 0, 0, null);
                // IplImage image = cvLoadImage(name); // 非常吃内存！！
                // // 创建一个 timestamp用来写入帧中
                // videoTS = 1000
                // * (System.currentTimeMillis() - startTime - (System.currentTimeMillis() -
                // pauseTime));
                // // 检查偏移量
                // if (videoTS > recorder.getTimestamp()) {
                // recorder.setTimestamp(videoTS);
                // }

                BufferedImage screenCapture = robot.createScreenCapture(rectangle); // 截屏

                BufferedImage videoImg = new BufferedImage((int)rectangle.getWidth(), (int)rectangle.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR); // 声明一个BufferedImage用重绘截图

                Graphics2D videoGraphics = videoImg.createGraphics();// 创建videoImg的Graphics2D

                videoGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
                videoGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_SPEED);
                videoGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                videoGraphics.drawImage(screenCapture, 0, 0, null); // 重绘截图

                Java2DFrameConverter java2dConverter = new Java2DFrameConverter();

                Frame frame = java2dConverter.convert(videoImg);
                try {
                    videoTS = 1000L
                            * (System.currentTimeMillis() - startTime - (System.currentTimeMillis() - pauseTime));
                    // 检查偏移量
                    if (videoTS > recorder.getTimestamp()) {
                        recorder.setTimestamp(videoTS);
                    }
                    recorder.record(frame); // 录制视频
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 释放资源
                videoGraphics.dispose();
                videoGraphics = null;
                videoImg.flush();
                videoImg = null;
                java2dConverter = null;
                screenCapture.flush();
                screenCapture = null;

            }
        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);

    }

    /**
     * 抓取声音
     */
    public void caputre() {
        audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
        dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException e1) {
            // TODO Auto-generated catch block
            System.out.println("#################");
        }
        try {
            line.open(audioFormat);
        } catch (LineUnavailableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        line.start();

        final int sampleRate = (int) audioFormat.getSampleRate();
        final int numChannels = audioFormat.getChannels();

        int audioBufferSize = sampleRate * numChannels;
        final byte[] audioBytes = new byte[audioBufferSize];

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {

            public void run() {
                try {
                    int nBytesRead = line.read(audioBytes, 0, line.available());
                    int nSamplesRead = nBytesRead / 2;
                    short[] samples = new short[nSamplesRead];

                    // Let's wrap our short[] into a ShortBuffer and
                    // pass it to recordSamples
                    ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                    ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                    // recorder is instance of
                    // org.bytedeco.javacv.FFmpegFrameRecorder
                    recorder.recordSamples(sampleRate, numChannels, sBuff);
                    // System.gc();
                } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }, (int) (1000 / frameRate), (int) (1000 / frameRate), TimeUnit.MILLISECONDS);
    }

    /**
     * 停止
     */
    public void stop() {
        if (null != screenTimer) {
            screenTimer.shutdownNow();
        }
        try {
            recorder.stop();
            recorder.release();
            recorder.close();
            screenTimer = null;
            // screenCapture = null;
            if (isHaveDevice) {
                if (null != exec) {
                    exec.shutdownNow();
                }
                if (null != line) {
                    line.stop();
                    line.close();
                }
                dataLineInfo = null;
                audioFormat = null;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 暂停
     *
     * @throws Exception
     */
    public void pause() throws Exception {
        screenTimer.shutdownNow();
        screenTimer = null;
        if (isHaveDevice) {
            exec.shutdownNow();
            exec = null;
            line.stop();
            line.close();
            dataLineInfo = null;
            audioFormat = null;
            line = null;
        }
        pauseTime = System.currentTimeMillis();
    }


}
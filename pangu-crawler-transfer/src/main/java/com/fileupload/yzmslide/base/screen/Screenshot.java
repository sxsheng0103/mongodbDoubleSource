package com.fileupload.yzmslide.base.screen;


import com.fileupload.yzmslide.base.log.Log;
import com.fileupload.yzmslide.base.screen.Screenshot;
import java.io.File;

public class Screenshot {
    private Screenshot() {
    }

    private static final int MAX = 500;

    public static final String NIRCMD_FILE = "C://nircmd.exe";

    public static boolean nircmdExist() {
        return (new File(NIRCMD_FILE)).exists();
    }

    public static boolean screenshot(String file) {
        // String cmd = String.format("CMD /C START %s savescreenshot %s", NIRCMD_FILE, file);
        String cmd = String.format("%s savescreenshot %s", NIRCMD_FILE, file);
        return screenshot(false, file, cmd);
    }

    public static boolean screenshot(String file, int x, int y, int width, int height) {
        // String cmd = String.format("CMD /C START %s savescreenshot %s %d %d %d %d", NIRCMD_FILE, file, x, y, width, height);
        String cmd = String.format("%s savescreenshot %s %d %d %d %d", NIRCMD_FILE, file, x, y, width, height);
        return screenshot(true, file, cmd);
    }

    private static boolean screenshot(boolean range, String file, String cmd) {
        Log.out("screenshot " + (range ? "range" : "all") + "!");
        Log.out("screenshot output file : " + file);
        Log.out("screenshot cmd : " + cmd);
        if (!nircmdExist()) {
            Log.out("screenshot fail : [" + NIRCMD_FILE + "] not exist!");
            return false;
        }
        ProcessThread processThread = new ProcessThread(cmd);
        processThread.start();
        int count = 0;
        boolean found = false;
        while (!found && count <= MAX) {
            if (!(new File(file)).exists()) {
                count++;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
                Log.out("screenshot continue, %s not exist! count = %d", file, count);
            } else {
                found = true;
                Log.out("screenshot continue, %s exist! count = %d", file, count);
            }
        }
        Log.out("screenshot success! count = %d, found = %s", count, found);
        processThread.end();
        return found;
    }

    private static class ProcessThread extends Thread {
        private final String cmd;

        private Process process;

        public ProcessThread(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                process = Runtime.getRuntime().exec(cmd);
                Log.out("screenshot cmd runtime process run : " + process);
            } catch (Exception e) {
                Log.error(e, "screenshot fail!");
            }
        }

        public void end() {
            if (process == null) {
                Log.out("screenshot cmd runtime process is null!");
            } else {
                int count = 0;
                while (process.isAlive()) {
                    if (count >= MAX) {
                        Log.out("screenshot cmd runtime process still alive, but count overflow : " + count);
                        break;
                    }
                    Log.out("screenshot cmd runtime process still alive, count = " + count);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    count++;
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                count = 0;
                while (process.isAlive()) {
                    if (count >= MAX) {
                        Log.out("screenshot cmd runtime process destroy : " + process + ", but count overflow : " + count);
                        break;
                    }
                    Log.out("screenshot cmd runtime process destroy : " + process + ", count = " + count);
                    process = process.destroyForcibly();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    count++;
                }
                Log.out("screenshot cmd runtime process end : " + process);
            }
        }
    }
}
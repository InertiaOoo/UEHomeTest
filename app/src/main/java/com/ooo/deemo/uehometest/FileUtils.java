package com.ooo.deemo.uehometest;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static java.lang.System.out;

/**
 * Author by Deemo, Date on 2019/5/8.
 * Have a good day
 */
public class FileUtils {


    public  static void saveFile(List<TestLog> logList){

        File file0 = Environment.getExternalStoragePublicDirectory("testlog.txt");

        String filePath = "/sdcard/UTest/";
        File file1 = new File(filePath);
        file1.mkdir();

        File file = new File(filePath+"testLog.txt");


        if(file.exists()){

            file.delete();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                for (TestLog testLog : logList) {
                    fos.write(testLog.getID().getBytes("UTF-8"));
                    fos.write(testLog.getLogmsg().getBytes("UTF-8"));
                    fos.write("\r\n".getBytes());
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {


        }

    }

    public  static void saveFile(Bitmap bp, String name, Context context){

        String filePath = "/sdcard/UTest/";
        File file1 = new File(filePath);
        if (file1.exists()) {


        }else {
            file1.mkdir();
        }

        File file = new File(filePath + name);
        if(file.exists()){

            file.delete();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
                if(bp.compress(Bitmap.CompressFormat.JPEG, 90, fos))
                {
                    fos.flush();
                    fos.close();
// 插入图库
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), name, null);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {


        }


    }


    private void initData() {
        String filePath = "/sdcard/Test/";
        String fileName = "log.txt";

        writeTxtToFile("txt content", filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }




//    public void saveFile(List<TestLog> logList){
//
//
//
//        File file = Environment.getExternalStoragePublicDirectory("testlog");
//        if(file.exists()){
//
//            file.delete();
//        }
//
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//                FileOutputStream fos = new FileOutputStream(file);
//                for (TestLog testLog : logList) {
//                    fos.write(testLog.getID().getBytes("UTF-8"));
//                    fos.write(testLog.getLogmsg().getBytes("UTF-8"));
//                    fos.write("\r\n".getBytes());
//                }
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else {
//
//
//
//        }
//
//    }
}

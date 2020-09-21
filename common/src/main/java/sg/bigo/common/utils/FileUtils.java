package sg.bigo.common.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private String path = Environment.getExternalStorageDirectory().toString() + "/shidoe";
 
    public FileUtils() {
        File file = new File(path);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
            file.mkdirs();
        }
    }
 
    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public File createFile(String FileName) throws IOException {
        File file = new File(path, FileName);
        if(!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
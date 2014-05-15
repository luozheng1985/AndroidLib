package com.androidlib.httpUtils;

import java.io.File;

public class FileBodyModel {

    public static final class FileMimeType{
        public static final String IMAGE = "image/*";
        public static final String VEDIO = "vedio/*";
        public static final String AUDIO = "audio/*";
        public static final String TEXT = "text/*";
    }
    
    /** 需要上传的本地文件 */
    private File file;
    /** 文件类型    */
    private String mimeType;
    /** 文件在远程服务器上的文件名   */
    private String remoteFileName;
    private String charset = "utf-8";
    
    public FileBodyModel(File file, String mimeType, String remoteFileName) {
        super();
        this.file = file;
        this.mimeType = mimeType;
        this.remoteFileName = remoteFileName;
        charset = "utf-8";
    }

    public File getFile() {
        return file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public String getCharset() {
        return charset;
    }
    
}

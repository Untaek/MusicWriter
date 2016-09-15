package com.limwoon.musicwriter.parse;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by ejdej on 2016-08-02.
 */


// 안드로이드 내부 저장소에서 xml 파일 가져오는 클래스 없을시 null


public class FileGetter {

    private String filePath;
    private File xmlFile;
    private FileInputStream fileIS;
    private Context context;

    public FileGetter(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public InputStream getFileIS() {
        return fileIS;
    }

    public void setFileIS(FileInputStream fileIS) {
        this.fileIS = fileIS;
    }

    private void init(){
        try {
            fileIS = context.openFileInput(filePath);
        }catch (FileNotFoundException e){
            fileIS = null;
        }
    }

    public FileInputStream getXml(){
        init();
        if (fileIS !=null){
            return fileIS;
        }
        return null;
    }
}

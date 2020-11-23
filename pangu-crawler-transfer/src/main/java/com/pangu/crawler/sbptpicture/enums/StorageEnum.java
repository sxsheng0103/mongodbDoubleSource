package com.pangu.crawler.sbptpicture.enums;

import javax.validation.constraints.NotNull;
public enum StorageEnum{

    serverdisk("1","服务器磁盘",1);
    private String type;
    private String info;
    private int value;

    StorageEnum(@NotNull String type, @NotNull String info, @NotNull int value){
        this.type = type;
        this.info = info;
        this.value = value;
    }


    public String getType(){return  this.type;}
    public String getInfo(){return  this.info;}
    public Integer getValue(){return  this.value;}

    @Override
    public String toString() {
        return "StorageEnum{" +
                "type='" + type + '\'' +
                ", info='" + info + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
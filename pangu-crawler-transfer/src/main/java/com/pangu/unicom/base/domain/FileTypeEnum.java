package com.pangu.unicom.base.domain;

import java.util.Optional;

public enum FileTypeEnum implements BaseEnum {
    // excel
    EXCEL("excel", "xlsx"),
    // word
    WORD("word", "docx"),
    // pdf
    PDF("pdf", "pdf"),
    // png
    PNG("png", "png"),
    // jpg
    JPG("jpg", "jpg");

    public static Optional<FileTypeEnum> match(String fileType) {
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (fileTypeEnum.getCode().equals(fileType)) {
                return Optional.of(fileTypeEnum);
            }
        }
        return Optional.empty();
    }

    private String code;

    private String suffix;

    FileTypeEnum(String code, String suffix) {
        this.code = code;
        this.suffix = suffix;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return "FileTypeEnum{" +
                "code='" + code + '\'' +
                ", suffix='" + suffix + '\'' +
                '}';
    }
}

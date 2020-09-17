package com.pangu.crawler.ocr;

import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

@Data
@ResponseBody
public class LianZhongResponseEntity {
    private Datavo data;
    private String message;
    private String code;
}

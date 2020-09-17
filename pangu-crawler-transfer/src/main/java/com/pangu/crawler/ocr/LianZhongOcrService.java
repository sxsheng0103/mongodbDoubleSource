package com.pangu.crawler.ocr;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LianZhongOcrService {

    public String base64Discern(String imgBase64){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://v2-api.jsdama.com/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LianZhongEntity lianZhongEntity = new LianZhongEntity();
        lianZhongEntity.setCaptchaData(imgBase64);
        HttpEntity request = new HttpEntity<>(lianZhongEntity, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        LianZhongResponseEntity lianZhongResponseEntity = JSON.parseObject(response.getBody().toString(), LianZhongResponseEntity.class);
        return lianZhongResponseEntity.getData().getRecognition();
    }
}

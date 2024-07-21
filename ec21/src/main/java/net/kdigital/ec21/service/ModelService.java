package net.kdigital.ec21.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.modelDTO.Lstm;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {
    private final RestTemplate restTemplate;

    // lstm server 경로
    @Value("${lstm.predict.server}")
    String lstmUrl;

    public List<Map<String, Object>> predictLSTM(Lstm lstm) {
        List<Map<String, Object>> error = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            ResponseEntity<List> response = restTemplate.postForEntity(lstmUrl, lstm, List.class);
            result = response.getBody();
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("statusCode", "450");
            error.add(map);
            return error;
        }
        return result;
    }
}

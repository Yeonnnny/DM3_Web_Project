package net.kdigital.ec21.service;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.kdigital.ec21.repository.CustomerRepository;
import net.kdigital.ec21.repository.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ManagerBoardService {
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public List<Map<String, Object>> getRegisterProductCount() {
        List<Object[]> results = productRepository.countProductsByCreationDate();
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("createDate", result[0]); // Assuming result[0] is the createDate
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Integer>> getCategoryData() {
        List<Object[]> results = productRepository.findCategoryCounts();
        Map<String, Map<String, Integer>> categoryData = new HashMap<>();

        for (Object[] result : results) {
            String category = (String) result[0];
            Integer judge0Count = ((Number) result[1]).intValue();
            Integer judge1Count = ((Number) result[2]).intValue();

            Map<String, Integer> counts = new HashMap<>();
            counts.put("이상", judge0Count);
            counts.put("정상", judge1Count);

            categoryData.put(category, counts);
        }

        return categoryData;
    }

    // 특정기간에 다른 케테고리 데이터 반환
    public List<Map<String, Object>> getCategoryDataByDateRange(String start, String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        // LocalDate를 LocalDateTime으로 변환하여 자정 시간을 사용합니다.
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // 다음 날 자정을 끝으로 사용합니다.

        List<Object[]> results = productRepository.countProductsByCategoryAndDateRange(startDateTime, endDateTime);
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", result[0]); // 날짜
                    map.put("category", result[1]); // 카테고리
                    map.put("count", result[2]); // 갯수
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopSimilarWordCounts() {
        int pageSize = 5; // 상위 3개만 가져옴
        Pageable pageable = PageRequest.of(0, pageSize, Sort.Direction.DESC, "count");

        Page<Object[]> results = productRepository.countSimilarWords(pageable);
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("similarWord", result[0]);
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    // 240427 dy : 5일 동안 각 일별 등록 회원 수, 등록 상품 수
    public List<Map<String, Object>> getRegistCustomerAndProuctCountsForFiveDays() {
        // 반환할 결과 담을 List
        List<Map<String, Object>> result = new ArrayList<>();

        // 시스템상 당일 정보
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        // 데이터 포맷 형식
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd (E)", Locale.KOREAN);

        // 7일 동안의 값 (ACS)
        // for (int i = 1; i <= 7; i++) {
        for (int i = 7; i >= 1; i--) {
            // count
            Long productCnt = productRepository.countByCreateDateBetween(todayStart.minusDays(i),
                    todayEnd.minusDays(i));
            Long customerCnt = customerRepository.countByCreateDateBetween(todayStart.minusDays(i),
                    todayEnd.minusDays(i));
            // date
            String formattedDate = todayStart.minusDays(i).format(formatter);

            Map<String, Object> data = new HashMap<>();
            data.put("date", formattedDate);
            data.put("productCnt", productCnt);
            data.put("customerCnt", customerCnt);

            result.add(data);
        }
        return result;
    }

}

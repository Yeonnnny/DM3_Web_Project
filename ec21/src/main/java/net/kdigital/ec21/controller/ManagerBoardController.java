package net.kdigital.ec21.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.service.ManagerBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ManagerBoardController {
    private final ManagerBoardService managerBoardService;

    // 상품등록일자별 카운트하여 linechart data에 Map형식으로 전달
    @ResponseBody
    @GetMapping("/manager/board/registerProductCount")
    public List<Map<String, Object>> registerProductCount() {
        List<Map<String, Object>> result = managerBoardService.getRegisterProductCount();
        return result;
    }

    // 카테고리별 장상/이상 개수 카운트하여 barchart data에 Map형식으로 전달
    @ResponseBody
    @GetMapping("/manager/board/categoryProductCount")
    public Map<String, Map<String, Integer>> categoryData() {
        Map<String, Map<String, Integer>> result = managerBoardService.getCategoryData();
        return result;
    }

    @ResponseBody
    @GetMapping("/manager/board/categoryProductCountByDateRange")
    public List<Map<String, Object>> categoryProductCountByDateRange(
            @RequestParam(name = "startDate") String start,
            @RequestParam(name = "endDate") String end) {
        log.info("Start Date: " + start + start.getClass().getName());
        log.info("End Date: " + end + end.getClass().getName());

        return managerBoardService.getCategoryDataByDateRange(start, end);
    }

    @ResponseBody
    @GetMapping("/manager/board/topSimilarWordCounts")
    public List<Map<String, Object>> getTopSimilarWordCounts() {
        return managerBoardService.getTopSimilarWordCounts();
    }

    // 240427 dy : 이게 맞을랑가 모르겠지만.. 참고해주세욥
    @ResponseBody
    @GetMapping("/manager/board/getRegistCustomerAndProuctCountsForFiveDays")
    public List<Map<String, Object>> getRegistCustomerAndProuctCountsForFiveDays() {
        return managerBoardService.getRegistCustomerAndProuctCountsForFiveDays();
    }

}

package net.kdigital.ec21.controller;

import java.util.List;

import net.kdigital.ec21.dto.ProductDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.service.InquiryService;
import net.kdigital.ec21.service.ProductService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
	private final ProductService productService;
	private final InquiryService inquiryService;

	//================= main/index.html =====================
	/**
	 * 첫 화면 및 메인 페이지 화면 요청
	 * @return
	 */
	@GetMapping({ "", "/" })
	public String index(Model model) {
		// hitCount기준 DESC, createDate 기준 DESC 순으로 상위 8개를 가져옴
		// (judge=='Y' && customerId에 해당하는 Customer의 blacklistCheck=='N'인 데이터들 중에서) 
		List<ProductDTO> dtoList = productService.getTopProductList();
		model.addAttribute("list", dtoList);
		return "main/index";
	}

	/**
	 * ajax - 전달받은 사용자ID가 받은 인콰이어리 중에 읽지 않은 인콰이어리 개수 요청
	 * @param customerId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/main/index/getInboxCount")
	public int getInboxCount(@RequestParam(name = "customerId")String customerId){
		int inboxCount = inquiryService.getInboxNotReadCount(customerId);
		return inboxCount;
	}

	/**
	 * ajax : 상품을 올린 고객의 나라 반환
	 * @param productId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/main/index/getCountry")
	public String getMethodName(@RequestParam(name = "productId") String productId) {
		String country = productService.getCustomerCountry(productId);
		return country;
	}
	

	/**
	 * 블랙리스트 회원인 경우 로그인 후 화면
	 * @return
	 */
	@GetMapping("/blacklist")
	public String blacklist() {
		return "blacklist";
	}

	/**
	 * 탈퇴한 회원인 경우 로그인 후 화면
	 * @return
	 */
	@GetMapping("/withdrawalAccount")
	public String withdrawalAccount() {
		return "withdrawalAccount";
	}
	
	/**
	 * 마지막 화면
	 * @return
	 */
	@GetMapping("/endpage")
	public String endpage() {
		return "endpage";
	}

}


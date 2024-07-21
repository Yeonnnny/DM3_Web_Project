package net.kdigital.ec21.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.CustomerDTO;
import net.kdigital.ec21.dto.CustomerListModalDTO;
import net.kdigital.ec21.dto.ModelPredictDTO;
import net.kdigital.ec21.dto.ModelPredictModalDTO;
import net.kdigital.ec21.dto.ProductDTO;
import net.kdigital.ec21.dto.ReportedCustomerWithInfoDTO;
import net.kdigital.ec21.service.ManagerService;
import net.kdigital.ec21.service.ProductService;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ManagerController {
	private final ManagerService managerService;
	private final ProductService productService;

	// ============================= 메인보드 =============================
	/**
	 * 관리자 메인보드 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("manager/manager_index")
	public String manager_index() {

		return "manager/manager_index";
	}

	/**
	 * ajax - 당일 등록 상품 수, 당일 이상상품 등록 수, 당일 회원가입 수, 미처리된 신고 개수 반환
	 * 
	 * @return
	 */
	@GetMapping("manager/count")
	@ResponseBody
	public Map<String, Long> count() {
		Map<String, Long> result = managerService.getCount();
		return result;
	}

	// ============================= 상품 관리 =============================

	/**
	 * 전체 상품 리스트 화면 요청
	 * 
	 * @return
	 */

	@GetMapping("manager/productList")
	public String productList() {
		return "manager/productList";
	}

	/**
	 * ajax - 전체 상품 화면에서 상품 리스트 반환
	 * (카테고리, 검색어 조건에 따라 다르게 반환, 기본적으로 최신 등록일자 순으로 반환)
	 * (아직 버튼 처리 안함)
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/manager/productList/getList", method = RequestMethod.GET)
	public String getProductList(@RequestParam(name = "category", defaultValue = "total") String category,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, 
			@RequestParam(name = "filter", defaultValue = "") String filter, Model model) {
		List<ProductDTO> dtoList = new ArrayList<>();

		// 전체 상품 대상
		if (filter.equals("") || filter.equals("total")) {
			dtoList = managerService.selectProductBySearch(category, searchWord);
		} // 미처리/정상/이상 상품 대상
		else{
			dtoList = managerService.selectProductBySearchAndFilter(filter, category, searchWord);
		}
		model.addAttribute("list", dtoList);

		return "/manager/productList::#result";
	}

	/**
	 * 모델로 이상상품 판별된 상품 리스트 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("manager/modelPredict")
	public String modelPredict() {
		return "manager/modelPredict";
	}

	/**
	 * ajax - 모델이 이상으로 판별하고 관리자가 아직 처리하지 않은 상품 (lstm_predict==0 && judge==null) 중
	 * 전달 받은 카테고리와 검색어에 해당하는 상품을 최신 등록일 순으로 반환
	 * (아직 버튼 처리 안함)
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/manager/modelPredict/getList", method = RequestMethod.GET)
	public String getList(@RequestParam(name = "category", defaultValue = "total") String category,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {

		List<ModelPredictDTO> dtoList = managerService.selectModelPredictWeirdBySearch(category, searchWord);
		model.addAttribute("list", dtoList);

		return "/manager/modelPredict::#result";
	}

	/**
	 * ajax - 전달받은 상품ID에 해당하는 (금지어 유사단어, 금지어 단어, 금지어 카테고리) 정보 리스트로 반환
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/manager/modelPredict/getProhibitSimilarWordDTOs", method = RequestMethod.GET)
	public String getProhibitSimilarWordDTOs(@RequestParam(name = "productId") String productId, Model model) {
		List<ModelPredictModalDTO> result = managerService.getProhibitSimilarWordDTOs(productId);
		model.addAttribute("list", result);
		return "/manager/modelPredict::#modalTable";
	}

	/**
	 * 금지어 목록에 새로운 단어 추가 요청
	 * 
	 * @param similarWord
	 * @param prohibitReason
	 * @return
	 */
	@ResponseBody
	@GetMapping("modelPredict/wordPlus")
	public Boolean getMethodName(@RequestParam(name = "similarWord") String similarWord,
			@RequestParam(name = "prohibitReason") String prohibitReason) {
		return managerService.insertProhibitWord(similarWord, prohibitReason);
	}

	/**
	 * 정상상품 요청 시 상품의 judge를 Y 즉, 정상으로 변경
	 * 
	 * @param productId
	 * @return
	 */
	@ResponseBody
	@GetMapping("modelPredict/productNormal")
	public Boolean productNormal(@RequestParam(name = "productId") String productId) {
		log.info("지금 정상 버튼 눌러서 컨트롤러왔어");
		return managerService.updateProductJudgeNormal(productId);
	}

	/**
	 * 이상상품 요청 시 상품의 judge를 N 즉, 이상으로 변경
	 * 
	 * @param productId
	 * @return
	 */
	@ResponseBody
	@GetMapping("modelPredict/productWeird")
	public Boolean productWeird(@RequestParam(name = "productId") String productId) {
		log.info("지금 블랙 버튼 눌러서 컨트롤러왔어");
		return managerService.updateProductJudgeWeird(productId);
	}

	// ============================= 회원 관리 =============================

	/**
	 *  정상회원 화면 요청
	 * @param searchCategory
	 * @param searchWord
	 * @param model
	 * @return
	 */
	@GetMapping("manager/customerList")
	public String customerListView(@RequestParam(name = "searchCategory", defaultValue = "total") String searchCategory,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {
		List<CustomerDTO> dtoList = managerService.selectNotBlacklistByCategoryAndSearch(searchCategory, searchWord);
		// List<CustomerDTO> dtoList = managerService.selectNotBlacklist();
		model.addAttribute("list", dtoList);
		model.addAttribute("searchCategory", searchCategory);
		model.addAttribute("searchWord", searchWord);
		return "manager/customerList";
	}


	/**
	 * 전달받은 상품 ID에 해당하는 금지어유사도 결과 리스트를 JSON 데이터로 반환
	 * 
	 * @param productId
	 * @return
	 * @throws JsonProcessingException
	 */
	@ResponseBody
	@GetMapping("/manager/customerList/getCustomerProductDTOs")
	public String getCustomerProductDTOs(@RequestParam(name = "customerId") String customerId)
			throws JsonProcessingException {
		log.info(customerId);
		List<CustomerListModalDTO> result = managerService.getCustomerProductDTOs(customerId);
		if (result == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(result);
	}

	@ResponseBody
	@GetMapping("/manager/customerList/forSecondModal")
	public ProductDTO forSecondModal(@RequestParam(name = "productId")String productId){
		return productService.getProduct(productId);
	}

	
	/**
	 * 전달받은 customerId에 해당하는 회원 블랙리스트 처리 요청
	 * 
	 * @param customerId
	 * @param blackReason
	 * @param etcReason
	 * @return
	 */
	@ResponseBody
	@GetMapping("/manager/customerList/toBlack")
	public Boolean getMethodName(@RequestParam(name = "customerId") String customerId,
			@RequestParam(name = "blackReason") String blackReason,
			@RequestParam(name = "etcReason") String etcReason) {
		log.info("======= 블랙으로 변경");
		log.info("=======  {}", customerId);
		log.info("=======  {}", blackReason);
		log.info("=======  {}", etcReason);
		return managerService.insertToBlacklist(customerId, blackReason, etcReason);
	}

	/**
	 * 신고당한 회원 리스트 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("manager/reportedCustomerList")
	public String reportedCustomerList(Model model) {
		return "manager/reportedCustomerList";
	}

	/**
	 * ajax - 신고당한 회원 리스트 반환 (미처리/처리(블랙버튼/정상버튼)에 따라 다르게 처리)
	 * 
	 * @param reportCustomerId 
	 * @param reportedId
	 * @param category
	 * @param searchWord
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/manager/reportedCustomerList/getList", method = RequestMethod.GET)
	public String getList(@RequestParam(name = "reportCustomerId", defaultValue = "-100") Long reportCustomerId,
			@RequestParam(name = "reportedId", defaultValue = "") String reportedId,
			@RequestParam(name = "category", defaultValue = "total") String category,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord,
			Model model) {
		if (reportCustomerId != -100) {
			// 블랙버튼 : 신고당한 회원 블랙리스트에 추가
			if (!reportedId.isEmpty()) {
				managerService.reportedIdToBlackList(reportCustomerId, reportedId);
			}
			// 정상버튼 : 관리자 처리 완료 상태로 변경
			managerService.reportCustomerUpdateManagerCheck(reportCustomerId);
		}

		List<ReportedCustomerWithInfoDTO> dtoList = managerService.selectReportedCustomerBySearch(category, searchWord);
		model.addAttribute("list", dtoList);

		return "/manager/reportedCustomerList::#result";
	}

	/**
	 * 전달받은 회원ID가 블랙리스트 DB에 존재하는지 확인 (존재하면 false, 존재하지 않으면 true)
	 * 
	 * @param reportedId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/reportedCustomerList/checkBlack")
	public Boolean checkBlack(@RequestParam(name = "reportedId", defaultValue = "") String reportedId) {
		return managerService.checkBlack(reportedId);
	}

	/**
	 * 블랙리스트 회원 리스트 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("manager/blackList")
	public String blackList() {
		return "manager/blackList";
	}

	/**
	 * ajax - 블랙리스트 반환 (정상버튼 클릭시는 다르게 처리)
	 * 
	 * @param blacklistId
	 * @param category
	 * @param searchWord
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/manager/blackList/getList", method = RequestMethod.GET)
	public String getList(@RequestParam(name = "blacklistId", defaultValue = "-100") Long blacklistId,
			@RequestParam(name = "category", defaultValue = "total") String category,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {

		if (blacklistId != -100) {
			// 정상버튼 : 블랙리스트 테이블에서 해당 블랙리스트ID(일련번호) 삭제 및 해당 customerId blacklistCheck값 변경
			managerService.deleteFromBalcklist(blacklistId);
		}
		List<ReportedCustomerWithInfoDTO> dtoList = managerService.selectblackListBySearch(category, searchWord);
		model.addAttribute("list", dtoList);

		return "/manager/blackList::#result";
	}

}

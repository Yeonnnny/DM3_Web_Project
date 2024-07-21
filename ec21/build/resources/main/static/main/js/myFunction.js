/********************
 * 모달과 관련된 함수
 *************************/

// modalControl.js
// $(document).ready(function () {
//     $('#inquiry').click(function () {
//         $('#myModal').modal('show');  // Bootstrap 모달을 수동으로 열기
//     });
// });





$(document).ready(function () {
    $("#submitBtn").click(function (e) {
        e.preventDefault(); // 기본 폼 제출 방지

        // 필수 입력 필드 검사
        var firstEmptyInput = findFirstEmptyInput();
        if (firstEmptyInput) {
            alert("Please fill in the " + firstEmptyInput.label + " field."); // 경고 메시지
            $('#' + firstEmptyInput.id).focus(); // 해당 필드에 포커스
            return; // 폼 제출 중단
        }

        // 체크박스 검사
        if (!$("#termsAgreement").is(":checked")) { // 체크박스가 체크되었는지 확인
            $("#termsAgreement").focus();
            return; // 폼 제출 중단
        }

        // 모든 검사를 통과하면 폼 제출
        $("#myForm").submit(); // 폼 제출
    });
});

// 빈 필드 검사 함수
function findFirstEmptyInput() {
    var requiredFields = $("#myForm").find("input[required], textarea[required]");
    var firstEmpty = null;

    requiredFields.each(function () {
        var value = $(this).val().trim(); // 공백 제거
        if (!value) {
            firstEmpty = {
                id: $(this).attr("id"),
                label: $(this).closest("tr").find("th").text() // 필드의 라벨
            };
            return false; // 반복 중단
        }
    });

    return firstEmpty; // 첫 번째 빈 필드 반환
}



// inquiry modalControl.js
$(document).ready(function () {
    $('#inquiryTitle').click(function () {
        $('#inquiryDetail').modal('show');  // Bootstrap 모달을 수동으로 열기
    });
});

/*********************************************** 
 * saved-icon 을 누르면 색이 있는 별로 되게 하는 스크립트
/************************************************ */
// $(document).ready(function() {

//     // 이벤트 위임하는 것
//     $(document).on('click', '.saved-icon', function(event) {
//         event.preventDefault();
//         var icon = $(this).find('i');

//         if (icon.hasClass('ti-star')) {
//             icon.removeClass('ti-star').addClass('fa fa-star');
//         } else {    
//             icon.removeClass('fa fa-star').addClass('ti-star');
//         }
//     });
// });

/**********************************
 * IP 주소 마지막 주소 * 처리
 ***********************************/

// $(document).ready(function () {
//     $('.ipElement').each(function () { // 'ipElement' 클래스를 가진 모든 요소에 대해 반복
//         var ipElement = $(this); // 현재 반복 중인 요소
//         console.log(ipElement);
//         var remoteIp = ipElement.text().trim(); // 텍스트 가져오기 및 공백 제거
//         console.log(ipElement.text().trim()); // 각 요소의 텍스트 확인

//         if (!remoteIp) { // IP 주소가 없을 경우 예외 처리
//             console.log("IP 주소가 없습니다.");
//             return;
//         }

//         if (remoteIp.length <= 3) { // 3자리 이하일 경우 마스킹
//             ipElement.text('***');
//         } else { // 4자리 이상일 경우 마지막 세 자리를 마스킹
//             ipElement.text(remoteIp.slice(0, -3) + '***');
//         }
//     });
// });

document.addEventListener("scroll", function () {
    const header = document.querySelector(".header-inner");
    const scrollPosition = window.scrollY;

    if (scrollPosition > 150) { // 스크롤 위치가 100px 이상이면
        header.classList.add("fixed-header");
        document.body.classList.add("header-padding");
    } else { // 스크롤 위치가 100px 미만이면
        header.classList.remove("fixed-header");
        document.body.classList.remove("header-padding");
    }
});

// $(document).ready(function () {
//     // 각 제품 콘텐츠 요소 내에서 작동합니다.
//     $('.product-content').each(function () {
//         let $this = $(this);
//         let countryName = originContainer.find('.product-seller').attr('data-country'); // data-country 속성에서 국가 이름을 가져옵니다.
//         let $flagContainer = $this.find('.countryFlag');  // 국기 이미지를 업데이트할 컨테이너를 찾습니다.

//         console.log($this.find('.product-seller').attr('data-country'));
//         console.log($flagContainer);

//         // 국가 이름을 기반으로 AJAX 요청을 통해 국가 정보를 가져옵니다.
//         if (countryName) {
//             $.ajax({
//                 url: `https://restcountries.com/v3.1/name/${countryName}`,
//                 method: 'GET',
//                 success: function (data) {
//                     if (data && data.length > 0) {
//                         let countryCode = data[0].cca2.toLowerCase();
//                         let flagImageUrl = `https://flagcdn.com/${countryCode}.svg`;  // 변경된 API를 사용하여 국기 URL을 생성합니다.
//                         $flagContainer.attr('src', flagImageUrl);  // 이미지 태그의 src 속성을 업데이트
//                         $flagContainer.attr('alt', `${countryName} Flag`);  // alt 속성을 업데이트
//                     } else {
//                         clearFlag($flagContainer);  // 국가 데이터가 없거나 오류 시 플래그 컨테이너를 비웁니다.
//                     }
//                 },
//                 error: function () {
//                     clearFlag($flagContainer);  // AJAX 요청 오류 시 플래그 컨테이너를 비웁니다.
//                 }
//             });
//         }
//     });
// });

// function clearFlag($flagContainer) {
//     $flagContainer.attr({
//         'src': '',
//         'alt': ''
//     });
// }

/**********************************
 * 메인 화면에 국기 이미지 넣기 -----  완성
 ***********************************/
// create by 주영
// $(document).ready(function () {
//     $('.product-seller').each(function () {
//         var countryName = $(this).data("country");
//         var $this = $(this); // 현재 .product-seller 요소를 $this 변수에 저장

//         if (countryName) {
//             // AJAX 요청을 통해 국가 정보를 가져옵니다.
//             $.ajax({
//                 url: `https://restcountries.com/v3.1/name/${countryName}`,
//                 method: 'GET',
//                 success: function (data) {
//                     if (data && data.length > 0) {
//                         var countryCode = data[0].cca2.toLowerCase();
//                         var flagImageUrl = `https://flagcdn.com/${countryCode}.svg`;
//                         // $this 범위 내의 .countryFlag 이미지 src를 업데이트합니다.
//                         $this.find(".countryFlag").attr('src', flagImageUrl).attr('alt', `${countryName} Flag`);
//                     } else {
//                         console.warn('No data for country: ' + countryName);
//                         $this.find(".countryFlag").attr('src', '').attr('alt', 'Flag not available');
//                     }
//                 },
//                 error: function () {
//                     console.error('Error retrieving country data.');
//                     $this.find(".countryFlag").attr('src', '').attr('alt', 'Error loading flag');
//                 }
//             });
//         }
//     });
// });

// edit by 도연 (240502)
$(document).ready(function () {
    $('.product-seller').each(function () {
        var productId = $(this).data("productid");
        var $this = $(this); // 현재 .product-seller 요소를 $this 변수에 저장

        $.ajax({
            url: "/main/index/getCountry",
            data:{"productId":productId},
            method:"GET",
            success:function (country) {
                if (country) {
                    // AJAX 요청을 통해 국가 정보를 가져옵니다.
                    $.ajax({
                        url: `https://restcountries.com/v3.1/name/${country}`,
                        method: 'GET',
                        success: function (data) {
                            if (data && data.length > 0) {
                                var countryCode = data[0].cca2.toLowerCase();
                                var flagImageUrl = `https://flagcdn.com/${countryCode}.svg`;

                                // $this 범위 내의 .countryFlag 이미지 src를 업데이트합니다.
                                $this.find(".countryFlag").attr('src', flagImageUrl).attr('alt', `${country} Flag`);
                            } else {
                                console.warn('No data for country: ' + country);
                                $this.find(".countryFlag").attr('src', '').attr('alt', 'Flag not available');
                            }
                        },
                        error: function () {
                            console.error('Error retrieving country data.');
                            $this.find(".countryFlag").attr('src', '').attr('alt', 'Error loading flag');
                        }
                    });
                }
                
            }
        });

    });
});

/**********************************
 * productsDetail -----  완성
 ***********************************/

$(document).ready(function () {
    // 각 제품의 원산지를 기반으로 국기 이미지를 가져옵니다.
    $('.product-desc').each(function () {
        let originContainer = $(this).find('.origin');
        let countryName = originContainer.find('.country-name').text();
        let flagContainer = originContainer.find('.flag-container');
        
        // 국가 이름을 기반으로 AJAX 요청을 통해 국가 정보를 가져옵니다.
        if (countryName) {
            $.ajax({
                url: `https://restcountries.com/v3.1/name/${countryName}`,
                method: 'GET',
                success: function (data) {
                    if (data && data.length > 0) {
                        let countryCode = data[0].cca2.toLowerCase();
                        let flagImageUrl = `https://flagcdn.com/${countryCode}.svg`; // 변경된 API 사용
                        flagContainer.html(`<img src="${flagImageUrl}" alt="${countryName} Flag" style="width:20px; height:20px;">`);
                    } else {
                        console.warn("Country data not found.");
                        flagContainer.html(""); // 국가 데이터가 없을 경우 플래그 컨테이너를 비웁니다.
                    }
                },
                error: function () {
                    console.error("Error retrieving country data.");
                    flagContainer.html(""); // AJAX 요청 오류 시 플래그 컨테이너를 비웁니다.
                }
            });
        }
    });
});


/**********************************
 * productsDetail : seller 국가 -----  완성
 ***********************************/
$(document).ready(function () {
    // 각 제품의 원산지를 기반으로 국기 이미지를 가져옵니다.
    $('.comp-info').each(function () {
        let originContainer = $(this).find('.country');
        let countryName = originContainer.siblings('.seller-country').text();
        let flagContainer = originContainer.find('#seller-flag');

        if (flagContainer.length > 0) { // 'flagContainer'가 존재하는지 검사
            console.log("Flag container found.")
        } else{
            console.log("Flag container not found.")
            
        }

        // console.log('나라는?' + originContainer.find('.seller-country').text());
        // console.log(originContainer.find('.seller-country'));

        // 국가 이름을 기반으로 AJAX 요청을 통해 국가 정보를 가져옵니다.
        if (countryName) {
            console.log('아냥ㅇ');
            $.ajax({
                url: `https://restcountries.com/v3.1/name/${countryName}`,
                method: 'GET',
                success: function (data) {
                    if (data && data.length > 0) {
                        console.log(data);
                        let countryCode = data[0].cca2.toLowerCase();
                        console.log("===================="+countryCode);
                        let flagImageUrl = `https://flagcdn.com/${countryCode}.svg`; // 변경된 API 사용
                        console.log("===================="+flagImageUrl);
                        flagContainer.html(`<img src="${flagImageUrl}" alt="${countryName} Flag" style="width:20px; height:20px;">`);
                    } else {
                        console.warn("Country data not found.");
                        flagContainer.html(""); // 국가 데이터가 없을 경우 플래그 컨테이너를 비웁니다.
                    }
                },
                error: function () {
                    console.error("Error retrieving country data.");
                    flagContainer.html(""); // AJAX 요청 오류 시 플래그 컨테이너를 비웁니다.
                }
            });
        }
    });
});


/*********************
 * 이동 시켜 놓음
 * 신고 내용 GET 방식으로 보내기
 * 	
		// function submitInquiry() {
		// 	let receiverId = $("#modalReceiverId").text();
		// 	let senderId = $("#modalSenderId").val();
		// 	let productId = $("#modalProductId").val();
		// 	let quantity = $("#modalQuantity").val();
		// 	let inquiryTitle = $("#modalTitle").val();
		// 	let inquiryContent = $("#modalContent").text();
		// 	let uploadFile = $("#modalFile").val();
			
		// 	if (confirm("Successfully sending inquiry!")) {
		// 		$.ajax({
		// 			url: "/productsDetail/sendInquiry",
		// 			data: { "senderId": senderId, "receiverId": receiverId, "productId": productId, "quantity": quantity, 
		// 					"inquiryTitle": inquiryTitle, "inquiryContent": inquiryContent, "uploadFile": uploadFile},
		// 			method: "get",
		// 			success: function (resp) {
		// 				if (resp) {
		// 					$("#closeBtn").click();
		// 				}
		// 			}
		// 		});
		// 	}
		// }
 * 
 ***********************/
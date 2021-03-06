### 타임세일

상품 할인과 관련된 기능들을 구현했습니다.

할인 정보 테이블은 아래와 같습니다.

| 컬럼명             | 데이터 타입 | 내용           |
| :----------------- | :---------- | -------------- |
| PRODUCT_NO (FK)    | NUMBER      | 상품 고유 번호 |
| TIMESALE_START     | DATE        | 세일 시작 시간 |
| TIMESALE_END       | DATE        | 세일 종료 시간 |
| TIMESALE_SALEPRICE | NUMBER      | 세일가격       |

![sale01](https://user-images.githubusercontent.com/80666066/114559258-808a8d00-9ca6-11eb-8ba4-9e2c701806f4.png)
타임세일 페이지에 들어갔을 때 보이는 화면  

![sale02](https://user-images.githubusercontent.com/80666066/114559324-913b0300-9ca6-11eb-95d4-22dc080b3de8.png)
상품 목록 전체보기에서 보이는 화면  
<br/>

이용자가 페이지를 로드하는 시간 기준으로 현재 세일 진행중인 상품의 세일가와 할인율, 타임세일 페이지에서는 추가로 할인 종료까지 남은 시간을 보여줍니다.  
할인률과 남은 시간의 경우 JavaScript로 페이지 로드 시 마다 값을 계산해서 보여줍니다.  

```java
//timesale 테이블에서 현재 시간상 유효기간에 속하는 상품의 데이터를 조회하고, List로 반환합니다 
	public List<SaleProductVo> selectValid(){
		SaleProductDao dao = sqlSession.getMapper(SaleProductDao.class);
		List<SaleProductVo> list = dao.selectValid(getNow());
		return list;
	}
	

//timesale 테이블에서 현재 시간상 유효기간에 속하는 상품의 데이터를 조회하고, Map으로 반환합니다
	public HashMap<Integer, SaleVo> selectValidMap(){
		List<SaleVo> list = sqlSession.selectList("sale.selectValid", getNow());
		HashMap<Integer, SaleVo> saleMap = new HashMap<>();
		for(SaleVo sv : list) {
			saleMap.put(sv.getProduct_no(), sv);
		}
		return saleMap;
	}
```
  
같은 테이블에서 필요에 따라 list로 반환받는 경우, 상품 번호를 key로 갖는 map으로 반환받는 경우 두 가지 방식으로 조회했습니다.  
세일 상품만을 보여줄 시에는 list로 데이터를 받아오고, 전체 상품 보기에서는 상품 번호를 map에서 조회하여 있는 경우 세일정보를 표기했습니다. 
<br/>

![sale_detail](https://user-images.githubusercontent.com/80666066/117337379-4349a180-aed8-11eb-9f40-d2df25c49748.PNG)
```java
// 상세 상품 보기
@RequestMapping("/detailProducts.do")
	public ModelAndView detail(int no,HttpSession session) {
		String mem_id = (String)session.getAttribute("login");
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("reviews", reviewDao.selectProductReview(no));
		mav.addObject("p", dao.selectOne(no));
		mav.addObject("mem_id",mem_id);
		
		// 해당 상품이 세일중이라면 세일 정보를 같이 넘긴다
		SaleVo sv = saleDao.selectOne(no);
		if (sv != null && saleDao.checkValid(sv)) {
			mav.addObject("sale", saleDao.selectOne(no));	
		}
		return mav;
	}
```
상품 상세보기 시 상품의 PK로 세일 테이블에서 검색해 정보를 전달합니다.  
할인율 계산과 표기는 화면단에서 자바스크립트로 처리하였습니다.  
```javascript
//가격을 천단위로 문자열 포매팅
function priceFormat(price){
	let price_string = String(price);
	return price_string.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}


//원가와 세일가격을 받아 할인율을 계산해주는 함수
function setSalerate(origin, sale, saletag){
	let gap = origin - sale;
	let salerate = gap / origin * 100;
	salerate = Math.floor(salerate);
	
	saletag.innerHTML = '-' + salerate + '%할인';
}

if(document.getElementById('origin_price')){
	let origin = document.getElementById('product_price').getAttribute('value');
	let sale = document.getElementById('sale_price').getAttribute('value');
	setSalerate(origin, sale, document.getElementById('sale_tag'));
}
```

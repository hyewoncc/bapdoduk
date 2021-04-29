### 검색 기능

상품명을 검색하는 기능을 만들었습니다

![search00](https://user-images.githubusercontent.com/80666066/116546768-f93b4b80-a92c-11eb-839b-01b1e7ac991f.PNG)  
검색어를 입력할 시 검색어가 상품명에 포함 된 제품들과 결과 건수를 보여줍니다  
컨트롤러에서 검색어를 파라미터로 받아 해당 코드를 거쳐 페이지로 반환하게 만들었습니다  

```java
//상품명 검색 결과를 반환하고 검색 결과 페이지로 넘김
@RequestMapping("/search")
public ModelAndView searchPage(HttpServletRequest request) {
  String keyword = request.getParameter("keyword");
  
  //검색어 앞뒤의 공백 제거
  keyword = mainUtil.removeBlank(keyword);
		
	ModelAndView mav = new ModelAndView();
	mav.addObject("keyword", keyword);
		
	//검색 결과가 비어있을 시 안내 문구를 보이기 위해 결과값 추가
	List<ProductVo> resultList = productDao.searchByName(keyword);
	mav.addObject("resultCount", resultList.size());
	if(resultList.isEmpty()) {
		mav.addObject("notFound", "true");
	}else {
		mav.addObject("resultList", resultList);
	}
		
	mav.setViewName("searchResult");
	return mav;
 }
```  

아래는 DAO 객체에서 작성한 검색 결과 리스트를 반환받는 함수와 검색용 SQL문입니다
```java
//특정 문구가 이름에 포함된 상품 목록을 반환합니다
public List<ProductVo> searchByName(String keyword){
  keyword = ("%" + keyword + "%");
	List<ProductVo> list = new ArrayList<>();	
	list = sqlSession.selectList("products.searchByName", keyword);				
	
  return list;
}
```  

```xml
<select id="searchByName" resultType="productVo">
	select * from TB_PRODUCT
	where product_title like #{keyword}
</select>
```  

검색 결과가 없을 경우 JSP C 태그를 통해 별도의 안내메세지도 보여줍니다
![search02](https://user-images.githubusercontent.com/80666066/116563364-edf11b80-a93e-11eb-9f07-783c42c001c6.PNG)  
```jsp
<c:if test="${resultCount == 0}">
  <div class="not-found">
    <i class="fas fa-exclamation-circle"></i><br>
    <span>검색 결과가 없습니다. 다른 키워드로 찾아보시겠어요?</span>
   </div>
</c:if>
```  


검색란이 있는 메뉴바의 경우, 사용자 편의를 생각해 자바스크립트와 CSS를 작성하여 하단으로 스크롤 시 상단에 고정되게 했습니다  
페이지 어느 위치에서든 쉽게 검색과 게시판 이동이 가능합니다  



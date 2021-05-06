### 카테고리 보기  

![category](https://user-images.githubusercontent.com/80666066/117342341-17c9b580-aede-11eb-8d6a-e2bef72c9614.gif)

상품 카테고리 정보에 따라 분류해서 보는 기능을 구현했습니다.  

```html
<div class="dropdown-content">		 
    <a href="/category?key=국&key=탕">국/탕</a>
    <a href="/category?key=찜&key=조림">찜/조림</a>
    <a href="/category?key=육류">육류</a>
    <a href="/category?key=생선&key=해산물">생선/해산물</a>
    				...
```

메뉴바의 카테고리 드랍다운 항목을 클릭하면 키워드를 파라미터로 넘깁니다.  

상품이 여러 카테고리에 속할 수 있고, 분류상 묶을 수 있기에 여러 키워드를 받을 수 있도록 만들었습니다.  



```java
//카테고리 검색 결과를 반환하고 목록 페이지로 넘김
@RequestMapping("/category")
public ModelAndView searchCategory(HttpServletRequest request) {
    String[] keywords = request.getParameterValues("key");
    ModelAndView mav = new ModelAndView();
    String title = "";
		
    //각 키워드에 해당하는 상품 목록을 받아서 추가
    List<ProductVo> categoryList = new ArrayList<ProductVo>();
    for(String key : keywords) {
        title += ("/" + key);
		categoryList.addAll(productDao.selectCategory(key));
    }
    
    //상품 목록에서 중복 제거
    List<ProductVo> list = new ArrayList<ProductVo>();
    for(ProductVo pv : categoryList) {
        if(!list.contains(pv)) {
            list.add(pv);
		}
	}
    
    mav.addObject("list", list);
    mav.addObject("title", title.substring(1));
    mav.addObject("saleMap", saleDao.selectValidMap());
    mav.setViewName("listProducts");
    return mav;
}
```

컨트롤러에서 해당 키워드를 받아 DAO 클래스를 통해 해당하는 상품을 가져옵니다.  

카테고리 분류 상 중복이 존재할 수 있기에 내역을 돌면서 중복을 제거합니다.  

아래는 DAO 클래스와 sql문 코드입니다.  

```java
//카테고리에 해당하는 상품 목록 반환
public List<ProductVo> selectCategory(String keyword){
    keyword = ("%" + keyword + "%");
    List<ProductVo> list = new ArrayList<ProductVo>();
    list = sqlSession.selectList("products.selectCategory", keyword);
    return list;
}
```

```sql
<select id="selectCategory" resultType="productVo">
	select * from TB_PRODUCT
	where product_category like #{keyword}
</select>
```


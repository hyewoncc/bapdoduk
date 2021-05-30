### 세일 정보 관리 (등록/수정/삭제)

세일 정보 관리와 관련된 기능을 구현했습니다  
페이지가 길어져 사용한 기술을 먼저 요약하겠습니다  
- 기본적인 DB CRUD 쿼리문 작성과 이용  
- 두 테이블의 정보를 합쳐서 조회  
- 자바스크립트로 화면단에서 유효성 검증  
- ajax 통신과 이를 통한 html 동적 생성 
- 쿼리스트링을 통한 값 전달  
- jsp의 c태그를 이용한 html 동적 생성  

이하 구현한 내용에 대한 상세한 설명입니다  
</br>
</br>
세일 정보를 담고 있는 테이블은 다음과 같습니다  

| 컬럼명              | 데이터 타입    | 내용             |
| ------------------- | -------------- | ----------------|
| PRODUCT_NO          | NUMBER         | 상품 고유번호    |
| TIMESALE_START      | DATE           | 세일 시작시간    |
| TIMESALE_END        | DATE           | 세일 마감시간    |
| TIMESALE_SALEPRICE  | NUMBER         | 할인가격         |  

</br>

### 세일 등록
![sale-search](https://user-images.githubusercontent.com/80666066/120122003-2c4d5500-c1e1-11eb-92bd-379928af36e2.gif)
세일 등록은 사용자가 상품 이름을 검색해서 선택해 등록할 수 있도록 했습니다  
검색을 누르면 팝업창이 뜨고, 키워드를 입력하면 검색이 됩니다  
키보드 키를 누르고 뗐을 때 ajax 통신으로 검색한 결과를 가져오고 html을 동적 생성하도록 했습니다  
</br>
```javascript
//상품명 검색 시 검색 결과를 바로바로 반영하는 기능
//searchBoxId : 검색어가 입력되는 input 칸의 id
function search(searchBoxId){
  //검색 키워드를 입력창에서 키가 입력될 때 마다 받아옴
  let keyword = document.getElementById(searchBoxId).value;
  let data = {'keyword' : keyword};
  if(keyword != ''){
    $.ajax({
      url:'/admin/product-search',
      data: data,
      type:'post',
      success:function(data){
        $("#resultList").empty();
        $("#resultList").append('<th>상품번호</th><th>카테고리</th><th>상품명</th><th>재고</th><th>원가격</th>');
        
        $.each(data, function(index, item){
          let tr = $("<tr></tr>");
          let td1 = $("<td></td>").html(item.product_no);
          let td2 = $("<td></td>").html(item.product_category);
          let td3 = $("<td></td>").html(item.product_title);
          let td4 = $("<td></td>").html(item.product_stock);
          let td5 = $("<td></td>").html(item.product_price);
          
          $(tr).click(function(){
            sendItem(item);
          });
          $(tr).append(td1,td2,td3,td4,td5);
          $("#resultList").append(tr);
        })
      }
    })
  }
}
```
컨트롤러에서는 ajax 통신으로 받은 keyword(검색어)로 DB를 조회해서 결과를 리스트로 반환합니다  
```java
//POST 방식 접근, 검색 결과를 갱신
@RequestMapping(value = "/product-search", method = RequestMethod.POST)
@ResponseBody
public List<ProductVo> updateSearchList(HttpServletRequest request) {
  String keyword = request.getParameter("keyword");
  List<ProductVo> searchList = new ArrayList<>();
  searchList = dao.searchByName(keyword);			
  return searchList;}
```
조회를 위해 작성한 쿼리문입니다   
```xml
<select id="searchByName" resultType="productVo">
  select * from TB_PRODUCT
  where product_title like #{keyword}
</select>
```
상품 번호나 이름을 직접 타이핑 해넣을 시 생길 오류를 방지하기 위해 이렇게 만들었습니다  
</br>
이 후, 세일할 가격과 기간을 설정해서 등록하면 됩니다  
자바스크립트로 화면단에서 간단한 유효성 검사를 하도록 했습니다  
필수 정보에 공란이 있을 경우, 세일가에 숫자 아닌 문자가 있을 경우, 
세일가가 원가보다 비싸거나, 세일 기간에 논리적으로 오류가 있을 경우 등의 문제가 있을 시 등록되지 않고 알람을 줍니다  
![saleregis](https://user-images.githubusercontent.com/80666066/120122279-c530a000-c1e2-11eb-8744-108500de40d3.gif)  
유효성 검증을 위해 아래와 같은 자바스크립트 코드를 작성했습니다  
```javascript
//세일 등록 시 유효성을 검사하는 함수
function checkSaleForm(){
  let reg = /^[0-9]+$/;
  let salePrice = document.getElementById('salePrice');
  let originPrice = document.getElementById('productPrice').value;
  let productNo = document.getElementById('productNo').value;
  let start = document.getElementById('startDate').value + document.getElementById('startTime').value;
  let end = document.getElementById('endDate').value + document.getElementById('endTime').value;
  
  //상품을 선택했는지 검증
  if(productNo == ''){
    alert('할인 할 상품을 선택하세요');
    return false;
  }
  
  //세일가를 기입했는지 확인
  if(salePrice.value == ''){
    textClean(salePrice, '할인가');
    return false;
  }
  
  //세일가에 숫자만 들어있는지 확인
  if(!reg.test(salePrice.value)){
    numClean(salePrice, '할인가');
    return false;
  }
  
  //세일가가 정상가보다 낮은지 확인
  if(salePrice.value >= originPrice){
    alert('할인가는 원가격보다 낮아야합니다');
    salePrice.value = '';
    salePrice.focus();
    return false;
  }
  
  //세일 기간이 정상적으로 설정되었는지 확인
  if(start >= end){
    alert('할인 종료일이 시작일보다 이르거나 같습니다\n기간을 확인하고 다시 설정하세요');
    return false;
  }
	
	return true;	
}
```
</br>
컨트롤러에서는 받은 값 중 시간을 DB에 맞는 문자열 형식으로 편집한 후 등록합니다  

```java
//POST 방식 접근, 받은 정보를 세일정보 테이블에 입력
@RequestMapping(value = "/sale-register", method = RequestMethod.POST)
public String registerSaleDetail(HttpServletRequest request) {
  int productNo = Integer.parseInt(request.getParameter("productNo"));
  String startDate = request.getParameter("startDate") + " " + request.getParameter("startTime") + ":00";
  String endDate = request.getParameter("endDate") + " " + request.getParameter("endTime") + ":00";
  int salePrice = Integer.parseInt(request.getParameter("salePrice"));
  
  SaleVo sv = new SaleVo(productNo, startDate, endDate, salePrice);
  sdao.registerSale(sv);
  
  return "/admin/saleRegister";
}
```  
</br>  

세일 테이블에 삽입을 위해 작성한 쿼리문입니다  

```xml
<select id="register">
  insert into TB_TIMESALE values(
    #{product_no},
    to_date(#{timesale_start}, 'yyyy-mm-dd hh24:mi:ss'),
    to_date(#{timesale_end}, 'yyyy-mm-dd hh24:mi:ss'),
    #{timesale_saleprice})
</select>
```
</br>
</br>

### 세일 수정  

![sale-edit](https://user-images.githubusercontent.com/80666066/120122479-fc538100-c1e3-11eb-829f-8f9afa913429.gif)  
할일 수정 및 삭제에서는 등록된 상품 할인 정보를 수정하거나 삭제할 수 있습니다  
모든 할인 정보를 할인 정보 테이블에서 조회하고, 해당 상품의 이름과 원가격 정보를 상품 정보 테이블에서 조회해 목록으로 보여줍니다  
</br>  

```java
//GET 방식 접근, 세일 상품의 목록을 보여줌
@RequestMapping(value = "/sale-edit", method = RequestMethod.GET)
public String editSale(Model model, HttpServletRequest request) {
  request.getSession().setAttribute("category", "sale");
  request.getSession().setAttribute("function", "saleEdit");
  model.addAttribute("list", sdao.findAll());
  return "/admin/saleEdit";
}
```  

</br>

컨트롤러에서 saleDao 인스턴스를 통해 목록을 조회하면 아래 쿼리문이 실행됩니다  
```xml
<resultMap type="com.mall.vo.sale.SaleVo" id="saleMap">
  <id property="product_no" column="product_no"/>
  //...일부 생략
  <id property="timesale_saleprice" column="timesale_saleprice"/>
</resultMap>

<resultMap type="com.mall.vo.product.ProductVo" id="productMap">
  <id property="product_no" column="product_no" />
  <id property="product_category" column="product_category" />
  //...일부 생략
  <id property="product_regdate" column="product_regdate" />
</resultMap>

<resultMap type="com.mall.vo.sale.SaleProductVo" id="saleProductMap">
  <collection property="sale" resultMap="saleMap" />
  <collection property="product" resultMap="productMap" />
</resultMap>

<select id="findAll" resultMap="saleProductMap">
  select	* from TB_TIMESALE a, TB_PRODUCT b
  where	a.product_no = b.product_no
  order by a.timesale_start	
</select>
```
이렇게 두 테이블의 정보를 합쳐 map 형식으로 가져오도록 했습니다  
그 후, jsp의 c태그를 이용해 동적 html 생성으로 세일 목록을 표로 보여줍니다  
</br>  

```html
<div class="table-container">
  <div class="table-wrap">
    <table class="table table-hover">
      <thead>
        <tr>
          <td>상품번호</td>
          <td>상품명</td>
          <td>시작일</td>
          <td>종료일</td>
          <td>원가격</td>
          <td>할인가</td>
          <td>수정</td>
          <td>삭제</td>
        </tr>
      </thead>
      
      <c:forEach var="s" items="${list }">
        <tr>
          <td id="${s.sale.product_no }">${s.sale.product_no }</td>
          <td>${s.product.product_title }</td>
          <td>${s.sale.timesale_start }</td>
          <td>${s.sale.timesale_end }</td>
          <td>${s.product.product_price}</td>
          <td>${s.sale.timesale_saleprice }</td>
          <td><i class="fas fa-edit" id="edit-icon" onclick="location.href='/admin/sale-edit/${s.sale.product_no }'"></i></td>
          <td><i class="fas fa-trash-alt" id="delete-icon" onclick="deleteConfirmSale('${s.sale.product_no }')"></i></td>
        </tr>
      </c:forEach>
    </table>
  </div>
</div>
```  
</br>
수정 완료 시, 값을 ajax 통신으로 전달해 정보를 수정하고, 성공 시 알림을 줍니다  

```javascript
let productNo = document.getElementById('productNo').value;
let ajaxUrl = '/admin/sale-edit/' + productNo;

$('#sale-register-submit').click(function(){

  if(checkSaleForm()){
    let updateData = new FormData(document.getElementById('sale_editor'));	
    
    $.ajax({
      url:ajaxUrl,
      data:updateData,
      type:'post',
      processData:false,
      contentType:false,
      success:function(){
        alert("세일 정보 수정에 성공하였습니다.");
      }
    });
  }
});
```  
</br>
세일 정보 수정을 위해 작성한 쿼리문은 아래와 같습니다  

```xml
<select id="update">
  update TB_TIMESALE 
  set timesale_start =  to_date(#{timesale_start}, 'yyyy-mm-dd hh24:mi:ss'), 
    timesale_end = to_date(#{timesale_end}, 'yyyy-mm-dd hh24:mi:ss'), 
    timesale_saleprice = #{timesale_saleprice} 
  where product_no = #{product_no}
</select>
```  
</br>
</br>

### 세일 삭제  

![sale-delete](https://user-images.githubusercontent.com/80666066/120122881-9d433b80-c1e6-11eb-81a7-260809fc1e88.gif)  
목록에서 삭제 아이콘을 클릭하면 상품 번호를 통해 DB에서 해당 정보를 삭제합니다  
상품 번호는 url에 쿼리스트링 형식으로 컨트롤러에 전달됩니다  
</br>
```java
//특정 세일 상품의 DB 데이터를 삭제
@RequestMapping("/sale-edit/delete")
public String deleteSale(int no) {
  //DB에서 해당 세일의 데이터를 삭제
  sdao.deleteSale(no);
  return "redirect:/admin/sale-edit";
}
```  
</br>
삭제를 위해 실행되는 쿼리문은 아래와 같습니다  

```xml
<select id="delete">
  delete from TB_TIMESALE 
  where product_no = #{no}
</select>
```  



### 상품 정보 관리 (등록/수정/삭제)

상품 정보 관리와 관련된 기능을 구현했습니다  
페이지가 길어져 사용한 기술을 먼저 요약하겠습니다  
- 기본적인 DB CRUD 쿼리문 작성과 이용    
- 동일 url에서 GET/POST 접근을 컨트롤러에서 구분해 처리하기  
- 서버에 이미지 파일 업로드  
- 쿼리스트링을 통한 값 전달
- 자바스크립트로 서버단에서 유효성 검증하기 
- ajax 통신 
- jsp의 c태그를 이용한 html 동적 생성  

이하 구현한 내용에 대한 상세한 설명입니다  
</br>
</br>
상품 정보를 담고 있는 테이블은 아래와 같습니다  

| 컬럼명              | 데이터 타입    | 내용             |
| ------------------- | -------------- | ---------------- |
| PRODUCT_NO          | NUMBER         | 상품 고유번호    |
| PRODUCT_CATEGORY    | VARCHAR2(100)  | 카테고리         |
| PRODUCT_STOCK       | NUMBER         | 재고             |
| PRODUCT_TITLE       | VARCHAR2(200)  | 상품명           |
| PRODUCT_SUBTITLE    | VARCHAR2(200)  | 상품 서브 제목   |
| PRODUCT_MAIN_IMG    | VARCHAR2(100)  | 메인 이미지      |
| PRODUCT_CALOIES     | NUMBER         | 칼로리           |
| PRODUCT_ORIGIN      | VARCHAR2(100)  | 원산지           |
| PRODUCT_STORAGE     | VARCHAR2(100)  | 보관방법         |
| PRODUCT_DETAIL_IMG1 | VARCHAR2(200)  | 상품 세부 이미지 |
| PRODUCT_DETAIL_IMG2 | VARCHAR2(200)  | 상품 세부 이미지 |
| PRODUCT_DETAIL_TEXT | NVARCHAR2(200) | 상품 세부 글     |
| PRODUCT_INGREDIENT  | VARCHAR2(300)  | 재료             |
| PRODUCT_PRICE       | NUMBER         | 상품 정가        |
| PRODUCT_REGDATE     | DATE           | 글 작성일        |

</br>

### 상품 등록  

![registerproduct01](https://user-images.githubusercontent.com/80666066/120112822-8125a700-c1b2-11eb-8db2-7f03015da504.gif)
관리자의 상품 등록 페이지  

상품 정보를 작성해서 등록하면 편의를 위해 자바스크립트로 유효성 검사를 하도록 했습니다  
필수 정보에 공란이 있을 경우 공란 텍스트 박스를 포커싱하며 다시 작성하라는 알람이 뜹니다  
재고, 칼로리, 가격 등의 경우는 숫자가 아닌 문자가 있을 경우, 텍스트 박스를 포커싱하며 알람을 줍니다  

```javascript
//상품 등록 및 수정 시 정보 유효성을 검사하는 함수
function checkProductForm(formtype){
	
	let test = document.getElementsByClassName('product_main_img');
	
	// 숫자만 있어야 하는 항목들
	let reg = /^[0-9]+$/;
	let regTarget = new Map();
	regTarget.set(document.getElementById('product_stock'), '재고');
	regTarget.set(document.getElementById('product_caloies'), '칼로리');
	regTarget.set(document.getElementById('product_price'), '가격');
	
	// 재고, 칼로리, 가격 항목에 숫자만 있는지 확인
	for(let [key, name] of regTarget){
		if(!reg.test(key.value)){
			numClean(key, name);
			return false;
		}
	}

	// 텍스트 필수 기재인 항목들 
	let checkTarget = new Map();
	checkTarget.set(document.getElementById('product_category'), '카테고리');
	checkTarget.set(document.getElementById('product_title'), '상품명');
	checkTarget.set(document.getElementById('product_subtitle'), '상품 부제');
	checkTarget.set(document.getElementById('product_detail_text'), '상세설명');
	checkTarget.set(document.getElementById('product_origin'), '원산지');
	checkTarget.set(document.getElementById('product_ingredient'), '재료');
	
	// 해당 항목들에 기재된 내용이 있는지 확인
	for(let [key, name] of checkTarget){
		if(key.value == ''){
			textClean(key, name);
			return false;
		}
	}
	
	if(formtype == 'register'){
		// 이미지 경로를 통해 필수 이미지가 있는지 확인
		let img = document.getElementById('mainImgFile');
		let detail1 = document.getElementById('detailImgFile1');
		
		if(img.value == ''){
			alert('메인이미지는 필수로 있어야 합니다')
			img.focus();
			return false;
		}
		
		if(detail1.value == ''){
			alert('상세이미지1은 필수로 있어야 합니다')
			detail1.focus();
			return false;
		}
	}

	return true;
}

```

이후 컨트롤러에 POST 방식으로 해당 정보가 넘어가면, 
productDao 인스턴스를 통해 DB에 정보를 등록합니다  
이미지 파일은 서버단에 저장되며, 이 때 편의를 위해 product[상품번호][메인인지/상세인지].확장자 의 형태로 이름을 변경해주었습니다  
</br>
```xml
<insert id="register">
  insert into TB_PRODUCT values(#{product_no}, #{product_category}, #{product_stock}, 
    #{product_title}, #{product_subtitle}, #{product_main_img}, #{product_caloies}, 
    #{product_origin}, #{product_storage}, #{product_detail_img1}, #{product_detail_img2}, 
    #{product_detail_text}, #{product_ingredient}, #{product_price}, sysdate)
</insert>
```
정보 등록을 위해 작성한 쿼리문입니다  
</br>
### 상품 수정  
![edit_product](https://user-images.githubusercontent.com/80666066/120113313-dbc00280-c1b4-11eb-93db-b3e489423040.gif)
상품 수정 및 삭제에 들어가면 현재 사이트에 등록된 모든 상품 목록을 가져와서 보여줍니다  

```jsp
<div class="table-container">
  <div class="table-wrap">
    <table class="table table-hover">
      <thead>
        <tr>
          <th>상품번호</th>
          <th>카테고리</th>
          <th>상품명</th>
          <th>재고</th>
          <th>메인이미지</th>
          <th>수정</th>
          <th>삭제</th>
        </tr>
      </thead>
      
      <c:forEach var="p" items="${list }">
        <tr>
          <td id="${p.product_no }">${p.product_no }</td>
           <td>${p.product_category }</td>
           <td>${p.product_title }</td>
           <td>${p.product_stock }</td>
           <td><img src="/img/${p.product_main_img }" style="width: 80px; height: 30px; object-fit: cover;"></td>
           <td><i class="fas fa-edit" id="edit-icon" onclick="location.href='/admin/product-edit/${p.product_no }'"></i></td>
           <td><i class="fas fa-trash-alt" id="delete-icon" onclick="deleteConfirmProduct('${p.product_no }','${p.product_title }')"></i></td>
         </tr>
       </c:forEach>
     </table>
   </div>
</div>
```  
jsp의 core 태그를 이용한 반복문으로 상품 목록을 보여줄 표를 만들었습니다  
수정 아이콘을 누를 시, /admin/product-edit/상품번호 의 형태로 GET방식 접근이 이뤄집니다  
컨트롤러에서는 해당 상품의 정보를 조회해서, 상세 정보를 보여주는 페이지로 연결해줍니다  
```java
//GET 방식 접근일 때, 수정 대상 상품의 상세 정보 보기
@RequestMapping(value = "/product-edit/{productNo}", method = RequestMethod.GET)
public ModelAndView editProductFormDetail(@PathVariable String productNo, HttpServletRequest request) {
  ModelAndView mav = new ModelAndView();
  int no = Integer.parseInt(productNo);
  mav.addObject("product", dao.selectOne(no));
  mav.setViewName("/admin/productEditDetail");
  return mav;
}
```
</br>
상세 상품 페이지에서 정보를 수정하고 버튼을 눌렀을 시, ajax 통신을 이용해 정보를 전송합니다  
수정이 한 번에 끝나지 않을 수도 있기에 새로고침 없이 계속 작업할 수 있게 하였습니다  

```javascript

let productNo = document.getElementById('product_no').value;
let ajaxUrl = '/admin/product-edit/' + productNo;

$('#product_edit_submit').click(function(){

	if(checkProductForm('edit')){
    let updateData = new FormData(document.getElementById('product_editor'));	
    $.ajax({
      url:ajaxUrl,
      data:updateData,
      type:'post',
      processData:false,
      contentType:false,
      success:function(){
        alert("상품 정보가 수정되었습니다.");
      }
    });
  }
});
```
정보 수정에 성공했다면 성공했다는 안내를 띄웁니다  
정보를 수정할 때도 등록과 마찬가지로 화면단에서 유효성 검사를 거치도록 했습니다  
</br>
```xml
<update id="update">
  update TB_PRODUCT
    set product_category=#{product_category}, product_stock=#{product_stock}, product_title=#{product_title},
        product_subtitle=#{product_subtitle}, product_main_img=#{product_main_img}, product_caloies=#{product_caloies},
        product_origin=#{product_origin}, product_storage=#{product_storage}, product_detail_img1=#{product_detail_img1},
        product_detail_img2=#{product_detail_img2}, product_detail_text=#{product_detail_text},
        product_ingredient=#{product_ingredient}, product_price=#{product_price} 
  where product_no = #{product_no}
</update>
```
정보 수정을 위해 작성한 쿼리문입니다  
PK인 상품 번호를 이용해 정보를 수정했습니다  
</br>

### 상품 삭제  
![delete-product](https://user-images.githubusercontent.com/80666066/120113949-cd271a80-c1b7-11eb-8c0b-5cab10426893.gif)
상품 목록에서 삭제 아이콘을 누르면 확인 알림을 띄운 후 삭제합니다  
```javascript
//상품 삭제 시 확인창을 띄우는 함수
function deleteConfirmProduct(no, productName){
  let deleteConfirm = confirm(no +'번 상품 : ' + productName + ' 삭제합니다');
  if(deleteConfirm){
    location.href = "/admin/product-edit/delete?no=" + no;
  }
}
```
삭제 확인을 누를 시, 해당 상품 번호를 쿼리 스트링 형식으로 전달합니다  
컨트롤러에서 서버에 저장된 해당 상품의 이미지를 모두 삭제하고, DB의 정보도 삭제합니다  
</br>
```java
//특정 상품의 DB 데이터와 서버에 저장된 관련 이미지를 삭제
@RequestMapping("/product-edit/delete")
public String deleteProduct(int no, HttpServletRequest request) {
    //서버에서 해당 상품의 관련 이미지를 삭제
    ProductVo pv = dao.selectOne(no);
    
    //해당 상품의 이미지 경로를 배열에 담고, 차례로 삭제
    List<String> imgList = new ArrayList<>();
    imgList.add(request.getRealPath("/img") + "/" + pv.getProduct_main_img());
    imgList.add(request.getRealPath("/img") + "/" + pv.getProduct_detail_img1());
    if(pv.getProduct_detail_img2() != null) {
      imgList.add(request.getRealPath("/img") + "/" + pv.getProduct_detail_img2());			
    }
		
    for(String path : imgList) {
      util.deleteImg(path);
    }
    
    //DB에서 해당 상품의 데이터를 삭제
    dao.deleteProduct(no);
    return "redirect:/admin/product-edit";
}
```
</br>
삭제를 위해 작성한 쿼리문입니다  
```xml
<delete id="delete">
  delete from TB_PRODUCT
    where product_no = #{no}
</delete>
```

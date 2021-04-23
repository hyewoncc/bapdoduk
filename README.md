## 반찬 쇼핑몰 '밥도둑'   
쌍용교육센터 Java 풀스택 개발자 코스에서 진행했던 최종 프로젝트입니다  



### 기술 스택

- Java, SpringBoot
- Oracle Database, Mybatis
- JSP, JavaScript, HTML, CSS 
- Git, Github  
<br/>

### 분담한 기능

구현한 기능과 페이지 상세 사항은 문서 하단에 배치했습니다. 각 항목을 클릭하면 이동합니다.



- [특정 기간동안 상품을 할인판매 하는 **타임세일**](#타임세일)
- [상품들을 묶어 판매하는 **세트판매**](#세트판매)
- [진행중인 이벤트를 보여주는 **이벤트 게시판**](#이벤트 게시판)
- 상품명으로 **상품검색** 
- **카테고리**별 상품 목록 보여주기  

  
관리자 페이지에서 다음을 맡았습니다

- 상품 정보, 세일 정보, 세트 정보의 CRUD
- 이벤트 게시판의 CRUD 
<br/>

### 프로젝트를 하면서

웹개발을 배우면서 처음으로 해 본 팀단위 작업입니다. 현대인을 위한 반찬 쇼핑몰이라는 서비스 기획부터 테이블 설계, 클래스와 뷰 설계, 화면 설계를 거쳐 구현에 이르기까지 일련의 과정을 거치면서 많은 것들을 배웠습니다. 



1. Spring MVC 패턴 구조를 이해하고, 목표한 구조와 기능을 구현했습니다.
2. Oracle Database로 요구사항에 맞는 테이블을 설계하고 생성했습니다.
3. 필요한 SQL문을 작성하고 MyBatis 프레임워크를 응용했습니다.
4. HTML, JSP로 화면을 구성했습니다.
5. CSS/JavaScript 라이브러리인 Bootstrap을 활용하고, 필요에 따라 손수 작성했습니다. 
6. Git으로 형상 관리를 하였습니다.
7. Github을 통해 여럿이서 협력 개발을 방법을 공부하고 활용했습니다.  



몇 달간 배운 것들을 모두 보여줄 수 있는 프로젝트를 하는 것이 목표였습니다.  
그런데 프로젝트를 진행하면서 물론 기술적으로 많은 발전이 있었지만, 협업 측면에서 정말 많은 것을 배웠습니다.  
예를 들어 같은 기능을 두고도 DB 설계 단계에서 팀원들끼리 테이블을 나누고 컬럼을 구성하는 방식이 달랐습니다. 거의 팀원 수만큼 다른 설계도가 나왔는데, 회의를 통해 취합하면서 혼자 생각해서는 절대 나오지 못했을 결과물이 나왔습니다. 

깃헙도 팀프로젝트를 하면서 처음 쓰기 시작했습니다. 각자 브랜치를 내고 코딩 후 합치는 과정에서 종종 충돌도 있었습니다. 병합 충돌을 해결하고, 충돌을 방지하기 위해 영역을 다시 구분하고, 다른 팀원이 하던 작업을 이어받아 하는 모든 과정이 새로운 경험이었습니다.  
어디서든 좋은 팀원이 될 수 있도록 개발 공부 뿐만이 아닌 소프트 스킬 공부의 필요성을 느꼈습니다.  
<br/>

### 앞으로의 계획

공식적인 팀프로젝트는 끝났지만 다음의 추가 기능을 꾸준히 개발해보려 합니다.

- 상품의 판매 동향을 관리자에게 보여주는 기능
- 상품 품절 처리와 재입고 알람을 요청한 회원에게 재입고시 알람을 주는 기능



또, 프로젝트를 진행하면서 구조 리펙토링이라는 개념을 접해서 이제까지 작성한 코드를 다시 읽어보고, 리펙토링을 진행하고 상황을 기록할 예정입니다.  
<br/>
<br/>
<br/>


## 구현 상세보기  



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

![sale02](https://user-images.githubusercontent.com/80666066/114559324-913b0300-9ca6-11eb-95d4-22dc080b3de8.png)



이용자가 페이지를 로드하는 시간 기준으로 현재 세일 진행중인 상품의 세일가와 할인율, 할인 종료까지 남은 시간을 보여줍니다.  
할인률과 남은 시간의 경우 JavaScript로 페이지 로드 시 마다 값을 계산합니다. 상품 목록에서도 세일 중인 상품은 세일가로 표기됩니다. 




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
<br/>
<br/>

### 세트판매 



작성중

  
### 이벤트 게시판
이벤트 게시판 기능을 구현했습니다.
이벤트 정보 테이블은 아래와 같습니다.

| 컬럼명             | 데이터 타입 | 내용           |
| :----------------- | :---------- | -------------- |
| EVENT_NO (PK)	| NUMBER	| 이벤트 고유 번호	|
| EVENT_TITLE	| VARCHAR2(100)	| 이벤트 제목 	|
| EVENT_CONTENT	| VARCHAR2(500) | 이벤트 정보 내용 	|
| EVENT_IMG	| VARCAHR2(100)	| 이벤트 배너 이미지	|
| EVENT_START	| DATE		| 이벤트 시작일	|
| EVENT_END	| DATE		| 이벤트 마감일	|
| EVENT_HIT	| NUMBER	| 이벤트 글 조회수	|
| EVENT_REGDATE	| DATE		| 이벤트 작성일	|
| EVENT_LINK	| VARCHAR2(100)	| 이벤트 관련 페이지 링크	|

![image](https://user-images.githubusercontent.com/80666066/115882006-49c32c80-a487-11eb-9160-908ab22c4815.png)
이벤트 페이지에 들어왔을 때  

![image](https://user-images.githubusercontent.com/80666066/115882372-a888a600-a487-11eb-96ea-6212c5f08be6.png)
특정 이벤트 배너를 클릭했을 때의 상세보기  


이용자가 이벤트 페이지에 들어오는 시각 시준으로 유효한 이벤트 정보만을 보여줍니다
```java
//현재 시간상 유효기간에 속하는 이벤트 데이터를 조회하고 반환합니다
public List<EventVo> selectValid(){
	List<EventVo> list = new ArrayList<>();
	list = sqlSession.selectList("event.selectValid", getNow());
	return list;
}
	
//db의 시간과 현재시간의 비교를 위해 현재시간을 원하는 포맷으로 추출
public String getNow() {
	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String now = sdf.format(date);
	return now;
}
```

쿼리문은 아래와 같이 짰습니다  
```
select * from tb_event 
	where	to_date(#{now}, 'yyyy-mm-dd hh24:mi:ss') > event_start 
	and	event_end > to_date(#{now}, 'yyyy-mm-dd hh24:mi:ss')
```

쇼핑몰 메인페이지에서는 현재 진행중인 이벤트 배너를 캐러셀 형식으로 보여줍니다   
리스트로 받아온 이벤트 정보를 jsp로 수와 정보에 맞춰 페이지 동적 생성을 하였습니다
```html
<div class="track">
	<c:forEach var="e" items="${eventList }">
		<div class = "slide">
			<a href="${e.event_link }"><img class="banner-image" src="img/${e.event_img }"></a>
		</div>
	</c:forEach>
</div>
			
<div class="dot-indicator">
	<c:forEach var="e" items="${eventList }">
		<div class="dot"></div>
	</c:forEach>
</div>
```

이벤트 상세글에서 링크 클릭 시, 메인페이지에서 배너 클릭 시 해당 이벤트 링크로 이동합니다 
![image](https://user-images.githubusercontent.com/80666066/115883573-e5a16800-a488-11eb-81e2-07048eede02f.png)







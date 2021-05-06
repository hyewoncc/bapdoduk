### 이벤트게시판

![event](https://user-images.githubusercontent.com/80666066/117336443-2b255280-aed7-11eb-937e-d4216f1c372f.gif)


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

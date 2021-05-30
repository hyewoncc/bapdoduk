### 이벤트 정보 관리 (등록/수정/삭제)

이벤트 정보 관리와 관련된 기능을 구현했습니다  
페이지가 길어져 사용한 기술을 먼저 요약하겠습니다  
- 기본적인 DB CRUD 쿼리문 작성과 이용  
- 서버에 이미지 파일 업로드  
- ajax 통신  
- 쿼리스트링을 통한 값 전달  
- jsp의 c태그를 이용한 html 동적 생성  

이하 구현한 내용에 대한 상세한 설명입니다  
</br>
</br>
이벤트 정보를 담고 있는 테이블은 다음과 같습니다  

| 컬럼명               | 데이터 타입    | 내용             |
| ------------------- | -------------- | ----------------|
| EVENT_NO            | NUMBER         | 이벤트 고유번호  |
| EVENT_TITLE         | VARCHAR2(100)  | 이벤트 제목      |
| EVENT_CONTENT       | VARCHAR2(500)   | 이벤트 내용    |
| EVENT_IMG         | VARCHAR2(100)         | 배너 이미지         |  
| EVENT_START | DATE | 시작일 |
| EVENT_END | DATE | 마감일 | 
| EVENT_REGDATE | DATE | 글 작성일 | 
| EVENT_IMG  | VARCHAR2(100)         | 배너 이미지         |  
| EVENT_HIT   | NUMBER  | 이벤트 조회수 |
| EVENT_LINK | VARCHAR2(100) | 이벤트 링크 | 
</br>

### 이벤트 등록  
![event-regi](https://user-images.githubusercontent.com/80666066/120123395-9f5ac980-c1e9-11eb-8576-8548b1a27248.PNG)

이벤트 등록에서는 새로운 이벤트를 등록할 수 있습니다  
입력된 정보는 컨트롤러에 POST 형식으로 전달되어, DB에 등록되게 됩니다  
</br>  

```java
//POST 방식 접근, 이벤트를 DB에 입력하고 결과를 반환
@RequestMapping(value = "/event-register", method = RequestMethod.POST)
public String registerEvent(EventVo ev, HttpServletRequest request) {
  
  String startDate = request.getParameter("startDate") + " " + request.getParameter("startTime") + ":00";
  String endDate = request.getParameter("endDate") + " " + request.getParameter("endTime") + ":00";
  ev.setEvent_start(startDate);
  ev.setEvent_end(endDate);
  
  //세트 이미지를 등록할 webapp/img 경로
  String path = request.getRealPath("/img");
  
  //파일 데이터를 받아와서 변수에 저장
  MultipartFile uploadImg = ev.getImgFile();
  
  //이미지 파일 이름을 event + 이벤트 고유 번호로 지정
  String imgName = util.renameEventImg(ev.getEvent_no(), uploadImg);
  
  //DB에 수정된 이미지 파일 이름을 저장
  ev.setEvent_img(imgName);
  
  try {
    util.uploadImg(path, uploadImg, imgName);
  } catch (IOException e) {
  }
  
  //이벤트 정보를 저장하는 쿼리문을 실행
  int re = eventdao.registerEvent(ev);
  
  return "redirect:/admin/event-register";
}
``` 
</br>

업로드된 이미지는 서버단에 event_고유번호.확장자 형식으로 등록되고, DB에는 이미지 이름이 올라갑니다  
실행되는 쿼리문은 아래와 같습니다  

```xml
<select id="register">
  insert into TB_EVENT values(
    #{event_no}, #{event_title}, #{event_content}, #{event_img},
    to_date(#{event_start}, 'yyyy-mm-dd hh24:mi:ss'),
    to_date(#{event_end}, 'yyyy-mm-dd hh24:mi:ss'),
    0, sysdate, #{event_link})
</select>
```
</br>
</br>

### 이벤트 수정  

![event-edit](https://user-images.githubusercontent.com/80666066/120123492-2019c580-c1ea-11eb-84bc-d04df3dbe88e.gif)  
이벤트 수정 및 삭제에서는 모든 이벤트 목록을 조회하여 표로 보여줍니다  
</br>

```java
//GET 방식 접근, 이벤트 목록을 보여줌
@RequestMapping(value = "/event-edit", method = RequestMethod.GET)
public ModelAndView editEvent(HttpServletRequest request) {
  request.getSession().setAttribute("category", "event");
  request.getSession().setAttribute("function", "eventEdit");
  ModelAndView mav = new ModelAndView();
  mav.addObject("list", eventdao.findAll());
  mav.setViewName("/admin/eventEdit");
  return mav;
}
```  
</br>  
전체 조회를 위해 실행되는 쿼리문은 아래와 같습니다  

```xml
<select id="findAll" resultType="eventVo">
  select * from tb_event
</select>
```
</br>
조회해서 생긴 List 값으로 jsp c태그를 통해 html을 동적 생성합니다  

```html
<div class="table-container">
  <div class="table-wrap">
    <table class="table table-hover">
      <thead>
        <tr>
          <td>글번호</td>
          <td>이벤트명</td>
          <td>시작일</td>
          <td>종료일</td>
          <td>배너</td>
          <td>수정</td>
          <td>삭제</td>
        </tr>
      </thead>
      
      <c:forEach var="e" items="${list }">
        <tr>
          <td id="${e.event_no }">${e.event_no }</td>
          <td>${e.event_title }</td>
          <td>${e.event_start }</td>
          <td>${e.event_end }</td>
          <td><img src="/img/${e.event_img }" style="width: 150px; height: 35px; object-fit: cover;"></td>
          <td><i class="fas fa-edit" id="edit-icon" onclick="location.href='/admin/event-edit/${e.event_no }'"></i></td>
          <td><i class="fas fa-trash-alt" id="delete-icon" onclick="deleteConfirm('${e.event_no}','${e.event_title}')"></i></td>
        </tr>
      </c:forEach>
    </table>
  </div>
</div>
```  

</br>
이 후, 정보를 수정하면 ajax 통신이 이뤄집니다  
성공할 시 성공했다는 알람을 보냅니다  

```javascript
let eventNo = document.getElementById('event_no').value;
let ajaxUrl = '/admin/event-edit/' + eventNo;

$('#event-register-submit').click(function(){

  let updateData = new FormData(document.getElementById('event-edit'));	
  
  $.ajax({
    url:ajaxUrl,
    data:updateData,
    type:'post',
    processData:false,
    contentType:false,
    success:function(){
      alert("이벤트 정보 수정에 성공하였습니다.");
    }
  });
});
```  
</br>

컨트롤러에서는 입력된 시간을 DB DATE와 맞는 형식으로 문자열 편집을 합니다  
또, 이미지가 수정되었을 시 서버단의 이미지를 교체합니다  

```java
//POST 방식 접근, 입력된 이벤트 정보를 수정함
@RequestMapping(value = "/event-edit/{eventNo}", method = RequestMethod.POST)
@ResponseBody
public String updateEvent(@PathVariable int eventNo, EventVo ev, HttpServletRequest request) {
  String startDate = request.getParameter("startDate") + " " + request.getParameter("startTime") + ":00";
  String endDate = request.getParameter("endDate") + " " + request.getParameter("endTime") + ":00";
  ev.setEvent_start(startDate);
  ev.setEvent_end(endDate);
  
  String path = request.getRealPath("/img");
  String imgName = "";
  
  if(ev.getImgFile().getSize() != 0) {
    //메인이미지가 변경되었다면 파일 삭제 후 새로운 이미지 파일명 지정
    String oldImgPath = path + "/" + ev.getEvent_img();
    util.deleteImg(oldImgPath);			
    imgName = util.renameEventImg(ev.getEvent_no(), ev.getImgFile());
    ev.setEvent_img(imgName);
    
    try {
      util.uploadImg(path, ev.getImgFile(), imgName);
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
  
  int re = eventdao.updateEvent(ev);
  
  return "";
}
```  
</br>

이벤트 정보 업데이트를 위해 실행되는 쿼리문은 아래와 같습니다  

```xml
<update id="update">
  update	TB_EVENT 
  set		event_title=#{event_title}, event_content=#{event_content}, event_img=#{event_img},
        event_start=to_date(#{event_start}, 'yyyy-mm-dd hh24:mi:ss'), 
        event_end=to_date(#{event_end}, 'yyyy-mm-dd hh24:mi:ss'), 
        event_link=#{event_link} 
  where event_no=#{event_no} 
</update>
```
</br>
</br>

### 이벤트 삭제  
![event-delete](https://user-images.githubusercontent.com/80666066/120123792-e8138200-c1eb-11eb-804f-5a081cc2f261.gif)  
목록에서 삭제 아이콘을 클릭하면 이벤트 글번호를 통해 DB에서 해당 정보를 삭제합니다  
글 번호는 url에 쿼리스트링 형식으로 컨트롤러에 전달됩니다  
</br>

```java
// 이벤트 이미지와 DB 데이터 삭제
@RequestMapping("/event-edit/delete")
public String deleteEvent(int no, HttpServletRequest request) {
  EventVo ev = eventdao.selectEvent(no);
  String path = request.getRealPath("/img") + "/" + ev.getEvent_img();
  util.deleteImg(path);
  int re = eventdao.deleteEvnet(no);
  
  return "redirect:/admin/event-edit";
}
```  

</br>
컨트롤러에서는 배너 이미지를 삭제하고, DB에서 정보를 삭제합니다  
삭제를 위해 실행되는 쿼리문은 아래와 같습니다  

```xml
<delete id="delete">
  delete from TB_EVENT
  where event_no=#{no} 
</delete>
``` 
</br>

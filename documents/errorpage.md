### 간단한 에러 페이지 처리  

404 에러 코드에 대응되는 페이지를 만들고 적용시켰습니다  
![error](https://user-images.githubusercontent.com/80666066/120124051-35dcba00-c1ed-11eb-9fcd-ccd05ddc1168.gif)  
없는 주소를 입력해 404 에러가 발생할 시, 기존 에러 페이지를 보여주는 대신에 별도의 안내 페이지를 보여줍니다  
</br>

```java
@Controller
public class ErrorPageController implements ErrorController{

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {

    //에러 코드 획득
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    //에러 코드 상태 정보
    HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));

    if(status != null) {
      int statusCode = Integer.valueOf(status.toString());
      if(statusCode == HttpStatus.NOT_FOUND.value()) {
        return "/error/404";
      }
    }

    return "";
  }
  
  @Override
  public String getErrorPath(){
  	return "/error";
}
}
```  
ErrorController 인터페이스를 구현한 ErrorPageController 클래스를 작성했습니다  
에러 발생 시 컨트롤러에서 발생한 에러의 형태를 확인해 에러 페이지로 연결시킵니다  


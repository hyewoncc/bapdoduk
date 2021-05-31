### 인터셉터를 이용한 접근 제한 기능  

![admin-enter](https://user-images.githubusercontent.com/80666066/120124233-5ce7bb80-c1ee-11eb-973b-7721c5db1618.gif)

관리자 페이지는 검증된 관리자만 접근 가능해야 하기 때문에, 인터셉터를 통해 접근을 제한시켰습니다  
주소를 입력해서 접근할 시, 관리자 비밀번호를 입력하게 합니다  

```java
//관리자 로그인이 유효한지 검증 후 관리자 페이지로 입장
@RequestMapping(value = "", method = RequestMethod.POST)
public String enterAdminMainpage(HttpServletRequest request) {
  
  //입력받은 비밀번호를 서버의 txt파일과 비교하여 검증
  String inputPwd = request.getParameter("inputPwd");
  String pwdPath = request.getRealPath("/adminPassword.txt");
  
  //일치할 경우 세션에 값을 저장, 관리자페이지 인터셉터에서 사용된다 
  if(util.checkPassword(inputPwd, pwdPath)) {
    request.getSession().setAttribute("admin", "true");
  }
  
  return "redirect:/admin/main";
}
```  
</br>  
입력된 비밀번호 값은 서버에 txt 형식으로 저장된 비밀번호로 검증합니다  
비밀번호가 일치하면 세션에 관리자임을 증명하는 값을 저장하고, 
이를 인터셉터에서 이용해 /admin 경로가 들어간 모든 관리자 페이지를 접근할 때 마다 확인합니다  
</br>

```java
@Configuration
public class AdminMvcConfig implements WebMvcConfigurer{
  
  @Autowired
  private AdminInterceptor interceptor;
  
  //관리자 페이지의 모든 주소에 관리자 페이지용 인터셉터가 동작하도록 등록 
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor) .addPathPatterns("/admin/*");
  }
}
```  
</br>
WebMvcContigurer 인터페이스를 상속해 /admin 경로의 모든 페이지 주소를 검증했습니다 
</br>  

```java
@Component
public class AdminInterceptor implements HandlerInterceptor{
  
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
    
    //세션의 admin 값이 "true"인지 확인하고, 아닐 경우 관리자 로그인 페이지로 이동시킴
    if(request.getSession().getAttribute("admin") != null && request.getSession().getAttribute("admin").equals("true")){
      return true;
    }else {
      response.sendRedirect("/admin");
      return false;
    }
  }
}
```  

HandlerInterceptor 인터페이스를 구현해 세션을 통해 관리자인지 확인했습니다  
만약 로그아웃을 했거나, 세션이 만료되어 관리자임이 확인되지 않으면,  
관리자 로그인 페이지로 사용자를 리다이렉트 시킵니다  
</br>



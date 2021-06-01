### 비회원 장바구니 담기 

세션을 이용해 비회원 장바구니 담기를 구현하고,  
로그인 시 자동으로 회원 장바구니로 전환하는 기능을 만들었습니다  
<br/>
![nonelogin00](https://user-images.githubusercontent.com/80666066/120287795-8c83ea00-c2fa-11eb-95d0-093c5fc2a226.gif)
<br/>
로그인 한 회원의 장바구니는 다른 팀원이 DB에 저장하는 방식으로 구현하였고,  
저는 세션을 이용한 비회원의 장바구니를 맡았습니다  

```java
// 장바구니에 상품 담기
@RequestMapping("/addCart")
public String insert(@ModelAttribute CartVo cv, HttpServletRequest request) {
  
  HttpSession session = request.getSession();
  
  //세일중이라면 세일가로 장바구니 정보를 수정
  if(request.getParameter("sale_price") != null) {
    cv.setProduct_price(Integer.parseInt(request.getParameter("sale_price")));
  }
  
  //로그인 상태 : DB에 장바구니 정보 저장
  //비로그인 상태 : 세션에 장바구니 정보 저장  
  if(session.getAttribute("login") != null) {
    String mem_id = (String)session.getAttribute("login");
    cv.setMem_id(mem_id);
    
    // 장바구니에 동일 상품이 있는지 검사
    int count = dao.countCart(cv.getProduct_no(), mem_id);
    if (count == 0) {
      // 장바구니에 동일 상품 없으면 insert
      dao.insertCart(cv);
    } else {
      // 장바구니에 동일상품 있으면 update
      dao.updateCart(cv);
		}	
  }else {
    //선택 정보로 CartVo 객체를 만들어서 세션 리스트에 추가
    List<CartVo> cartList = (ArrayList)session.getAttribute("cart");
    cv.setCart_no(cartList.size() + 1);
    cartList.add(cv);
    
    session.setAttribute("cart", cartList);
  }
  
  return "redirect:/listCart.do";
}
```
<br/>
세션에 cart라는 이름으로 장바구니 물품의 정보를 담고 있는 CartVo 객체를 리스트 형식으로 저장했습니다  
삭제와 수량변경도 회원 로그인 중일 시, 비회원일 시를 나눠서 처리하였습니다  

```java
// 장바구니 수량 변경
@RequestMapping("/update.do")
public String update(@RequestParam int[] product_qty, @RequestParam int[] product_no, HttpSession session) {
  
  //회원일 시 DB 테이블 값 수정
  //비회원일 시 세션 수정
  if(session.getAttribute("login") != null) {
    
    String mem_id = (String)session.getAttribute("login");
    // 레코드 갯수만큼 반복문 실행
    for (int i = 0; i < product_no.length; i++) {
      CartVo cv = new CartVo();
      cv.setMem_id(mem_id);
      cv.setProduct_qty(product_qty[i]);
      cv.setProduct_no(product_no[i]);
      dao.modifyCart(cv)
      }
  }else {
    //비회원일 시 세션 장바구니 리스트를 순회하며 해당 상품 번호의 물품 수량을 변경
    List<CartVo> list = (ArrayList)session.getAttribute("cart");
    for(int i = 0; i < product_no.length; i++) {
      for(CartVo cv : list) {
        if(cv.getProduct_no() == product_no[i]) {
          cv.setProduct_qty(product_qty[i]);
        }
      }
    }
  }
  
  return "redirect:/listCart.do";
}
```
<br/>

```java
// 장바구니에 담긴 상품 삭제
@RequestMapping("/delete.do")
public String delete(@RequestParam int cart_no, HttpSession session) {
  
  //회원일 시 DB 테이블 값 수정
  //비회원일 시 세션 수정
  if(session.getAttribute("login") != null) {
    dao.deleteCart(cart_no);
  }else {			
    List<CartVo> list = (ArrayList)session.getAttribute("cart");
    for(CartVo cv : list) {
      if(cv.getCart_no() == cart_no) {
        list.remove(cv);
        break;
      }
    }
  }
  
  return "redirect:/listCart.do";
}
```
<br/>
<br/>
비회원 상태로 장바구니를 담은 뒤 로그인을 할 시, 
세션에 담겨있는 장바구니를 DB에 옮기고, 기존 세션 정보는 삭제하도록 했습니다  

![login_cart](https://user-images.githubusercontent.com/80666066/120290270-264c9680-c2fd-11eb-906c-45ec743bf671.gif)

<br/>

```java
// 로그인 버튼 클릭시 post 요청에 의한 Process
@PostMapping("userLogin")
public String loginSubmit(Model model,HttpSession session,HttpServletRequest request,HttpServletResponse response,String mem_id, String mem_pwd) throws IOException {
  
  /* 타 팀원이 작성한 로그인 관련 코드 생략 */ 
  //비로그인 때 장바구니에 담은 상품이 있다면 DB로 정보를 옮겨준다
  if(((ArrayList)session.getAttribute("cart")).size() != 0) {
    cartdao.deleteCartAll(mem_id);
    List<CartVo> cartList = (ArrayList)session.getAttribute("cart");
    cartutil.moveCartToDb(cartList, cartdao, mem_id, session);
  }
  
  // 메인페이지로 리다이렉트
  return "redirect:/";
}//loginSubmit
```
<br/>

```java
@Component
public class CartUtil{
  //비로그인 때 세션에 담았던 장바구니 정보를 로그인 후 DB로 옮겨준다
  public void moveCartToDb(List<CartVo> list, CartDao cartdao, String mem_id, HttpSession session){
    for(CartVo cv : list) {
      cv.setMem_id(mem_id);
      cartdao.insertCart(cv);
    }
    
    session.removeAttribute("cart");
  }
}
```
<br/>


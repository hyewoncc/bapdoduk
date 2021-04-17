package com.mall.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mall.dao.cart.CartDao;
import com.mall.util.CartUtil;
import com.mall.vo.cart.CartVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

	private final CartDao dao;
	private final CartUtil cartUtil;

	// 장바구니에 상품 담기
	@RequestMapping("/addCart")
	public String insert(@ModelAttribute CartVo cv, HttpServletRequest request) {
		
		HttpSession session = request.getSession();
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
			cartList.add(cv);
			
			session.setAttribute("cart", cartList);
		}
		return "redirect:/listCart.do";
	}

	// 장바구니 목록보기
	@RequestMapping("/listCart.do")
	public ModelAndView listCart(HttpSession session, ModelAndView mav) {
		
		/*
		 * 로그인 유무에 따라 장바구니 보기를 다르게 처리한다
		 * 회원의 장바구니는 DB에 저장되어 있다
		 * 비회원의 장바구니는 세션에 저장되어 있다
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		int sumPriceCart = 0;
		List<CartVo> list;
		
		if(session.getAttribute("login") != null) {
			String mem_id = (String) session.getAttribute("login");
			list = dao.listCart(mem_id); // 장바구니 정보
			sumPriceCart = dao.sumPriceCart(mem_id); // 장바구니 전체금액
			
		} else {
			list = (ArrayList)session.getAttribute("cart");
			sumPriceCart = cartUtil.sumPrice(list);
		}
		
		// 장바구니 전체 금액에 따른 배송료 구분
		// 배송료(10만원 이상 무료, 미만 2500원)
		int fee = sumPriceCart >= 100000 ? 0 : 2500;
		map.put("list", list);
		map.put("count", list.size());
		map.put("sumPriceCart", sumPriceCart);
		map.put("fee", fee);
		map.put("totalPriceCart", sumPriceCart + fee);
		
		mav.setViewName("listCart");
		mav.addObject("map", map);	
		return mav;
	}

	// 장바구니 수량 변경
	@RequestMapping("/update.do")
	public String update(@RequestParam int[] product_qty, 
		@RequestParam int[] product_no, HttpSession session) {
			String mem_id = (String) session.getAttribute("login");
			// 레코드 갯수만큼 반복문 실행
			for (int i = 0; i < product_no.length; i++) {
				CartVo cv = new CartVo();
				cv.setMem_id(mem_id);
				cv.setProduct_qty(product_qty[i]);
				cv.setProduct_no(product_no[i]);
				dao.modifyCart(cv);
		}
		return "redirect:/listCart.do";
	}

	// 장바구니에 담긴 상품 삭제
	@RequestMapping("/delete.do")
	public String delete(@RequestParam int cart_no) {
		dao.deleteCart(cart_no);
		return "redirect:/listCart.do";
	}
	
	// 장바구니 선택 상품 삭제
	@RequestMapping(value = "/selectDelete", method = RequestMethod.POST)
	public String selectDelete(HttpSession session,
	     @RequestParam(value = "checkBox[]") List<String> checkArr, CartVo cart) throws Exception {
			String mem_id = (String) session.getAttribute("login");
			int cart_no = 0;
			 
			if(mem_id != null) {
				cart.setMem_id(mem_id);
				  
				for(String i : checkArr) {   
					cart_no = Integer.parseInt(i);
					cart.setCart_no(cart_no);
					dao.selectDeleteCart(cart);
				}   
			}  
		return "redirect:/listCart.do";
		}
	
	}

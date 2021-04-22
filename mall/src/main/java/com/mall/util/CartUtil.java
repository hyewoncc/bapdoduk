package com.mall.util;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.mall.dao.cart.CartDao;
import com.mall.vo.cart.CartVo;

@Component
public class CartUtil {
	public int sumPrice(List<CartVo> list) {
		int sum = 0;
		for(CartVo cv : list) {
			cv.setProduct_total(cv.getProduct_price() * cv.getProduct_qty());
			sum += cv.getProduct_total();
		}
		return sum;
	}
	
	//비로그인 때 세션에 담았던 장바구니 정보를 로그인 후 DB로 옮겨준다
	public void moveCartToDb(List<CartVo> list, CartDao cartdao, String mem_id, HttpSession session) {
		for(CartVo cv : list) {
			cv.setMem_id(mem_id);
			cartdao.insertCart(cv);
		}
		session.removeAttribute("cart");
	}
}

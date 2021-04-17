package com.mall.util;

import java.util.List;

import org.springframework.stereotype.Component;

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
}

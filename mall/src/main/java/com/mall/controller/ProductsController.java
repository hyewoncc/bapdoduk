package com.mall.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mall.dao.product.ProductDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ProductsController {
	
	private final ProductDao dao;
	
	public static int pageSIZE = 12;	// 한 화면에 보여줄 게시물 수
	public static int totalRecord = 0;	// 총 게시물 수
	public static int totalPage = 1;	// 페이지 번호


	// 상품 목록 조회
	@RequestMapping("/listProducts.do")
	public ModelAndView listProducts(HttpServletRequest request,
		
	@RequestParam(value = "pageNUM", defaultValue = "1") int pageNUM) {
		totalRecord = dao.countPage();
		totalPage = (int) Math.ceil(totalRecord / (double) pageSIZE);

		System.out.println("pageNUM:" + pageNUM);
		
		int start = (pageNUM - 1) * pageSIZE + 1;
		int end = start + pageSIZE - 1;

		System.out.println("시작레코드:" + start);
		System.out.println("끝나는레코드:" + end);
		System.out.println("-------------------------------");
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("list", dao.pagingProduct(start, end));
		mav.addObject("totalPage", totalPage);
		return mav;
	}
	
	@RequestMapping("/detailProducts.do")
	public ModelAndView detail(int no) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("p", dao.selectOne(no));
		return mav;
		}
	
		/*
		 * @RequestMapping("/listProducts.do") public ModelAndView listProducts() {
		 * ModelAndView mav = new ModelAndView(); mav.setViewName("listProducts");
		 * mav.addObject("list", dao.findAll()); return mav; }
		 */
	
}

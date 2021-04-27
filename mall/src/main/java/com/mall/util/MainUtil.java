package com.mall.util;

import org.springframework.stereotype.Component;

@Component
public class MainUtil {

	public String removeBlank(String keyword) {
		//검색어 시작이 공백문자일 때 공백문자 제거
		while(keyword.startsWith(" ")) {
			keyword = keyword.substring(1, keyword.length());
		}
		//검색어 끝이 공백문자일 때 공백문자 제거
		while(keyword.endsWith(" ")) {
			keyword = keyword.substring(0, keyword.length()-1);
		}
		
		return keyword;
	}
}

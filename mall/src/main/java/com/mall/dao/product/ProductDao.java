package com.mall.dao.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.mall.db.SqlSessionFactoryBean;
import com.mall.vo.product.ProductVo;

@Repository
public class ProductDao {
	
	private SqlSession sqlSession;
	
	public ProductDao() {
		sqlSession = SqlSessionFactoryBean.getSqlSession();
	}
	
	public List<ProductVo> findAll(){
		List<ProductVo> list = null;
		list = sqlSession.selectList("products.findAll");
		return list;
	}	
	
	// 페이징 처리를 위한 상품 시작,끝 값 가져오기
	public List<ProductVo> pagingProduct(int start, int end) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("start", start);
		map.put("end", end);

		List<ProductVo> list = sqlSession.selectList("products.paging", map);
		return list;
	}	
	
	//상품번호 no로 한 상품의 전체 데이터를 가져옵니다
	public ProductVo selectOne(int no) {
		ProductVo pv = sqlSession.selectOne("products.select", no);
		return pv;
	}
	
	public int registerProduct(ProductVo pv) {
		int re = sqlSession.update("products.register", pv);
		if (re == 1) {
			sqlSession.commit();
		}
		return re;
	}
	
	//상품번호 no 제품의 정보를 변경하고 성공 여부를 반환합니다
	public int updateProduct(ProductVo pv) {
		int re = sqlSession.update("products.update", pv);
		if(re == 1) {
			sqlSession.commit();
		}
		return re;
	}
	

	//상품번호 no 제품을 상품정보 테이블에서 삭제하고 성공 여부를 반환합니다
	public int deleteProduct(int no) {
		int re = sqlSession.delete("products.delete", no);
		if(re == 1) {
			sqlSession.commit();
		}
		return re;
	}

	//특정 문구가 이름에 포함된 상품 목록을 반환합니다
	public List<ProductVo> searchByName(String keyword){
		keyword = ("%" + keyword + "%");
		List<ProductVo> list = new ArrayList<>();	
		try {
			list = sqlSession.selectList("products.searchByName", keyword);			
		}catch(Exception e) {
			//검색 결과가 없을 때 예외 발생
		}
		return list;
	}
	
	public void commit() {
    	sqlSession.commit();
    }

	public int countPage() {
		int total = sqlSession.selectOne("products.countPage");
		return total;
	}
	

}

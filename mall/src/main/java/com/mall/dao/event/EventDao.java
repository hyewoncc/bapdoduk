package com.mall.dao.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.mall.db.SqlSessionFactoryBean;
import com.mall.vo.event.EventVo;

@Repository
public class EventDao {

private SqlSession sqlSession;
	
	public EventDao() {
		sqlSession = SqlSessionFactoryBean.getSqlSession();
	}

	//이벤트 글을 등록하고 성공 여부를 반환합니다
	public int registerEvent(EventVo ev) {
		int re = sqlSession.update("event.register", ev);
		if(re == 1) {
			sqlSession.commit();
		}
		return re;
	}
	
	//제일 최근 등록된 이벤트 글의 번호를 반환합니다
	public int getNextNo() {
		int re = 0;
		try {
			re = sqlSession.selectOne("event.getNextNo");
		} catch(Exception e) {
			//이벤트 데이터 개수가 0일 때 예외 발생
		}
		return re + 1;
	}
	
	//이벤트 글을 모두 찾아 리스트로 반환
	public List<EventVo> findAll(){
		List<EventVo> list = new ArrayList<>();
		list = sqlSession.selectList("event.findAll");
		return list;
	}
	
	//현재 시간상 유효기간에 속하는 이벤트 데이터를 조회하고 반환합니다
	public List<EventVo> selectValid(){
		List<EventVo> list = new ArrayList<>();
		list = sqlSession.selectList("event.selectValid", getNow());
		return list;
	}
	
	//db의 시간과 현재시간의 비교를 위해 현재시간을 원하는 포맷으로 추출
	public String getNow() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(date);
		return now;
	}
	
	public void commit() {
    	sqlSession.commit();
    }
}


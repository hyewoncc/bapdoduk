<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h2>${faq_title }</h2>
	<hr>
	<form action="insertFAQ.do" method="post" enctype="multipart/form-data">
		<input type="hidden" name="no" value="${faq_no }">
		글제목 : <input type="text" name="faq_title"><br>
		글내용 : <br>
		<textarea rows="10" cols="80" name="faq_content"></textarea><br>
		첨부파일 : <input type="file" name="uploadFile"><br>
		관리자 : <input type="text" name="faq_manager"><br>
		<input type="submit" value="등록">
		<input type="reset" value="취소">
	</form>
</body>
</html>


<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
   	<!-- Compiled and minified CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">

    <!-- Compiled and minified JavaScript -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>

<h2>Ż�� �ȳ�</h2>
ȸ��Ż�� ��û�ϱ� ���� �ȳ� ������ �� Ȯ�����ּ���.<br>
<hr>
<br>
����ϰ� ��� <strong>���̵�(${mVo.mem_id })</strong>�� Ż���� ��� ���� �� ������ �Ұ����մϴ�.<br>
Ż���� ���̵�� ���ΰ� Ÿ�� ��� ���� �� ������ �Ұ��Ͽ��� �����ϰ� �����Ͻñ� �ٶ��ϴ�.<br>

<strong>Ż�� �� ȸ������ �� ������ ���� �̿����� ��� �����˴ϴ�.</strong>
ȸ������ �� �ֹ�����, ���� �� ������ ���� �̿����� ��� �����Ǹ�, ������ �����ʹ� �������� �ʽ��ϴ�.<br>
�����Ǵ� ������ Ȯ���Ͻð� �ʿ��� �����ʹ� �̸� ����� ���ּ���.<br>

<hr>

<strong>Ż�� �Ŀ��� �Խ����� ���񽺿� ����� �Խù��� �״�� ���� �ֽ��ϴ�.</strong><br>
����Խ���,1:1�Խ��� � �ø� �Խñ� �� ����� Ż�� �� �ڵ� �������� �ʰ� �״�� ���� �ֽ��ϴ�.
������ ���ϴ� �Խñ��� �ִٸ� �ݵ�� Ż�� �� �����Ͻñ� �ٶ��ϴ�.
Ż�� �Ŀ��� ȸ�������� �����Ǿ� ���� ���θ� Ȯ���� �� �ִ� ����� ����, �Խñ��� ���Ƿ� �����ص帱 �� �����ϴ�.

<hr>

<form action="deleteAccount.do" method="post" id="deleteForm">
Ż�� �Ŀ��� ���̵� (${mVo.mem_id }) �� �ٽ� ������ �� ������ ���̵�� �����ʹ� ������ �� �����ϴ�.
�Խ����� ���񽺿� ���� �ִ� �Խñ��� Ż�� �� ������ �� �����ϴ�.<br>
<input type="checkbox" id="deleteVaild" > <label for="deleteVaild"><strong>�ȳ� ������ ��� Ȯ���Ͽ�����, �̿� �����մϴ�.</strong></label>
	<input type="button" value="Ȯ��" id="deleteCheck"  > 
</form>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
	$("#deleteCheck").on('click',function(){
	    if(!$("#deleteVaild").is(":checked")){
	    	alert("Ż�� �Ϸ��� �����ϱ⸦ �����ž��մϴ�.");
	    }else{
	    	$("#deleteForm").submit();
	    }
	});
</script>

</body>
</html>
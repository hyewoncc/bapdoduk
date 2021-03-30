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
    <!-- css -->
	<link rel="stylesheet" href="/static/css/myPage/wrap.css">
	<link rel="stylesheet" href="/static/css/myPage/deleteAccount.css">
	
</head>
<body>
<div class="wrap">
<h2>Ż�� �ȳ�</h2>
ȸ��Ż�� ��û�ϱ� ���� �ȳ� ������ �� Ȯ�����ּ���.
<p>
<hr>
<br>
����ϰ� ��� <span id="deleteId">���̵�(${mVo.mem_id })</span>�� Ż���� ��� ���� �� ������ �Ұ����մϴ�.<br>
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
Ż�� �Ŀ��� ���̵� (<span id="deleteId">���̵�(${mVo.mem_id })</span>) �� �ٽ� ������ �� ������ ���̵�� �����ʹ� ������ �� �����ϴ�.
�Խ����� ���񽺿� ���� �ִ� �Խñ��� Ż�� �� ������ �� �����ϴ�.<br>

<div class="row">
    <p>
      <label>
        <input type="checkbox" id="deleteVaild"/>
        <span>�ȳ� ������ ��� Ȯ���Ͽ�����, �̿� �����մϴ�.</span>
      </label>
    </p>
	<div class="row">
		<a class="waves-effect waves-light btn-large" id="deleteCheck"><i class="material-icons left">delete</i>Ż���ϱ�</a>
	</div>
</div>

<input type="checkbox" id="deleteVaild" > <label for="deleteVaild"></label>
</form>
</div>
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
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
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

<form action="deleteAccount.do" method="post">
Ż�� �Ŀ��� ���̵� (${mVo.mem_id }) �� �ٽ� ������ �� ������ ���̵�� �����ʹ� ������ �� �����ϴ�.
�Խ����� ���񽺿� ���� �ִ� �Խñ��� Ż�� �� ������ �� �����ϴ�.<br>
<input type="checkbox" id="deleteVaild"> <label for="deleteVaild"><strong>�ȳ� ������ ��� Ȯ���Ͽ�����, �̿� �����մϴ�.</strong></label>
	<input type="submit" value="Ȯ��" id="deleteCheck" checked > 
</form>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>

	$("#deleteCheck").on('click',function(){
		let chk = $(this).is(":checked");
		aelrt(chk);
	});

</script>

</body>
</html>
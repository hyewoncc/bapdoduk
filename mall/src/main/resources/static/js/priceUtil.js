
//가격을 천단위로 문자열 포매팅
function priceFormat(price){
	let price_string = String(price);
	return price_string.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}


//원가와 세일가격을 받아 할인율을 계산해주는 함수
function setSalerate(origin, sale, saletag){
	let gap = origin - sale;
	let salerate = gap / origin * 100;
	salerate = Math.floor(salerate);
	
	saletag.innerHTML = '-' + salerate + '%할인';
}

if(document.getElementById('origin_price')){
	let origin = document.getElementById('product_price').getAttribute('value');
	let sale = document.getElementById('sale_price').getAttribute('value');
	setSalerate(origin, sale, document.getElementById('sale_tag'));
}

<?php
header("Content-Type: text/html; charset=UTF-8");
?>

<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<script>
<script>
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분입니다.
            // 예제를 참고하여 다양한 활용법을 확인해 보세요.

            setAddress(data.zonecode, data.roadAddress, jibunAddress);
            // 현재 상황 : DaumPostalCD.kt 에서 javascriptinterface를 사용 못함 -> setAddress() 호출 불가능
        }
    }).open();
</script>


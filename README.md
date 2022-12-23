[comment]: <> (# 설문조사 기반 데이터 공유 플랫폼)

[comment]: <> (<div align = "center">)

[comment]: <> (<img src="https://user-images.githubusercontent.com/114554407/204599467-083b39e7-1a35-4d34-98ed-113a79120bf2.png" width="500" height="auto"/><br>)

[comment]: <> (<img src="https://user-images.githubusercontent.com/114554407/204599631-b55fb419-93ad-407a-bd1a-5dd13934f3fc.png" width="500" height="auto"/><br>)

[comment]: <> (<img src="https://user-images.githubusercontent.com/114554407/204601023-40c32791-0bb4-407c-adf9-83ca29f80588.png" width="500" height="auto"/><br>)

[comment]: <> (<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white"/>)

[comment]: <> (<img src="https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=React&logoColor=black"/>)

[comment]: <> (<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white"/>)

[comment]: <> (</div>)
# Survey Service
|Method|API|Description|
|---|---|---|
|GET|/survey-service/v1/survey/list|설문 목록 조회|
|GET|/survey-service/v1/survey/search_list|설문 목록 검색|
|GET|/survey-service/v1/survey/ptcp_list|설문 참여 목록 조회|
|GET|/survey-service/v1/survey/make_list|설문 생성 목록 조회|
|POST|/survey-service/v1/survey/reg|설문 생성|
|POST|/survey-service/v1/survey/detail|설문 상세 정보 조회|
|GET|/survey-service/v1/survey/best|인기 설문 조회|
|POST|/survey-service/v1/survey/vulgarismInsert|비속어 저장|
|GET|/survey-service/v1/survey/vulgarismList|비속어 목록 조회|
|GET|/survey-service/v1/survey/download/excel|설문 엑셀 다운로드|
|GET|/survey-service/v1/survey/category/list|설문 카테고리 전체 조회|
|GET|/survey-service/v1/survey/category/selectlist|설문 카테고리 조회|
|POST|/survey-service/v1/survey/category/reg|설문 카테고리 생성|
|POST|/survey-service/v1/survey/category/del|설문 카테고리 삭제|
|POST|/survey-service/v1/answer/user_answer|답변 조회(사용자)|
|POST|/survey-service/v1/answer/list|답변 조회(설문)|
|POST|/survey-service/v1/answer/reg|답변 등록|
|POST|/survey-service/v1/answer/mod|답변 수정|
|POST|/survey-service/v1/answer/del|답변 삭제|


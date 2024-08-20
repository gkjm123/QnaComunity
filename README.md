# QnA 커뮤니티 만들기
특정 주제에 대한 질문 또는 답변을 주고받는 사이트입니다.

### 프로젝트 기능 및 설계

* 회원가입 기능
  * 사용자는 Oath2 또는 JWT 토큰 방식을 통해 회원가입을 할 수 있습니다.
  * 로그인 아이디, 패스워드, 닉네임, 이메일을 입력받습니다.
  * 회원가입 후 시작 스코어 50 점이 자동 지급됩니다.
  * 회원정보 조회, 수정, 패스워드 변경, 프로필 업데이트, 회원탈퇴가 가능합니다.
  * 회원 탈퇴시 DB에서 삭제되지 않고 삭제시간(deleted_at) 이 세팅되며 로그인 아이디가 변경됩니다.

* 질문 기능
  * 로그인 후 질문을 등록할 수 있습니다.
  * 제목, 내용, 보상 스코어(최소 5점), 키워드를 입력받습니다.
  * 보상 스코어만큼 질문자가 가지고 있는지 확인후 질문이 등록되고, 스코어가 질문자에게 차감됩니다.
  * 저장시 생성시간, 조회수(hits=0) 이 자동으로 세팅됩니다.
  * 조회, 수정, 삭제 가능하며, 답변이 달린 질문은 수정, 삭제 할 수 없습니다.
  * 질문 조회시 조회수가 1씩 올라갑니다. Redission Lock 을 사용합니다.
  * 삭제시 DB에서 삭제되지 않고 삭제시간(deleted_at) 이 세팅됩니다.
  * 질문 저장후 ElasticSearch 에도 저장하고, 저장 실패시 로그가 남아 주기적으로 DB 와 연동작업을 합니다.

* 답변 기능
  * 로그인 후 답변을 등록할 수 있습니다.
  * 자신의 글에는 답변할 수 없습니다.
  * 질문ID, 답변 내용을 입력받습니다.
  * 조회, 수정, 삭제 가능하며, 채택된 답변은 수정, 삭제 할 수 없습니다.
  * 삭제시 DB에서 삭제되지 않고 삭제시간(deleted_at) 이 세팅됩니다.

* 채택 기능
  * 답변 아이디를 입력받습니다.
  * 질문 작성자 본인인지, 이미 채택된 질문이 아닌지 체크 후 답변을 채택합니다.
  * 채택시 질문자에게 페이백 스코어 5점, 답변자에게 질문의 보상 스코어가 지급됩니다.
  * 채택된 답변의 채택시간(picked_at) 이 세팅됩니다.

* 스코어 기능
  * 스코어의 증감,차감 시 Redisson Lock 을 사용합니다.
  * 스코어 변경시 History table 에 변경 내역이 기록됩니다.  

* 키워드 기능
  * 질문 등록시 키워드를 #으로 구분하여 입력하면 수동으로 세팅되며
  * 미입력시 형태소 분석을 통해 키워드(최대 3개)를 자동 생성합니다.

* 검색 기능
  * ElasticSearch 를 사용하여 질문을 검색합니다.
  * 제목/내용 검색 또는 키워드 검색으로 나뉩니다.
  * 제목/내용 검색은 제목/제목+내용, 최신순/정확도순 을 설정하여 검색할 수 있습니다.
  * 키워드 검색은 #으로 구분해 입력하면 해당 키워드가 모두 일치하는 글만 최신순으로 반환합니다.

* 연관글 기능
  * 질문에서 연관글 보기를 누르면 ElasticSearch 를 이용해 열람중인 질문과 키워드 일치수가  
    높은 순으로(최소 1개 일치) 최대 3개까지 연관글을 보여줍니다.

* 랭킹 기능
  * 멤버 랭킹(스코어 기준), 키워드 랭킹(등록수 기준) 으로 나뉩니다.
  * Redis Cache 를 이용해 변동 사항을 실시간으로 확인 가능합니다.
  * 각각 10위 까지 보여줍니다.

* 등급 기능(매니저 기능)
  * 매니저 역할을 가진 회원은 스코어에 따른 등급(grade)을 생성,수정,삭제 할 수 있습니다.
  * 등급은 등급이름, 최소 score 값을 가집니다.
  * 등급 조회시 최소 score 를 만족하는 가장 높은 등급을 찾아 반환합니다.
  * RedisCache 를 이용해 이미 조회했던 스코어에 대한 등급을 바로 반환해줍니다. 

### ERD
![qna](https://github.com/user-attachments/assets/8bd7ede9-18da-4aec-a6e0-865d9e6277cf)


### 사용 기술
* Java, Sring, MySQL, Oauth2, Redis Cache, Redisson Lock, ElasticSearch, AWS S3, Komoran(한글 분석기)

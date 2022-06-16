# Petmily Android Application

**2022-1학기 한성대학교 컴퓨터공학부 캡스톤디자인**

<img src="Images/petmily.jpg" style="zoom: 33%;" />

## 팀 구성

**팀장** : 박우림(Client)   

**팀원** : 박준석(Server), 주권영(Server), 정예린(Client)

## Youtube Link
### Youtube 동영상(전시 영상)

[<img src="https://user-images.githubusercontent.com/70479375/170943139-be641897-fd9e-4d55-b515-ad926531100b.png" alt="전시 표지"
 width = "360" height="180" />](https://youtu.be/CfLEWmCUOZU)
### Youtube 동영상(발표 영상)
#### *** 자막 사용해서 시청하세요 ***

[<img src="https://user-images.githubusercontent.com/70479375/170943139-be641897-fd9e-4d55-b515-ad926531100b.png" alt="전시 표지"
 width = "360" height="180" />](https://youtu.be/OhhwRF_aNI8)
 
## Ios Github
[Petmily-Ios-App](https://github.com/bagoonichanger/Petmily_Ios_App)

## Server Github

[Petmily-Server](https://github.com/jookwonyoung/petmily-server)

[Petmily-Flask-Server](https://github.com/wolfdate25/Petmily_flask_server)



## 목차

- ### 이용 전, 필요한 부분



### 1. 소개

- 앱 소개
- 주제를 선정하게 된 이유
- 주요 적용 기술
- 구조
- 간략한 기능 설명
- 앱 사용 방법



### 2. 사전 설정 및 환경 구축

- Google



### 3. 기능 구현


### 4. 차별성

### 5. 실용성

### 6. 시장성

### 7. 기대효과

### 8. 결론



## 이용 전, 필요한 부분



- 안드로이드 스튜디오 3.4.0 이상의 Version

> [Android Studio Download](https://developer.android.com/studio)

- API Level : API 수준 27 이상(권장 : API 수준 30)



##  1. 소개

### 앱 소개

<img src="Images/petmily.jpg" style="zoom: 33%;" />

이 어플리케이션은 총 5가지의 기능을 제공한다.

- 딥러닝을 통한 반려동물 감정 분석
- 딥러닝을 통한 반려동물 종 구분
- 딥러닝을 통한 게시물 자동 태그
- GPS를 이용한 산책 기록 및 관리
- GPS를 이용한 주변 서비스(장소) 검색 및 저장 기능



### 주제를 선정하게 된 이유

많은 미디어 매체에서는 반려동물 관련 콘텐츠를 방영하고, SNS에서는 전용 계정이 생기는 등 반려동물에 대한 관심이 뜨겁다. 하지만 SNS의 광고나 무분별한 게시글로 피로를 느껴, 관심 있는 분야만 즐길 수 있는 플랫폼을 찾는 사용자가 증가하고 있다. 이에 반려동물에 집중한 커뮤니티(SNS)와 반려동물 케어에 도움이 되는 기능을 갖춘 ‘Petmily’ 애플리케이션을 개발하게 되었다.



### 주요 적용 기술

**개발 언어** : Java, Kotlin, Mustache, Python, SQL

**개발 도구** : Android Studio, IntelliJ IDEA, PyTorch, PyCharm

**개발 환경** : Amazon EC2, Amazon RDS, Flask Server, MySQL, Spring Boot

**주요 기술** : Imagine Classification, Object Detection, REST API



### 구조

![Structure](Images/Structure1.jpg)



------



![Structure](Images/Structure2.jpg)

### 간략한 기능 설명

####  1.  홈 화면

<img src="Images/3_1.png" width="360" height="720" />



- 구글 연동을 통한 로그인
- 실시간 날씨 확인 (온도, 바람, 구름, 습도)
- 즐겨찾기, 감정분석으로 이동할 수 있는 버튼

#### 2. 동물 감정 분석

<img src="Images/a3_2.png"/>

- 개/고양이 분류, 종 분류, 감정 분석
- 앨범 속 사진이나 카메라로 찍어서 사용

#### 3. 커뮤니티 화면

<img src="Images/a3_3.png"/>

- 개, 고양이 사진만 업로드 가능
- 딥 러닝을 이용한 자동 태그 기능 (개, 고양이 종 / 감정)
- 좋아요, 댓글, 공유 기능
- 좋아요 한 게시물만 모아볼 수 있는 버튼

#### 4. 산책 화면

<img src="Images/a3_4-1.png"/>



<img src="Images/a3_4-2.png"/>

- GPS을 이용한 실시간 산책 위치 확인
- 산책 종료 시 자동으로 기록 저장
- background에서도 작동할 수 있게 상태바 기능
- 날짜 별 산책 기록 확인

#### 5. 장소 공유 화면

<img src="Images/a3_5.png"/>

- 키워드 검색 장소 추천 기능

- 나만의 장소 즐겨찾기, 공유 및 삭제 기능

  

## 2. 사전 설정 및 환경 구축

### Google

Server와 Google 로그인을 연동하기 위해서는 Google Cloud Platform에 프로젝트를 등록해야 한다.

![2_1](Images/2_1.png)

------

Google 로그인을 사용 하므로 '디버그 서명 인증서 SHA-1'을 알아야 한다.

<'SHA-1'은 안드로이드 스튜디오 오른쪽에 있는 Gradle -> Tasks->android->signInReport를 클릭 하면 알 수 있다>

![2_2](Images/2_2.jpg)

------

구성 파일을 다운로드 한 뒤 , 생성된 프로젝트 파일->app 폴더 에다가 저장한다(JSON 형식)

![2_3](Images/2_3.jpg)

------

![2_4](Images/2_4.png)



#### Android Studio에 google Login SDK 추가 완료



## 3. 기능구현

> [Petmily Android Application 기능구현](https://github.com/bagoonichanger/Petmliy_android_app/blob/master/Functional%20implementation.md)
>
> 상세 설명은 링크를 통해 확인 하세요!



## 4. 차별성

- 반려동물을 인식하고 감정을 분석함으로써, 대략적인 감정 파악과 사용자의 흥미를 이끌 수 있다.

- 이번 주의 열정 그래프(Attendance Rate)를 통해서 금주 출석체크 기록을 확인할 수 있으며 자신의 부족함을 채워 다음주 공부계획을 체계적으로 세울 수 있다.

- 게시물을 올릴 때, 동물 인식을 통해 게시물에 적절한 태그를 자동으로 달아준다.

- 게시물을 올릴 때, 동물 사진이 없는 사진은 차단하여  반려동물 커뮤니티라는 점을 온전히 느낄 수 있다.

  (추후, 광고 구분 및 부적절한 이미지도 차단 가능하게 develop 할 예정)

- 산책 기록을 움직임에 따라 실시간으로 GPS를 이용하여 확인할 수 있다.

- 산책 관리 캘린더를 이용해 한달 기록을  가독성있게 확인 할 수 있다.



## 5. 실용성

사람들은 한 공간에서 모든 일들을 해결 하는 것을 추구하는 경향이 있다. 
예를 들어, 상점, 운동 시설, 도서관 등 우리가 생각할 수 있는 웬만한 것들은 아파트 단지안에서 이용 할 수 있다. 

이와 같이, 사람들은 수 많은 기능과 서비스를 하나의 애플리케이션에서 이용하길 원한다.



Petmily는 반려동물의 감정 분석 및 종 구분, 게시글 인식을 통한 자동 태그 및 게시글 제한, 산책 기록 및 장소 검색 등 다양한 기능을 통해서 반려동물에 집중 할 수 있는 환경과 반려동물 케어에 도움을 주기에 반려가구에 유용하게 자주 쓰일 것이다. 



또한, 실용성을 입증하기 위해서 PlayStore에 Petmily를 등록 할 예정이다.
안드로이드 애플리케이션이 안정화가 된다면 iOS 버전도 출시 예정이다.



> PlayStore Link(Android) : (예정)

> AppStore Link(iOS) :  (예정)



## 6. 시장성

- 반려가구 구성원들의 나이 스펙트럼 다양하기 때문에 넓은 수요층을 가질 수 있다.
- 기반은 SNS(커뮤니티) 및 위치 기반 서비스이기 때문에  지속적으로 추가 개발이 가능하다.
- 개인화된 서비스이기 때문에, 사용자 데이터를 체계적으로 관리할 수 있다.
- 다른 반려동물 관리 서비스와 다르게 ''감정 인식''이라는 차별화 둔 기능이 있기에 사람들의 호기심을 불러일으켜 사람들에 관심을 끌 수 있다.



## 7. 기대효과

- ### 오락성

  반려동물의 감정을 대략적으로 파악함으로써 재미와 적절한 활용 가능 

- ### 편의성

  산책 기록 및 관리, 장소 검색 및 저장 기능을 한 애플리케이션에서 손쉽게 이용 가능

- ### 안정성

  무분별한 이미지를 제한함으로써 사용자들의 피로도 감소



## 8. 결론

이번 프로젝트를 통해 체계적으로 기획부터 계획했던 결과물을 만들어 내는 과정을 직접 경험해 보았습니다.

사용하고 있는 모바일 애플리케이션들의 적용되는 대략적인 구성과 개발 과정을 파악함으로써 팀원들의 각 분야에 맞는 로드맵이 정립되는 시간이었습니다. 그리고, 단순히 개인들의 역량뿐만이 아니라 소통 그리고 협업을 위한 방법 또한 체득할 수 있었기에, 이를 적용하기에는 많은 시간이 들고 크고 작은 어려움들이 있었지만, 모든 부분에서 어떠한 방식으로든 많은 것들을 배울 수 있었습니다. 

 팀원들 모두 프로젝트를 원활히 진행하기 어려워 추가적인 공부를 하면서 프로젝트를 진행하는데 힘들어했지만, 포기하지 않고 마무리를 할 수 있게 되어 감사합니다.

# 프로젝트 소개
- 제목 : 관심사 같은 친구 찾기
- 구분 : 개인 프로젝트
- 개발환경  :  
서버 - AWS EC2, NodeJS  
DB - AWS RDS, MySQL
- 사용 언어 - Kotlin
- 내용  
프로필 설정(닉네임, 나이, 직업, 관심사 3개)  
나와 관심사가 같은 사람들의 목록 표시  
서로 좋아요 누를 시 채팅 방 생성

---

# 개요

프로필 수정 | 관심사 같은 사용자 목록
:------:|:-------:
![KakaoTalk_20210429_183729397](https://user-images.githubusercontent.com/70185380/116809874-70feb580-ab7b-11eb-92bc-521a97e65c9e.jpg) | ![KakaoTalk_20210429_183729550](https://user-images.githubusercontent.com/70185380/116809881-78be5a00-ab7b-11eb-8d41-9bba068e58a5.jpg)  

- 프로필 사진 정보는 FirebaseStorage, FirebaseFireStore 통해 저장 후 Glide 사용
- 관심사 중 하나만 같은 경우 해당 유저 목록  RecyclerView 통해 구현


상대방 프로필 | 채팅창 생성 | 채팅창
:------:|:-------:|:------:
![KakaoTalk_20210429_183729397](https://user-images.githubusercontent.com/70185380/116809857-5593aa80-ab7b-11eb-8446-d35ae4c97963.jpg) | ![KakaoTalk_20210429_183729246](https://user-images.githubusercontent.com/70185380/116809914-a1465400-ab7b-11eb-8da7-b29bfb1e4a45.jpg) | ![KakaoTalk_20210429_183729110](https://user-images.githubusercontent.com/70185380/116809902-925fa180-ab7b-11eb-921c-87ae96be28cd.jpg)

- 내가 좋아요 한 사람은 ILike 나를 좋아요 한 사람은 LikeMe 숫자로 알 수 있고 해당 숫자 누를 시 유저 목록 표시
- 서로 좋아요 누를 시 채팅 방 생성 : 좋아요 데이터는 FirebaseFireStore 사용
- 채팅 방은 FireBaseRealTimeDataBase 통해 구현





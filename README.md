# 🕹 TicTacTalk (mini_socket)

**Java 기반 소켓 통신 틱택토 & 채팅 애플리케이션**  
MSAFullstack 풀스택 Java 개발 과정의 "mini_socket" 미니 프로젝트

---

## 🎯 프로젝트 소개

TicTacTalk은 Java Swing GUI로 구현된 1:1 실시간 틱택토 게임과 채팅이 가능한 데스크탑 애플리케이션입니다.  
socket 통신 기반으로, 게임 로직과 메시징이 동시에 동작하며, 데이터베이스 연동을 통해 사용자 인증 및 전적 관리를 지원합니다 :contentReference[oaicite:1]{index=1}.

---

## ⚙️ 주요 기능

- **회원가입 & 로그인**
  - Oracle DB 또는 JDBC 연동 테스트용 샘플 DB를 통한 사용자 인증
- **실시간 매칭 & 게임**
  - Server ↔ Client 소켓 통신을 이용해 플레이어 2명 매칭
  - Tic-Tac-Toe 게임 로직, 승패/무승부 판정, 턴 타이머 적용
- **실시간 채팅**
  - 게임 중 채팅 송수신 기능 제공 :contentReference[oaicite:2]{index=2}
- **GUI**
  - Java Swing 기반
  - CardLayout을 이용한 화면 전환
  - 게임 보드, 채팅 창, 상태 표시 창 등 통합 UI

---

## 🛠️ 기술 스택

- Java 8 이상
- Java Swing (GUI)
- Socket 프로그래밍 (TCP 기반)
- JDBC + Oracle (또는 H2 등 테스트용 DB)
- MVC 패턴 기반 설계

---

## 🚀 실행 방법

1. 데이터베이스 설정  
2. 서버 실행
3. 클라이언트 실행
4. 두 명의 클라이언트에서 접속 ➝ 로그인 ➝ 실시간 매칭 ➝ 게임 & 채팅 시작
---

## 🔍 화면 예시
로그인 / 회원가입 화면
아이디ㆍ비밀번호 입력 + 로그인 버튼

대기 및 로딩 화면
자동 상대 매칭 중 표시되는 로딩 UI

게임 & 채팅 화면
틱택토 보드

채팅 메시지 송수신 창

턴 타이머, 현재 턴 표시

---

## ✨ 향후 개선 / 확장 아이디어
AI 상대 모드 추가 (미니맥스 알고리즘 기반)

사용자 등급 및 전적 차트 UI 개선

멀티 서버 구현: Redis 기반 채팅 브로드캐스트, MSA 구조로 확장

UI 리팩토링: JavaFX 또는 웹 UI 전환 가능

---
## 📌 참고
본 프로젝트는 AI application MSA 기반 풀스택 Java 개발 과정의 미니 프로젝트입니다.

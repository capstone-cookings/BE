# Server
캡스톤 cook 서버 레포지토리입니다.
### AWS Server

![스크린샷 2025-04-10 183244](https://github.com/user-attachments/assets/c61da1fa-f69f-4c78-9b87-c70adf12d562)

### ✉️ Commit Messge Rules
  - 반영사항을 바로 확인할 수 있도록 작은 기능 하나라도 구현되면 커밋을 권장합니다.
  - 기능 구현이 완벽하지 않을 땐, 각자 브랜치에 커밋을 해주세요.
    
### 📌 Commit Convention
**[태그] 제목의 형태**

| 태그 이름 | 설명 |
|-----------|------|
| FEAT      | 새로운 기능을 추가할 경우 |
| FIX       | 버그를 고친 경우 |
| CHORE     | 짜잘한 수정 |
| DOCS      | 문서 수정 |
| INIT      | 초기 설정 |
| TEST      | 테스트 코드, 리펙토링 테스트 코드 추가 |
| RENAME    | 파일 혹은 폴더명을 수정하거나 옮기는 작업인 경우 |
| STYLE     | 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우 |
| REFACTOR  | 코드 리팩토링 |



### 커밋 타입
  - `[태그] 설명` 형식으로 커밋 메시지를 작성합니다.
  - 태그는 영어를 쓰고 대문자로 작성합니다.

예시 >
```
  [FEAT] 검색 api 추가
```  
## Branch Convention (GitHub Flow)
- `main`: 배포 가능한 브랜치, 항상 배포 가능한 상태를 유지
- `feature/{description}`: 새로운 기능을 개발하는 브랜치
    - 예: `feature/add-login-page`
### Flow
1. `main` 브랜치에서 새로운 브랜치를 생성.
2. 작업을 완료하고 커밋 메시지에 맞게 커밋.
3. Pull Request를 생성 / 팀원들의 리뷰.
4. 리뷰가 완료되면 `main` 브랜치로 병합.
5. 병합 후, 필요시 배포.
   **예시**:
```bash
# 새로운 기능 개발
git checkout -b feature/add-login-page
# 작업 완료 후, main 브랜치로 병합
git checkout main
git pull origin main
git merge feature/add-login-page
git push origin main
```

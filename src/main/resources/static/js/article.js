// 삭제 기능
const deleteButton = document.getElementById('delete-btn');
// 1. id 가 delete-btn 으로 설정한 엘리먼트를 찾음

// if (deleteButton) { // 기존 삭제 (spring security X, token X, OAuth X)
//     // 2. delete-btn의 클릭 이벤트가 발생하면, fetch() 메소드를 통해 /api/articles/DELETE 요청을 보내는 역할
//     deleteButton.addEventListener('click', event => {
//         let id = document.getElementById('article-id').value;
//
//         // 3. fetch() 메소드에 이어지는 then() 메소드는 fetch()가 완료되면 연이어 실행되는 메소드
//         fetch(`/api/articles/${id}`, {
//             method: 'DELETE'
//         })
//             .then(() => {
//                 alert('삭제가 완료되었습니다.');
//                 // 4. alter() 메소드는 then() 메소드가 실행되는 시점에서 웹 브라우저 화면에서 삭제 완료를 알리는 팝업을 띄우는 메소드
//                 location.replace('/articles');
//                 // 5. location.replace() : 실행 시 사용자의 웹 브라우저 화면을 현재 주소를 기반해 옮겨주는 메소드
//             });
//     });
// }

if (deleteButton) {
    // 2. delete-btn의 클릭 이벤트가 발생하면, fetch() 메소드를 통해 /api/articles/DELETE 요청을 보내는 역할
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;

        function success() {
            alert("삭제가 완료되었습니다.");
            location.replace("/articles");
        }

        function fail() {
            alert("삭제 실패했습니다.");
            location.replace("/articles");
        }

        httpRequest("DELETE", "/api/articles/" + id, null, success, fail);
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn'); // 1. id가 modify-btn 인 엘리먼트 조회

// if(modifyButton) { // 기존 수정 (security X, token X, OAuth X)
//     // 2. 클릭 이벤트가 감지되면 수정 API 요청
//     modifyButton.addEventListener('click', event => {
//         // id가 title, content인 엘리먼트의 값을 가져옴
//         let params = new URLSearchParams(location.search);
//         let id = params.get('id');
//
//         // fetch() 메소드를 통해 수정 API로 /api/articles/PUT 요청을 보냄
//         fetch(`/api/articles/${id}`, {
//             method : 'PUT',
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify({
//                 title: document.getElementById('title').value,
//                 content: document.getElementById('content').value
//             })
//         })
//             .then(() => {
//                 alert('수정이 완료되었습니다.');
//                 location.replace(`/articles/${id}`);
//             });
//     });
// }

if(modifyButton) { // token 사용
    // 2. 클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', event => {
        // id가 title, content인 엘리먼트의 값을 가져옴
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });

        function success() {
            alert("수정 완료되었습니다.");
            location.replace("/articles/" + id);
        }

        function fail() {
            alert("수정 실패했습니다.");
            location.replace("/articles/" + id);
        }

        httpRequest("PUT", "/api/articles/" + id, body, success, fail);
    });
}

// 등록 기능
// 1. id가 create-btn인 엘리먼트
const createButton = document.getElementById("create-btn");

// if(createButton) { // 기존 접속, 로그인 후 글 생성 (security X, token X, OAuth X)
//     // 2. 클릭 이벤트가 감지되면 생성 API 요청
//     createButton.addEventListener("click", (event) => {
//         // 2-1. id가 create-btn인 엘리먼트를 찾아, 그 엘리먼트에서 클릭이벤트가 발생하면,
//         fetch("/api/articles", {
//             // 2-3. fetch() 메소드를 통해 생성 API로 /api/articles/POST 요청을 보냄
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify({
//                 // 2-2. id가 title, content인 엘리먼트 값을 가져와서
//                 title: document.getElementById("title").value,
//                 content: document.getElementById("content").value,
//             }),
//         }).then(() => {
//             alert("등록 완료되었습니다.");
//             location.replace("/articles");
//         });
//     });
// }

if (createButton) { // token 기반 요청 사용
    // 등록 버튼을 클릭하면 .api/articles로 요청을 보냄
    createButton.addEventListener("click", (event) => {
        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });
        function success() {
            alert("등록 완료되었습니다.");
            location.replace("/articles");
        }

        function fail() {
            alert("등록 실패했습니다.");
            location.replace("/articles");
        }

        httpRequest("POST", "/api/articles", body, success, fail);
    });
    
}

// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(";");
    cookie.some(function (item) {
        item = item.replace(" ", "");

        var dic = item.split("=");

        if (key == dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: "Bearer" + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body: body,
    }).then((response) => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie("refresh_token");
        if (response.status === 401 && refresh_token) {
            fetch("/api/token", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: getCookie("refresh_token"),
                }),
            })
                .then((res) => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then((result) => {
                    // 재발급이 성공하면 로컬 스토리지 값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem("access_token", result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch((error) => fail());
        } else {
            return fail();
        }
    });
}

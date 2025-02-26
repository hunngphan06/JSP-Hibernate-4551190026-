<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quản lý bài viết</title>
    <style>
        .post-form {
            margin-bottom: 20px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .post-list {
            display: grid;
            gap: 20px;
        }
        .post-item {
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .post-actions {
            margin-top: 10px;
            display: flex;
            gap: 10px;
        }
        .btn {
            padding: 5px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-edit {
            background-color: #4CAF50;
            color: white;
        }
        .btn-delete {
            background-color: #f44336;
            color: white;
        }
    </style>
</head>
<body>
    <h1>Quản lý bài viết</h1>

    <div class="post-form">
        <h2>Đăng bài mới</h2>
        <form action="${pageContext.request.contextPath}/post" method="post">
            <div>
                <label for="title">Tiêu đề:</label>
                <input type="text" id="title" name="title" required>
            </div>
            <div>
                <label for="body">Nội dung:</label>
                <textarea id="body" name="body" required></textarea>
            </div>
            <button type="submit" class="btn btn-submit">Đăng bài</button>
        </form>
    </div>

    <div class="post-list">
        <c:forEach items="${posts}" var="post">
            <div class="post-item">
                <h3>${post.title}</h3>
                <p>${post.body}</p>
                <small>Đăng bởi: ${post.user.username} - ${post.createdAt}</small>
                
                <c:if test="${sessionScope.role == 'ADMIN' || sessionScope.user.id == post.user.id}">
                    <div class="post-actions">
                        <button onclick="editPost(`${post.id}`)" class="btn btn-edit">Sửa</button>
                        <button onclick="deletePost(`${post.id}`)" class="btn btn-delete">Xóa</button>
                    </div>
                </c:if>
            </div>
        </c:forEach>
    </div>

    <script>
        function deletePost(postId) {
            if (confirm('Bạn có chắc muốn xóa bài viết này?')) {
                fetch('${pageContext.request.contextPath}/post/' + postId, {
                    method: 'DELETE'
                }).then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                });
            }
        }

        function editPost(postId) {
            // Implement edit functionality
            const newTitle = prompt('Nhập tiêu đề mới:');
            const newBody = prompt('Nhập nội dung mới:');
            
            if (newTitle && newBody) {
                const formData = new FormData();
                formData.append('title', newTitle);
                formData.append('body', newBody);
                
                fetch('${pageContext.request.contextPath}/post/' + postId, {
                    method: 'PUT',
                    body: formData
                }).then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                });
            }
        }
    </script>
</body>
</html> 
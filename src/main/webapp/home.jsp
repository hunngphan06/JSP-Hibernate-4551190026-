<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>Trang chủ</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 20px;
                    background-color: #f0f2f5;
                }

                .header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20px;
                    padding: 10px 20px;
                    background-color: white;
                    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                }

                .user-info {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .container {
                    max-width: 800px;
                    margin: 0 auto;
                }

                .post-form {
                    background-color: white;
                    padding: 20px;
                    border-radius: 8px;
                    margin-bottom: 20px;
                    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                }

                .post-form textarea {
                    width: 100%;
                    padding: 10px;
                    margin: 10px 0;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    resize: vertical;
                }

                .post-list {
                    display: flex;
                    flex-direction: column;
                    gap: 20px;
                }

                .post-card {
                    background-color: white;
                    padding: 20px;
                    border-radius: 8px;
                    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                }

                .post-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 10px;
                }

                .post-author {
                    font-weight: bold;
                }

                .post-date {
                    color: #666;
                    font-size: 0.9em;
                }

                .post-content {
                    margin: 10px 0;
                }

                .btn {
                    padding: 8px 16px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    font-weight: bold;
                }

                .btn-primary {
                    background-color: #1877f2;
                    color: white;
                }

                .btn-primary:hover {
                    background-color: #166fe5;
                }

                .pagination {
                    display: flex;
                    justify-content: center;
                    gap: 10px;
                    margin-top: 20px;
                }

                .page-link {
                    padding: 8px 12px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    color: #1877f2;
                    text-decoration: none;
                }

                .page-link.active {
                    background-color: #1877f2;
                    color: white;
                    border-color: #1877f2;
                }

                .post-form-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 15px;
                }

                .btn-close {
                    background: none;
                    border: none;
                    font-size: 24px;
                    cursor: pointer;
                    color: #666;
                }

                .btn-close:hover {
                    color: #333;
                }

                .form-actions {
                    display: flex;
                    gap: 10px;
                    margin-top: 10px;
                }

                .form-control {
                    width: 100%;
                    padding: 10px;
                    margin-bottom: 10px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                }
            </style>
        </head>

        <body>
            <div class="header">
                <h1>Trang chủ</h1>
                <div class="user-info">
                    <span>Xin chào, ${sessionScope.user.username}</span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn">Đăng xuất</a>
                </div>
            </div>

            <div class="container">
                <!-- Nút đăng bài -->
                <button onclick="togglePostForm()" class="btn btn-primary" style="margin-bottom: 20px;">
                    <i class="fas fa-plus"></i> Đăng bài mới
                </button>

                <!-- Form đăng bài (mặc định ẩn) -->
                <div id="postForm" class="post-form" style="display: none;">
                    <div class="post-form-header">
                        <h2>Đăng bài mới</h2>
                        <button onclick="togglePostForm()" class="btn-close">&times;</button>
                    </div>
                    <form action="${pageContext.request.contextPath}/post" method="post">
                        <input type="text" name="title" placeholder="Tiêu đề bài viết" required class="form-control">
                        <textarea name="body" rows="4" placeholder="Nội dung bài viết" required></textarea>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Đăng bài</button>
                            <button type="button" onclick="togglePostForm()" class="btn">Hủy</button>
                        </div>
                    </form>
                </div>

                <!-- Danh sách bài viết -->
                <div class="post-list">
                    <c:forEach items="${posts}" var="post">
                        <div class="post-card">
                            <div class="post-header">
                                <div class="post-meta">
                                    <div class="post-author">
                                        <i class="fas fa-user"></i>
                                        <span>Người dùng: ${post.user.username}</span>
                                    </div>
                                    <div class="post-date">
                                        <i class="far fa-clock"></i>
                                        <fmt:parseDate value="${post.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss"
                                            var="parsedDate" type="both" />
                                        <span>
                                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </span>
                                    </div>
                                </div>
                                <c:if test="${sessionScope.user.id == post.user.id}">
                                    <div class="post-actions">
                                        <form action="post/edit" method="get" style="display: inline;">
                                            <input type="hidden" name="postId" value="${post.id}">
                                            <button type="submit" class="btn btn-edit">
                                                <i class="fas fa-edit"></i> Sửa
                                            </button>
                                        </form>
                                        <!-- Xóa bài viết -->
                                        <!-- Form không hỗ trợ DELETE nên dùng fetch để xóa -->

                                        <button type="button" class="btn btn-delete" onclick="deletePost('${post.id}')">
                                            <i class="fas fa-trash-alt"></i> Xóa
                                        </button>
                                    </div>
                                </c:if>
                            </div>
                            <h3 class="post-title">${post.title}</h3>
                            <div class="post-content">
                                <p>${post.body}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Phân trang -->
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}" class="page-link">&laquo; Trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="?page=${i}" class="page-link ${currentPage == i ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}" class="page-link">Sau &raquo;</a>
                    </c:if>
                </div>
            </div>

            <script>
                function togglePostForm() {
                    const form = document.getElementById('postForm');
                    if (form.style.display === 'none') {
                        form.style.display = 'block';
                    } else {
                        form.style.display = 'none';
                    }
                }

                function editPost(postId) {
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
            </script>
        </body>

        </html>
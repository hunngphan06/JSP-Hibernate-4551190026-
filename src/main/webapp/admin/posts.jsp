<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>Admin - Quản lý bài viết</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }

                    .admin-header {
                        background-color: #333;
                        color: white;
                        padding: 15px;
                        margin: -20px -20px 20px -20px;
                    }

                    .admin-container {
                        max-width: 1200px;
                        margin: 0 auto;
                    }

                    .posts-table {
                        width: 100%;
                        border-collapse: collapse;
                        background-color: white;
                        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
                    }

                    .posts-table th,
                    .posts-table td {
                        padding: 12px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }

                    .posts-table th {
                        background-color: #4CAF50;
                        color: white;
                    }

                    .posts-table tr:hover {
                        background-color: #f5f5f5;
                    }

                    .status-select {
                        padding: 5px;
                        border-radius: 4px;
                        border: 1px solid #ddd;
                    }

                    .action-btn {
                        padding: 5px 10px;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                        margin-right: 5px;
                    }

                    .edit-btn {
                        background-color: #4CAF50;
                        color: white;
                    }

                    .delete-btn {
                        background-color: #f44336;
                        color: white;
                    }

                    .status-ACTIVE {
                        color: #4CAF50;
                    }

                    .status-PENDING {
                        color: #ff9800;
                    }

                    .status-DELETED {
                        color: #f44336;
                    }

                    .filters {
                        margin-bottom: 20px;
                        display: flex;
                        gap: 10px;
                    }

                    .search-box {
                        padding: 8px;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                        width: 200px;
                    }

                    .btn {
                        padding: 5px 10px;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                        margin: 0 5px;
                    }

                    .btn-approve {
                        background-color: #4CAF50;
                        color: white;
                    }

                    .btn-reject {
                        background-color: #f44336;
                        color: white;
                    }

                    .status-badge {
                        padding: 3px 8px;
                        border-radius: 3px;
                        font-size: 14px;
                    }

                    .status-pending {
                        background-color: #ffd700;
                        color: #000;
                    }

                    .status-approved {
                        background-color: #4CAF50;
                        color: white;
                    }

                    .status-rejected {
                        background-color: #f44336;
                        color: white;
                    }

                    .header {
                        background-color: #f8f9fa;
                        padding: 15px;
                        border-radius: 4px;
                    }

                    .btn {
                        padding: 8px 16px;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                        color: white;
                    }

                    .btn:hover {
                        opacity: 0.9;
                    }
                </style>
            </head>

            <body>
                <div class="admin-header">
                    <div class="admin-container">
                        <h1>Quản lý bài viết</h1>
                    </div>
                </div>

                <div class="admin-container">
                    <div class="header"
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                        <h2>Quản lý bài viết</h2>
                        <form action="${pageContext.request.contextPath}/logout" method="get" style="margin: 0;">
                            <button type="submit" class="btn" style="background-color: #dc3545;">Đăng xuất</button>
                        </form>
                    </div>

                    <div class="filters">
                        <input type="text" id="searchInput" class="search-box" placeholder="Tìm kiếm bài viết...">
                        <select id="statusFilter" class="status-select">
                            <option value="">Tất cả trạng thái</option>
                            <option value="ACTIVE">Đang hoạt động</option>
                            <option value="DELETED">Đã xóa</option>
                        </select>
                    </div>

                    <table class="posts-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Nội dung</th>
                                <th>Tác giả</th>
                                <th>Ngày tạo</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${posts}" var="post">
                                <tr>
                                    <td>${post.id}</td>
                                    <td>${post.title}</td>
                                    <td>${post.body}</td>
                                    <td>${post.user.username}</td>
                                    <td>
                                        <fmt:parseDate value="${post.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss"
                                            var="parsedDate" type="both" />
                                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm:ss" />
                                    </td>
                                    <td>
                                        <span class="status-badge status-${post.status.toLowerCase()}">
                                            ${post.status}
                                        </span>
                                    </td>
                                    <td>
                                        <c:if test="${post.status == 'ACTIVE'}">
                                            <button class="btn btn-reject"
                                                onclick="deletePost('${post.id}')">Xóa</button>
                                            <a href="${pageContext.request.contextPath}/admin/posts/edit/${post.id}"
                                                class="btn btn-approve">Sửa</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <script>
                    //Form không hỗ trợ DELETE nên dùng fetch để xóa
                    function deletePost(postId) {
                        if (confirm('Bạn có chắc muốn xóa bài viết này?')) {
                            fetch(`${pageContext.request.contextPath}/admin/posts?postId=` + postId, {
                                method: 'DELETE'
                            })
                                .then(response => {
                                    if (response.ok) {
                                        window.location.reload();
                                    } else {
                                        alert('Có lỗi xảy ra khi xóa bài viết');
                                    }
                                })
                                .catch(error => {
                                    console.error('Error:', error);
                                    alert('Có lỗi xảy ra khi xóa bài viết');
                                });
                        }
                    }

                    // Implement search functionality
                    document.getElementById('searchInput').addEventListener('input', function (e) {
                        const searchText = e.target.value.toLowerCase();
                        const rows = document.querySelectorAll('.posts-table tbody tr');

                        rows.forEach(row => {
                            const title = row.children[1].textContent.toLowerCase();
                            const author = row.children[2].textContent.toLowerCase();

                            if (title.includes(searchText) || author.includes(searchText)) {
                                row.style.display = '';
                            } else {
                                row.style.display = 'none';
                            }
                        });
                    });

                    // Implement status filter
                    document.getElementById('statusFilter').addEventListener('change', function (e) {
                        const status = e.target.value;
                        const rows = document.querySelectorAll('.posts-table tbody tr');

                        rows.forEach(row => {
                            const rowStatus = row.querySelector('.status-select').value;
                            if (!status || rowStatus === status) {
                                row.style.display = '';
                            } else {
                                row.style.display = 'none';
                            }
                        });
                    });

                    // Handle delete action
                    document.querySelectorAll('.btn-reject').forEach(button => {
                        button.addEventListener('click', function (e) {
                            e.preventDefault();
                            if (confirm('Bạn có chắc muốn xóa bài viết này?')) {
                                const postId = this.closest('form').querySelector('input[name="postId"]').value;

                                fetch(`${pageContext.request.contextPath}/admin/posts?postId=${postId}`, {
                                    method: 'DELETE'
                                }).then(response => {
                                    if (response.ok) {
                                        window.location.reload();
                                    } else {
                                        alert('Có lỗi xảy ra khi xóa bài viết');
                                    }
                                });
                            }
                        });
                    });
                </script>
            </body>

            </html>
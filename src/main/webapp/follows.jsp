<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>${listType == 'following' ? 'Đang theo dõi' : 'Người theo dõi'}</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <style>
                .header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20px;
                    padding: 10px 20px;
                    background-color: white;
                    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                }
                .follow-container {
                    max-width: 800px;
                    margin: 20px auto;
                    padding: 20px;
                    background: #fff;
                    border-radius: 8px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                }

                .follow-tabs {
                    display: flex;
                    border-bottom: 1px solid #ddd;
                    margin-bottom: 20px;
                }

                .follow-tabs a {
                    padding: 10px 20px;
                    text-decoration: none;
                    color: #666;
                    border-bottom: 2px solid transparent;
                }

                .follow-tabs a.active {
                    color: #1a73e8;
                    border-bottom: 2px solid #1a73e8;
                }

                .user-list {
                    display: flex;
                    flex-direction: column;
                    gap: 15px;
                }

                .user-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 15px;
                    border: 1px solid #ddd;
                    border-radius: 8px;
                }

                .user-info {
                    display: flex;
                    align-items: center;
                    gap: 15px;
                }

                .avatar {
                    width: 50px;
                    height: 50px;
                    border-radius: 50%;
                    object-fit: cover;
                }

                .user-details h3 {
                    margin: 0;
                    font-size: 16px;
                }

                .user-details p {
                    margin: 5px 0 0;
                    color: #666;
                    font-size: 14px;
                }

            </style>
        </head>

        <body>
            <div class="container">
                <div class="header">
                    <h1>Trang chủ</h1>
                    <div class="user-info">
                        <a href="${pageContext.request.contextPath}/follows/following" class="btn" style="margin-right: 10px; text-decoration: none; color: #000;">
                            <i class="fas fa-users"></i> Theo dõi
                        </a>
                        <span>Xin chào, ${sessionScope.user.username}</span>
                        <a href="${pageContext.request.contextPath}/logout" class="btn">Đăng xuất</a>
                    </div>
                </div>

                <div class="follow-container">
                    <div class="follow-tabs">
                        <a href="${pageContext.request.contextPath}/follows/following"
                            class="${listType == 'following' ? 'active' : ''}">
                            Đang theo dõi
                        </a>
                        <a href="${pageContext.request.contextPath}/follows/followers"
                            class="${listType == 'followers' ? 'active' : ''}">
                            Người theo dõi
                        </a>
                    </div>

                    <div class="user-list">
                        <c:choose>
                            <c:when test="${listType == 'following'}">
                                <c:forEach items="${following}" var="user">
                                    <div class="user-item">
                                        <div class="user-info">
                                            <div class="user-details">
                                                <h3>Tên: ${user.username}</h3>
                                            </div>
                                        </div>
                                        <button onclick="unfollowUser('${user.id}')" class="btn btn-danger">
                                            Bỏ theo dõi
                                        </button>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${followers}" var="user">
                                    <div class="user-item">
                                        <div class="user-info">
                                            <div class="user-details">
                                                <h3>${user.username}</h3>
                                            </div>
                                        </div>
                                        <button onclick="followUser('${user.id}')"
                                            class="btn btn-primary follow-btn-${user.id}">
                                            Theo dõi lại
                                        </button>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <script>
                function unfollowUser(userId) {
                    if (confirm('Bạn có chắc muốn bỏ theo dõi người dùng này?')) {
                        fetch('${pageContext.request.contextPath}/follow/' + userId, {
                            method: 'DELETE'
                        }).then(response => {
                            if (response.ok) {
                                location.reload();
                            }
                        });
                    }
                }

                function followUser(userId) {
                    fetch('${pageContext.request.contextPath}/follow/' + userId, {
                        method: 'POST'
                    }).then(response => {
                        if (response.ok) {
                            location.reload();
                        }
                    });
                }
            </script>
        </body>

        </html>
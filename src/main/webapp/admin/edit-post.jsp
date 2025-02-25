<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Sửa bài viết</title>
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
            max-width: 800px;
            margin: 0 auto;
        }
        
        .form-container {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.2);
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        .form-group input[type="text"],
        .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        
        .form-group textarea {
            height: 200px;
            resize: vertical;
        }
        
        .btn-container {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }
        
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .error-message {
            color: #dc3545;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <div class="admin-header">
        <div class="admin-container">
            <h1>Sửa bài viết</h1>
        </div>
    </div>
    
    <div class="admin-container">
        <div class="form-container">
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/admin/posts/edit" method="post">
                <input type="hidden" name="postId" value="${post.id}">
                
                <div class="form-group">
                    <label for="title">Tiêu đề</label>
                    <input type="text" id="title" name="title" value="${post.title}" required>
                </div>
                
                <div class="form-group">
                    <label for="body">Nội dung</label>
                    <textarea id="body" name="body" required>${post.body}</textarea>
                </div>
                
                <div class="form-group">
                    <label for="status">Trạng thái</label>
                    <select id="status" name="status" class="form-control">
                        <option value="ACTIVE" ${post.status == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                        <option value="DELETED" ${post.status == 'DELETED' ? 'selected' : ''}>Đã xóa</option>
                    </select>
                </div>
                
                <div class="btn-container">
                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                    <a href="${pageContext.request.contextPath}/admin/posts" class="btn btn-secondary">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html> 
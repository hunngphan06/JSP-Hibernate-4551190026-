package org.example.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.entity.User;
import org.example.util.JPAUtil;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy thông tin từ form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Kết nối đến database
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Thực hiện truy vấn để lấy user từ database
            User user = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getSingleResult();

            // Tạo session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            
            // Chuyển hướng dựa vào role
            if ("ADMIN".equals(user.getRole())) {
                // nếu role là admin thì chuyển hướng đến trang admin/dashboard
                response.sendRedirect(request.getContextPath() + "/admin/dashboard"); // ở đây ví dụ là admin/dashboard, sau này muốn thay gì thay
            } else {
                // nếu role là user thì chuyển hướng đến trang home
                response.sendRedirect(request.getContextPath() + "/home"); 
            }
            
        } catch (NoResultException e) {
            // nếu username không tồn tại thì hiển thị thông báo lỗi
            request.setAttribute("error", "Username không tồn tại!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }
    
    // Hiển thị trang đăng nhập
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
} 
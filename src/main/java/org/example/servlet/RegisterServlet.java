package org.example.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.User;
import org.example.util.JPAUtil;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy thông tin từ form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Kiểm tra username đã tồn tại chưa
            try {
                User existingUser = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
                
                if (existingUser != null) {
                    request.setAttribute("error", "Username đã tồn tại!");
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                    return;
                }
            } catch (NoResultException e) {
                // Username chưa tồn tại, tiếp tục đăng ký
            }
            
            // Tạo user mới
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole("USER"); // Mặc định là USER
            newUser.setCreatedAt(LocalDateTime.now());
            
            em.getTransaction().begin();
            em.persist(newUser);
            em.getTransaction().commit();
            
            // Chuyển hướng đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Hiển thị thông báo lỗi trên trang đăng ký
            request.setAttribute("error", "Đã xảy ra lỗi khi đăng ký!");
            // Chuyển hướng đến trang đăng ký để hiển thị lại form và thông báo lỗi
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    // Hiển thị trang đăng ký
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
} 
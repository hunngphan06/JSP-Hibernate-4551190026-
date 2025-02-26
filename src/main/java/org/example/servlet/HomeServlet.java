package org.example.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.Posts;
import org.example.util.JPAUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final int POSTS_PER_PAGE = 5; // Số bài viết trên mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Lấy trang hiện tại từ parameter, mặc định là trang 1
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
            }

            // Tính vị trí bắt đầu
            int offset = (page - 1) * POSTS_PER_PAGE;

            // Lấy tổng số bài viết
            Long totalPosts = em.createQuery("SELECT COUNT(p) FROM Posts p WHERE p.status = 'ACTIVE'", Long.class)
                    .getSingleResult();

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalPosts / POSTS_PER_PAGE);

            // Lấy danh sách bài viết cho trang hiện tại
            List<Posts> posts = em.createQuery(
                    "SELECT p FROM Posts p LEFT JOIN FETCH p.user WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC", 
                    Posts.class)
                    .setFirstResult(offset)
                    .setMaxResults(POSTS_PER_PAGE)
                    .getResultList();

            // Đặt các thuộc tính vào request
            request.setAttribute("posts", posts);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            
            // Forward đến trang JSP
            request.getRequestDispatcher("/home.jsp").forward(request, response);
            
        } finally {
            em.close();
        }
    }
} 
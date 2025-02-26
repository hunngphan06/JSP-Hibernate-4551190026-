package org.example.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.entity.User;
import org.example.util.JPAUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/follows/*")
public class FollowListServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Nếu không có pathInfo hoặc là "/", mặc định chuyển đến /following
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendRedirect(request.getContextPath() + "/follows/following");
                return;
            }

            if ("/following".equals(pathInfo)) {
                // Lấy danh sách người dùng mà currentUser đang follow
                List<User> following = em.createQuery(
                        "SELECT uf.following FROM Follow uf WHERE uf.follower = :currentUser", User.class)
                        .setParameter("currentUser", currentUser)
                        .getResultList();
                
                request.setAttribute("following", following);
                request.setAttribute("listType", "following");
                
            } else if ("/followers".equals(pathInfo)) {
                // Lấy danh sách người dùng đang follow currentUser
                List<User> followers = em.createQuery(
                        "SELECT uf.follower FROM Follow uf WHERE uf.following = :currentUser", User.class)
                        .setParameter("currentUser", currentUser)
                        .getResultList();
                
                request.setAttribute("followers", followers);
                request.setAttribute("listType", "followers");
            }

            request.getRequestDispatcher("/follows.jsp").forward(request, response);
            
        } finally {
            em.close();
        }
    }
} 
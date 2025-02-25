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

@WebServlet("/posts")
public class PostListServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Posts> posts = em.createQuery("SELECT p FROM Posts p ORDER BY p.createdAt DESC", Posts.class)
                    .getResultList();
            
            request.setAttribute("posts", posts);
            request.getRequestDispatcher("/posts.jsp").forward(request, response);
            
        } finally {
            em.close();
        }
    }
} 
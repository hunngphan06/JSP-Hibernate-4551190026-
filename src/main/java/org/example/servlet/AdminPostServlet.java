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
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/admin/posts/*")
public class AdminPostServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // List all posts
            listPosts(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Show edit form
            showEditForm(request, response);
        }
    }
    
    private void listPosts(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Posts> posts = em.createQuery(
                "SELECT p FROM Posts p LEFT JOIN FETCH p.user ORDER BY p.createdAt DESC", 
                Posts.class
            ).getResultList();
            
            request.setAttribute("posts", posts);
            request.getRequestDispatcher("/admin/posts.jsp").forward(request, response);
            
        } finally {
            em.close();
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        Long postId = Long.parseLong(pathParts[2]);
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Posts post = em.find(Posts.class, postId);
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            request.setAttribute("post", post);
            request.getRequestDispatcher("/admin/edit-post.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/edit")) {
            updatePost(request, response);
        }
    }
    
    private void updatePost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Long postId = Long.parseLong(request.getParameter("postId"));
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String status = request.getParameter("status");
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Posts post = em.find(Posts.class, postId);
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            post.setTitle(title);
            post.setBody(body);
            post.setStatus(status);
            post.setUpdatedAt(LocalDateTime.now());
            
            em.getTransaction().commit();
            response.sendRedirect(request.getContextPath() + "/admin/posts");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServletException("Error updating post", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Long postId = Long.parseLong(request.getParameter("postId"));
        System.out.println("postId: " + postId);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Posts post = em.find(Posts.class, postId);
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            post.setStatus("DELETED");
            post.setDeletedAt(LocalDateTime.now());
            
            em.getTransaction().commit();
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServletException("Error deleting post", e);
        } finally {
            em.close();
        }
    }
} 
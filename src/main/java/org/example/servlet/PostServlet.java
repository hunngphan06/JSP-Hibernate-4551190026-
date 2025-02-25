package org.example.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.entity.Posts;
import org.example.entity.User;
import org.example.util.JPAUtil;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/post/*")
public class PostServlet extends HttpServlet {

    private boolean isAuthorizedToModifyPost(HttpServletRequest request, Posts post) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return false;

        User currentUser = (User) session.getAttribute("user");
        String userRole = (String) session.getAttribute("role");

        return currentUser != null &&
                (currentUser.getId().equals(post.getUser().getId()) ||
                        "ADMIN".equals(userRole));
    }

    // Hiển thị form edit
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if ("/edit".equals(pathInfo)) {
            Long postId = Long.parseLong(request.getParameter("postId"));

            EntityManager em = JPAUtil.getEntityManager();
            try {
                Posts post = em.find(Posts.class, postId);

                if (post == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                if (!isAuthorizedToModifyPost(request, post)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                request.setAttribute("post", post);
                request.getRequestDispatcher("/edit.jsp").forward(request, response);

            } finally {
                em.close();
            }
        }
    }

    // Xử lý cập nhật bài viết
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        //Tạo bài viết
        if (pathInfo == null || pathInfo.equals("/")) {
            // Handle post creation
            createPost(request, response);
        } else if ("/edit".equals(pathInfo)) {
            // Handle post editing
            editPost(request, response);
        }
    }

    private void createPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Posts newPost = new Posts();
            newPost.setTitle(title);
            newPost.setBody(body);
            newPost.setUser(currentUser);
            newPost.setStatus("ACTIVE");
            newPost.setCreatedAt(LocalDateTime.now());
            newPost.setUpdatedAt(LocalDateTime.now());

            em.persist(newPost);
            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServletException("Error creating post", e);
        } finally {
            em.close();
        }
    }

    private void editPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long postId = Long.parseLong(request.getParameter("postId"));
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Posts post = em.find(Posts.class, postId);

            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!isAuthorizedToModifyPost(request, post)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            post.setTitle(title);
            post.setBody(body);
            post.setUpdatedAt(LocalDateTime.now());

            em.getTransaction().commit();
            response.sendRedirect(request.getContextPath() + "/");

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new ServletException("Error updating post", e);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long postId = Long.parseLong(pathInfo.substring(1));
            deletePost(postId, request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void deletePost(Long postId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Posts post = em.find(Posts.class, postId);

            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!isAuthorizedToModifyPost(request, post)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Soft delete - chỉ cập nhật trạng thái và thời gian xóa
            post.setStatus("DELETED");
            post.setDeletedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());

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
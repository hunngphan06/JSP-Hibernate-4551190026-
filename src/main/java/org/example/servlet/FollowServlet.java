package org.example.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.entity.Follow;
import org.example.entity.User;
import org.example.util.JPAUtil;

import java.io.IOException;

@WebServlet("/follow/*")
public class FollowServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long followingId = Long.parseLong(pathInfo.substring(1));
            
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                
                // Kiểm tra user muốn follow có tồn tại không
                User followingUser = em.find(User.class, followingId);
                if (followingUser == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // Kiểm tra xem đã follow chưa
                Follow existingFollow = em.createQuery(
                    "SELECT f FROM Follow f WHERE f.follower.id = :followerId AND f.following.id = :followingId",
                    Follow.class)
                    .setParameter("followerId", currentUser.getId())
                    .setParameter("followingId", followingId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);

                if (existingFollow != null) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Already following this user");
                    return;
                }

                // Tạo follow mới
                Follow follow = new Follow();
                follow.setFollower(currentUser);
                follow.setFollowing(followingUser);
                
                em.persist(follow);
                em.getTransaction().commit();
                
                response.setStatus(HttpServletResponse.SC_OK);
                
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ServletException("Error following user", e);
            } finally {
                em.close();
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long followingId = Long.parseLong(pathInfo.substring(1));
            
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                
                // Tìm và xóa follow record
                Follow follow = em.createQuery(
                    "SELECT f FROM Follow f WHERE f.follower.id = :followerId AND f.following.id = :followingId",
                    Follow.class)
                    .setParameter("followerId", currentUser.getId())
                    .setParameter("followingId", followingId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);

                if (follow == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                em.remove(follow);
                em.getTransaction().commit();
                
                response.setStatus(HttpServletResponse.SC_OK);
                
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ServletException("Error unfollowing user", e);
            } finally {
                em.close();
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }
} 
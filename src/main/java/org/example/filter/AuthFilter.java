package org.example.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Ở đây có kiểm soát chuyển hướng, nếu user đã đăng nhập thì không cho phép vào trang login hoặc register
 * Nếu user chưa đăng nhập thì không cho phép vào trang admin hoặc home
 */

@WebFilter("/*") //Dùng để bắt tất cả các request (Tất cả các trang đều phải qua filter này)
public class AuthFilter implements Filter {

    private static final String[] PUBLIC_PAGES = {"/login", "/register", "/login.jsp", "/register.jsp"};
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        //Lấy thông tin từ request
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); //Lấy session hiện tại
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length()); //Lấy path của request
        
        // Kiểm tra nếu user đã đăng nhập
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null); //Kiểm tra xem user có đăng nhập không
        boolean isPublicPage = containsPath(path, PUBLIC_PAGES); //Kiểm tra xem path có phải là trang login, register hoặc resource files không
        boolean isPublicResource = path.endsWith(".css") || path.endsWith(".js"); //Kiểm tra xem path có phải là file css hoặc js không
        boolean isAdminPage = path.startsWith("/admin/"); //Kiểm tra xem path có phải là trang admin không
        
        if (isLoggedIn) {
            // Lấy role của user
            String role = (String) session.getAttribute("role");
            
            // Nếu đã đăng nhập và cố truy cập trang login/register
            if (isPublicPage) {
                // Chuyển hướng về trang chủ tương ứng với role
                if ("ADMIN".equals(role)) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/dashboard");
                } else {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
                }
                return;
            }
            
            // Kiểm tra quyền truy cập trang admin
            if (isAdminPage && !"ADMIN".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            // Cho phép truy cập tất cả các trang khác
            chain.doFilter(request, response);
            return;
        }
        
        // Xử lý khi chưa đăng nhập
        if (isPublicPage || isPublicResource) {
            // Cho phép truy cập trang login, register và resource files
            chain.doFilter(request, response);
        } else {
            // Chuyển hướng về trang login cho các trang khác
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }
    
    // Thêm phương thức hỗ trợ kiểm tra path
    private boolean containsPath(String path, String[] allowedPaths) {
        for (String allowedPath : allowedPaths) {
            if (path.equals(allowedPath)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void destroy() {
    }
} 
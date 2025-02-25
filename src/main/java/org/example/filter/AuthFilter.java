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
        boolean isLoginPage = path.equals("/login") || path.equals("/login.jsp"); //Kiểm tra xem path có phải là trang login không
        boolean isRegisterPage = path.equals("/register") || path.equals("/register.jsp"); //Kiểm tra xem path có phải là trang register không
        boolean isPublicResource = path.endsWith(".css") || path.endsWith(".js"); //Kiểm tra xem path có phải là file css hoặc js không
        boolean isHomePage = path.equals("/") || path.equals("/home.jsp"); //Kiểm tra xem path có phải là trang home không
        
        if (isLoggedIn) {
            // Nếu đã đăng nhập và cố truy cập trang login/register
            if (isLoginPage || isRegisterPage) {
                // Chuyển hướng về trang chủ tương ứng với role
                String role = (String) session.getAttribute("role");
                if ("ADMIN".equals(role)) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/dashboard");
                } else {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
                }
                return;
            }
            
            // Kiểm tra quyền truy cập trang admin
            if (path.startsWith("/admin/") && !"ADMIN".equals(session.getAttribute("role"))) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            // Cho phép truy cập các trang khác
            chain.doFilter(request, response);
            return;
        }
        
        // Xử lý khi chưa đăng nhập
        if (isLoginPage || isRegisterPage || isPublicResource) {
            // Cho phép truy cập trang login, register và resource files
            chain.doFilter(request, response);
        } else if (isHomePage || path.equals("/")) {
            // Chuyển hướng về trang login nếu cố truy cập trang home hoặc trang chủ
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        } else {
            // Chuyển hướng về trang login cho các trang khác
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void destroy() {
    }
} 
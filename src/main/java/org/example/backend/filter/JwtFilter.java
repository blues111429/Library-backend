package org.example.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.example.backend.util.JwtUtil;
import org.example.backend.util.TokenBlacklist;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    public JwtFilter (JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未提供token");
            return;
        }

        String token = authHeader.substring(7);

        if(TokenBlacklist.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token已失效，请重新登录");
            return;
        }

        if(!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token无效");
            return;
        }

        String usernameFromToken = jwtUtil.getUsernameFromToken(token);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(usernameFromToken, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // --- 在这里加调试日志 ---
        System.out.println("请求路径: " + request.getServletPath());
        System.out.println("Authorization header: " + authHeader);
        System.out.println("Token有效性: " + jwtUtil.validateToken(token));
        // ---------------------

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path =  request.getServletPath();
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) ||
                path.equals("/api/user/login") ||
                path.equals("/api/user/register") ||
                path.equals("/api/user/logout") ||
                path.equals("/api/book/bookList") ||
                path.matches("/api/comment/getComments/\\d+");
    }
}

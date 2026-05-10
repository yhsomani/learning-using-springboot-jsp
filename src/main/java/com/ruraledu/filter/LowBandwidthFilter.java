package com.ruraledu.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LowBandwidthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        
        if (session != null && Boolean.TRUE.equals(session.getAttribute("lowBandwidthMode"))) {
            request.setAttribute("textOnlyMode", true);
        }
        
        chain.doFilter(request, response);
    }
}

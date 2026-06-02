package com.rutusoft.flowable.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsSecurityFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String origin = request.getHeader("Origin");
		if (origin != null) {
			response.setHeader("Access-Control-Allow-Origin", origin); // 👈 reflect actual origin
		} else {
			response.setHeader("Access-Control-Allow-Origin", "*");
		}

		response.setHeader("Access-Control-Allow-Credentials", "true"); // 👈 needed for auth headers
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
		response.addHeader("Access-Control-Expose-Headers", "xsrf-token");

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return; // 👈 use return, not else — avoids continuing the chain
		}

		filterChain.doFilter(request, response);
	}
}
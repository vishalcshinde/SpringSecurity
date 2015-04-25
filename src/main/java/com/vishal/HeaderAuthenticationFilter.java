package com.vishal;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.util.StringUtils;

public class HeaderAuthenticationFilter extends AbstractAuthenticationProcessingFilter{
    
    
	protected HeaderAuthenticationFilter() {
		super(AnyRequestMatcher.INSTANCE);
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,	HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
	    if(StringUtils.isEmpty(request.getHeader("IV-USER"))) {
	        throw new AccessDeniedException("");
	    }
		return this.getAuthenticationManager().authenticate(new User(request.getHeader("IV-USER")));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException,
	        ServletException {
	    super.successfulAuthentication(request, response, chain, authResult);
	    chain.doFilter(request, response);
	}
	

}

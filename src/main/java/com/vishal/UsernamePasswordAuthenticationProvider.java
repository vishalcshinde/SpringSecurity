package com.vishal;

import static java.lang.System.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        User user = this.userService.authenticate(authentication.getName());
        if(user == null) {
            throw new AuthenticationServiceException("User Not Found");
        }
        out.println("## " + user.firstName);
        out.println("## " + user.lastName);
        out.println("## " + user.role);
        out.println("## " + user.username);

        SecurityContextHolder.getContext().setAuthentication(user);

        return SecurityContextHolder.getContext().getAuthentication();

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}

package com.vishal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class User implements Authentication {
    /**
     * 
     */
    private static final long serialVersionUID = -8308837691458310057L;
    String username;
    String password;
    String firstName;
    String lastName;
    UserRoleEnum role;
    private Set<GrantedAuthority> authorities;
    boolean authenticated;

    public User(String name) {
        this.username = name;
    }

    public User(String username, String password, String firstName, String lastName, UserRoleEnum role) {
        super();
        this.authorities = new HashSet<GrantedAuthority>(1);
        authorities.add(new SimpleGrantedAuthority(role.name()));
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    @Override
    public String getName() {

        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    @Override
    public Object getCredentials() {

        return this.role.name();
    }

    @Override
    public Object getDetails() {

        return null;
    }

    @Override
    public Object getPrincipal() {

        return this.username;
    }

    @Override
    public boolean isAuthenticated() {

        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        this.authenticated = isAuthenticated;
    }
}

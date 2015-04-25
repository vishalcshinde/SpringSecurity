package com.vishal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public String getData() {
        return "data Only visible to Admin";
    }
}

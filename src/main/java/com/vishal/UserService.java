package com.vishal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User authenticate(String UserId) {
        return this.userRepository.findOne(UserId);
    }
}

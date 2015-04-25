package com.vishal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class UserMemoryRepository implements UserRepository {
    Map<String, User> UserMemoryRepository = new HashMap<>();

    public UserMemoryRepository() {
        UserMemoryRepository.put("james", new User("james", "password", "James", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("jane", new User("jane", "password", "Jane", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("jared", new User("jared", "password", "Jared", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("jason", new User("jason", "password", "Jason", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("johann", new User("johann", "password", "Johann", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("john", new User("john", "password", "John", "Doe", UserRoleEnum.ADMIN));
        UserMemoryRepository.put("joanna", new User("joanna", "password", "Joanna", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("jonathan", new User("jonathan", "password", "Jonathan", "Doe", UserRoleEnum.ADMIN));
        UserMemoryRepository.put("joseph", new User("joseph", "password", "Joseph", "Doe", UserRoleEnum.ADMIN));
        UserMemoryRepository.put("judith", new User("judith", "password", "Judith", "Doe", UserRoleEnum.NORMAL));
        UserMemoryRepository.put("julia", new User("julia", "password", "Julia", "Doe", UserRoleEnum.ADMIN));
    }

    @Override
    public Collection<User> findAll() {
        return UserMemoryRepository.values();
    }

    @Override
    public User findOne(String userName) {
        return UserMemoryRepository.get(userName);
    }

}

package com.vishal;

import java.util.Collection;

public interface UserRepository {
	Collection<User> findAll();
	User findOne(String userName);
}

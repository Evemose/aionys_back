package org.aionys.main.security.users;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByUsername(String username);
    void deleteById(Long id);
}

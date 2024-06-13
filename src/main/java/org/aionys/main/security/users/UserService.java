package org.aionys.main.security.users;

import java.util.Optional;

public interface UserService {
    User save(User user);

    // The requirement for this method arises from the fact that
    // password needs to be hashed ONLY when creating a new user
    // or updating an existing user's password.
    // As there is no deterministic way to check if it is already hashed (user may
    // input a password that "looks like" a hash, but is a password
    // itself), we need to provide a method to update the user
    // without hashing the password.
    User update(User note);

    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);
}

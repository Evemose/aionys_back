package org.aionys.notes.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.aionys.notes.persistence.repos.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @SuppressWarnings("all") // suppress lombok plugin warning
    @Delegate(types = UserService.class)
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

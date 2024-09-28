package com.joaogoncalves.feedback.service;

import com.joaogoncalves.feedback.entity.User;
import com.joaogoncalves.feedback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("User not found! [username: %s]", username))
                );
        log.debug("User found successfully [username: {}]", user.getUsername());
        return user;
    }
}

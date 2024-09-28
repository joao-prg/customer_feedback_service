package com.joaogoncalves.feedback.service;

import com.joaogoncalves.feedback.entity.User;
import com.joaogoncalves.feedback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("User not found! [e-mail: %s]", username))
                );
    }

    public User findById(final String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user id not found"));
    }
}

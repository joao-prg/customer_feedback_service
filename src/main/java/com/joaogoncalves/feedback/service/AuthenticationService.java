package com.joaogoncalves.feedback.service;

import com.joaogoncalves.feedback.entity.RefreshToken;
import com.joaogoncalves.feedback.entity.User;
import com.joaogoncalves.feedback.exception.UserAlreadyExistsException;
import com.joaogoncalves.feedback.model.TokenLogout;
import com.joaogoncalves.feedback.model.TokenRead;
import com.joaogoncalves.feedback.model.TokenRefresh;
import com.joaogoncalves.feedback.model.UserCreate;
import com.joaogoncalves.feedback.model.UserLogin;
import com.joaogoncalves.feedback.repository.RefreshTokenRepository;
import com.joaogoncalves.feedback.repository.UserRepository;
import com.joaogoncalves.feedback.security.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TokenRead login(final UserLogin userLogin) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final User user = (User) authentication.getPrincipal();
        final RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);
        final String accessToken = jwtHelper.generateAccessToken(user);
        final String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);
        return new TokenRead(user.getId(), accessToken, refreshTokenString);
    }

    private User createUser(final UserCreate userCreate) {
        if (userRepository.existsByUsername(userCreate.getUsername())) {
            throw new UserAlreadyExistsException(
                    String.format("User already exists! [username: %s]", userCreate.getUsername())
            );
        }
        User userToCreate = modelMapper.map(userCreate, User.class);
        userToCreate.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        final User savedUser = userRepository.save(userToCreate);
        log.debug("User saved successfully [username: {}]", savedUser.getUsername());
        return savedUser;
    }

    public TokenRead signup(final UserCreate userCreate) {
        final User user = createUser(userCreate);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);
        final String accessToken = jwtHelper.generateAccessToken(user);
        final String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);
        return new TokenRead(user.getId(), accessToken, refreshTokenString);
    }

    public void logout(final TokenLogout tokenLogout) {
        final String refreshTokenString = tokenLogout.getRefreshToken();
        if (jwtHelper.validateRefreshToken(refreshTokenString)
                && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
            refreshTokenRepository.deleteById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString));
            return;
        }
        throw new BadCredentialsException("Invalid token");
    }

    private User getUser(final String refreshTokenString) {
        final String userId = jwtHelper.getUserIdFromRefreshToken(refreshTokenString);
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found! [Id: %s]", userId)
                ));
    }

    public TokenRead refreshToken(final TokenRefresh tokenRefresh) {
        final String refreshTokenString = tokenRefresh.getRefreshToken();
        if (jwtHelper.validateRefreshToken(refreshTokenString)
                && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
            refreshTokenRepository.deleteById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString));
            final User user = getUser(refreshTokenString);
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshTokenRepository.save(refreshToken);
            final String accessToken = jwtHelper.generateAccessToken(user);
            final String newRefreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);
            return new TokenRead(user.getId(), accessToken, newRefreshTokenString);
        }
        throw new BadCredentialsException("Invalid token");
    }
}

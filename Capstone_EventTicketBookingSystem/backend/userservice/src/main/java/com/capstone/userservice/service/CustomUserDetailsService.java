package com.capstone.userservice.service;

import com.capstone.userservice.entity.User;
import com.capstone.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

@Service 
public class CustomUserDetailsService implements UserDetailsService
{
    @Autowired 
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override 
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        log.info("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> 
                                                        {
                                                            log.warn("User not found with email: {}", email);
                                                            return new UsernameNotFoundException("User not found with email: " + email);
                                                        }
                                                    );

        log.info("User found - ID: {}, Email: {}, Role: {}", user.getId(), user.getEmail(), user.getRole().name());

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                                                                    user.getEmail(),
                                                                    user.getPassword(),
                                                                    Collections.singletonList(authority)
                                                                     );
    }
}
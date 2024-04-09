package com.eazy.pay.service;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUser(){return userRepository.findById("fisafisa").orElseThrow(()-> new IllegalArgumentException(String.valueOf("fosafisa")));}
    @Override
    public User loadUserByUsername(String id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException(id));
    }
}

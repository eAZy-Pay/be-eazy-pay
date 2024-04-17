package com.eazy.pay.service;

import com.eazy.pay.dao.UserRepository;
import com.eazy.pay.dto.SignInDTO;
import com.eazy.pay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null; // UserDetailService 구현
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByStrId(String strId) {
        return userRepository.findByStrId(strId)
                .orElse(null);
    }

    public User findByNameAndPhoneNumber(String name, String phoneNumber) {
        return userRepository.findByNameAndPhoneNumber(name, phoneNumber)
                .orElse(null);
    }





}

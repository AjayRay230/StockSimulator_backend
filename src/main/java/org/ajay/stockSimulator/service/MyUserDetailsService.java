package org.ajay.stockSimulator.service;


import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ajay.stockSimulator.model.UserPrinciple;

import java.util.Collections;


@Service
public class MyUserDetailsService implements UserDetailsService {
  @Autowired
    UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        System.out.println("Trying to log in user: " + username);
        User user = userRepo.findByUsername(username);
        if(user==null)
        {
            System.out.println("user not found");
            throw new UsernameNotFoundException("user not found");

        }

           return new org.springframework.security.core.userdetails.User(
                   user.getUsername(),
                   user.getPassword(),
                   Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
           );

    }
}

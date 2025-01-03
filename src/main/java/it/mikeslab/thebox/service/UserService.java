package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.exception.EmailAlreadyUsedException;
import it.mikeslab.thebox.exception.UsernameAlreadyUsedException;
import it.mikeslab.thebox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //User user = this.getUserByEmail(email);

        //Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        return this.getUserByEmail(email);
    }

    public User getUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found for '"+ email + "'.");
        }
        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void checkIfUserAlreadyExists(String username, String email) throws RuntimeException {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email already in use.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyUsedException("Username already in use.");
        }
    }

    public boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    public void checkAndSave(User user) throws RuntimeException {

        checkIfUserAlreadyExists(user.getUsername(), user.getEmail());

        userRepository.save(user);

    }

}

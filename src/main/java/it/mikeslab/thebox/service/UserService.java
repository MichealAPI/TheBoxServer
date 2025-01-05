package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.exception.EmailAlreadyUsedException;
import it.mikeslab.thebox.exception.UsernameAlreadyUsedException;
import it.mikeslab.thebox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

        User user = this.getUserByEmail(email);

        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException("No user found for '"+ email + "'.");
        }

        //Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        return user;
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

    public void addVerification(User user, String verificationId) {
        user.setVerificationToken(verificationId);
        userRepository.save(user);
    }

    public boolean verifyUser(String username, String verificationId) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return false;
        }

        if (!user.getVerificationToken().equals(verificationId)) {
            return false;
        }

        user.setVerificationToken("none");

        userRepository.save(user);
        return true;
    }

}

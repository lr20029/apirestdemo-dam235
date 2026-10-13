package edu.sv.ues.dam235.apirestdemo.implementations;

import edu.sv.ues.dam235.apirestdemo.dtos.UserDTO;
import edu.sv.ues.dam235.apirestdemo.entities.User;
import edu.sv.ues.dam235.apirestdemo.repositories.UserRepository;
import edu.sv.ues.dam235.apirestdemo.services.UserServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(UserDTO request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return null;
        }
        User user = new User();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        User saved = userRepository.save(user);
        return new UserDTO(saved.getId(), saved.getName(), saved.getLastName(),
                saved.getEmail(), null, saved.getActive());
    }
}

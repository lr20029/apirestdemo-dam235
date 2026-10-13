package edu.sv.ues.dam235.apirestdemo.controllers;

import edu.sv.ues.dam235.apirestdemo.dtos.UserDTO;
import edu.sv.ues.dam235.apirestdemo.services.UserServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    final private UserServices userServices;

    private UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO request) {
        try {
            UserDTO created = userServices.createUser(request);
            if (created == null) {
                return ResponseEntity.status(409).build();
            }
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }
}

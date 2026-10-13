package edu.sv.ues.dam235.apirestdemo.controllers;

import edu.sv.ues.dam235.apirestdemo.dtos.LoginDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import edu.sv.ues.dam235.apirestdemo.services.AuthServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    final private AuthServices authServices;

    private AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO authRequest) {
        try {
            TokenDTO token = authServices.login(authRequest.getUser(), authRequest.getPass());
            if (token == null) {
                return ResponseEntity.status(401).build();
            } else {
                return ResponseEntity.ok(token);
            }
        } catch (Exception e) {
            log.error("{}", e);
        }
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            String token = authorizationHeader;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }
            authServices.logout(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("{}", e);
        }
        return ResponseEntity.status(500).build();
    }
}

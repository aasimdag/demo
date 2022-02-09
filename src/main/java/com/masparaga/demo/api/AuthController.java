package com.masparaga.demo.api;


import com.masparaga.demo.model.ERole;
import com.masparaga.demo.model.Role;
import com.masparaga.demo.model.User;
import com.masparaga.demo.payloads.requests.LoginRequest;
import com.masparaga.demo.payloads.requests.SignupRequest;
import com.masparaga.demo.payloads.responses.MessageResponse;
import com.masparaga.demo.payloads.responses.UserInfoResponse;
import com.masparaga.demo.repository.RoleRepository;
import com.masparaga.demo.repository.UserRepository;
import com.masparaga.demo.security.jwt.JwtUtils;
import com.masparaga.demo.services.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.usernameExists(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("username exists!"));
        }
        if(userRepository.emailExists(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("email exists!"));
        }
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));
        Set<String> stringRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if(stringRoles==null){
            Role userRole = roleRepository.findByName(ERole.ROLE_VISITOR)
                    .orElseThrow(() -> new RuntimeException("role not found!"));
            roles.add(userRole);
        } else{
            for(String role:stringRoles){
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("role not found!"));
                        roles.add(adminRole);
                        break;
                    case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("role not found!"));
                        roles.add(userRole);
                        break;
                    default:
                        Role visitorRole = roleRepository.findByName(ERole.ROLE_VISITOR)
                                .orElseThrow(() -> new RuntimeException("role not found!"));
                        roles.add(visitorRole);
                        break;
                }
            }
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User successfully registered."));
    }

}

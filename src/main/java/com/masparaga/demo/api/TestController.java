package com.masparaga.demo.api;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess(){
        return "public";
    }
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('VISITOR') or hasRole('ADMIN')")
    public String userAccess(){
        return "all users";
    }
    @GetMapping("/visitor")
    @PreAuthorize("hasRole('VISITOR')")
    public String visitorAccess(){
        return "visitor";
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(){
        return "admin";
    }
}

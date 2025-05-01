package com.collegeelection.controller;
import com.collegeelection.model.User;
import com.collegeelection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepo;
    

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("VOTER");
        user.setEnabled(true);
        userRepo.save(user);
        return "redirect:/login";
    }

	
    @GetMapping("/dashboard")
    public String redirectToDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?error=unauthenticated";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/admin_dashboard";
            } else if (role.equals("ROLE_VOTER")) {
                return "redirect:/voter/voter_dashboard";
            }
        }

        return "redirect:/login?error=unauthorized";
    }
	 
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}

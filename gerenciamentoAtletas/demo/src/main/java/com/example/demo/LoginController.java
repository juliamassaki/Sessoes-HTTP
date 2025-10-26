package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute(Sessao.USER_SESSION_KEY) != null) {
            return "redirect:/atletas";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (Sessao.USERNAME.equals(username) && Sessao.PASSWORD.equals(password)) {
            session.setAttribute(Sessao.USER_SESSION_KEY, username);
            return "redirect:/atletas"; // Redireciona para a lista de atletas
        } else {
            model.addAttribute("erro", "Usuário ou senha inválidos.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

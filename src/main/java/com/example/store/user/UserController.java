package com.example.store.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final HttpSession session;

    //로그인
    @PostMapping("/login")
    public String login() {
        return "redirect:/";
    }


    @GetMapping("/user/login-form")
    public String loginForm() {
        return "user/login-form";
    }

    //회원가입
    @PostMapping("/join")
    public String userJoin(UserRequest.JoinDTO reqDTO) {
        userService.join(reqDTO);
        return "redirect:/login-form";
    }

    @GetMapping("/user/join-form")
    public String userJoinForm() {
        return "user/join-form";
    }


    // 회원정보수정
    @PostMapping("/update")
    public String userUpdate() {
        return "redirect:/login-form";
    }

    @GetMapping("/user/update-form")
    public String userUpdateForm() {
        return "user/update-form";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}

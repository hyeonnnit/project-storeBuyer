package com.example.store.order;

import com.example.store.user.User;
import com.example.store.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class OrderController {
    private final UserService userService;
    private final HttpSession session;

    @GetMapping("/orders")
    public String orderList() {
        return "order/product-list";
    }

    @GetMapping("/order/{id}/product-form")
    public String orderForm(){
        return "order/product-order-form";
    }

    @PostMapping("/order/{id}/product")
    public String order(){
        return "redirect:/orders";
    }
}

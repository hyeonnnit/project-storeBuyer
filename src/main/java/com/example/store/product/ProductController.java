package com.example.store.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@Controller
public class ProductController {
    private final ProductService productService;


    @GetMapping("/")
    public String list() {
        return "product/list";
    }

    @GetMapping("/product/{id}")
    public String detail() {
        return "product/detail";
    }

}

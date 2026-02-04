package com.moeware.ims.controller.product;

import com.moeware.ims.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorys")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;
}

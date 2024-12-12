package com.mysite.crud2.controllers;

import com.mysite.crud2.models.Product;
import com.mysite.crud2.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository pRepo;

    @GetMapping( "")
    public String showProductList(Model model) {
        List<Product> products = pRepo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }


}

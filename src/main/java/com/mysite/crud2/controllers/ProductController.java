package com.mysite.crud2.controllers;

import com.mysite.crud2.models.Product;
import com.mysite.crud2.ProductRepository;
import com.mysite.crud2.models.ProductDTO;

import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDTO productDto = new ProductDTO();
        model.addAttribute("productDto", productDto);
        return "products/createProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDTO productDto,
                                BindingResult result) {
        if(productDto.getImageFile().isEmpty()) {
            //user가 이미지를 입력하지 않고 전송시 에러
            result.addError(new FieldError("productDto", "imageFile", "Image file is required"));
        }
        if(result.hasErrors()) {
            return "products/createProduct";
        }
        //성공시 DB 저장
        MultipartFile image = productDto.getImageFile();
        Date createDate = new Date();
        String storeFileName = createDate.getTime()+"_"+image.getOriginalFilename();
        //이미지를 public/images 폴더에 저장
        try {
            String uploadDir = "public/images/";//저장주소문자열
            Path uploadPath = Paths.get(uploadDir);//업로드주소 객체
            if(!Files.exists(uploadPath)) {
                Files.createDirectory(uploadPath);
            }
            try(InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageFileName(storeFileName);


        pRepo.save(product);//이미지 파일은 public 폴더에 저장되고 db에는 지정파일 이름을 저장

        return "redirect:/products";


    }

    //제품 수정 페이지
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {

        try {
            Product product = pRepo.findById(id).get();
            model.addAttribute("product", product);

            ProductDTO productDto = new ProductDTO();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return "products/editProduct";

    }
    //수정하기
 @PostMapping("/edit")
    public String editProduct(@Valid @ModelAttribute("productDto") ProductDTO productDto, BindingResult result,@RequestParam int id, Model model) {
        Product product = pRepo.findById(id).get();
        model.addAttribute("product", product);
        //이미지가 없어도 에러 발생 안함
        if(result.hasErrors()) {
            return "products/editProduct";
        }
     //수정 이미지 있다면 기존 이미지 삭제 후 수정 이미지 업로드

        if(!productDto.getImageFile().isEmpty()) {
            String uploadDir = "public/images/"; //업로드 폴더 주소
            Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

            try {
                Files.delete(oldImagePath);
            }catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            //새 이미지 업로드
            MultipartFile image = productDto.getImageFile();
            Date createDate = new Date();
            String storeFileName = createDate.getTime()+"_"+image.getOriginalFilename();

            try(InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);
            }catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            product.setImageFileName(storeFileName);//이미지 파일 이름을 업데이트

        }
        //이미지 제외한 수정 내용을 업데이트
     product.setName(productDto.getName());
     product.setBrand(productDto.getBrand());
     product.setCategory(productDto.getCategory());
     product.setPrice(productDto.getPrice());
     product.setDescription(productDto.getDescription());

     pRepo.save(product);
     System.out.println(product);
     return "redirect:/products";


    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            Product product = pRepo.findById(id).get();

            //이미지 파일 삭제하기
            String uploadDir = "public/images/";
            Path imagePath = Paths.get(uploadDir + product.getImageFileName());

            try {
                Files.delete(imagePath);
            }catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            //제품 삭제
            pRepo.delete(product);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

}

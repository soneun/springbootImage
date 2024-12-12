package com.mysite.crud2.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;



@Getter
@Setter
//유저에게 제품 입력을 받는 객체
public class ProductDTO {

    //id는 자동생성되므로 필요없음
    @NotEmpty(message = "이름을 입력하세요")
    private String name;
    
    @NotEmpty(message = "브랜드를 입력하세요")
    private String brand;
    
    @NotEmpty(message = "카테코리를 입력하세요")
    private String category;

    @Min(value = 0, message = "가격을 입력하세요")
    private int price;

    @Size(min = 10, message = "제품설명은 10자 이상")
    @Size(max = 100, message = "제품설명은 100자 이하")
    private String description;

    //날짜는 현재날짜시간으로 자동입력됨
    //DB에는 파일의 이름만 저장되지만 실제 유저로부터 파일이미지를 받음
    private MultipartFile imageFile;

    

}

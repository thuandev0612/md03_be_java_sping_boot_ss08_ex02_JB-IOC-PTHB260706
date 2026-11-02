package com.example.baitapsession08.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class BookCreateDTO {
    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không được quá 255 ký tự")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tác giả không được quá 255 ký tự")
    private String author;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn không được âm")
    private Integer stock;

    @NotNull(message = "Vui lòng chọn ảnh bìa")
    private MultipartFile coverImage;
}

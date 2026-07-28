package com.example.bookstore.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookResponseDto {

    private Long id;


    private String title;


    private String author;


    private String isbn;


    private BigDecimal price;


    private Integer stock;


    private LocalDateTime createdAt;

}

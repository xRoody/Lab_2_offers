package com.example.my.DTOs;

import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OfferDTO {
    private Long id;
    private String title;
    private Double price;
    private List<CharacteristicDTO> characteristics=new ArrayList<>();
    private Long categoryId;
    private Long payMethod;
}

package com.example.my.DTOs;

import lombok.*;

import java.util.ArrayList;
import java.util.Collection;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryDTO {
    private Long id;
    private String title;
    private Collection<OfferDTO> offers=new ArrayList<>();
}

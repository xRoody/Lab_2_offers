package com.example.my.DTOs;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CharacteristicDTO {
    private Long id;
    private String title;
    private String value;
    private Long offerId;
}

package com.example.my.services;

import com.example.my.DTOs.CharacteristicDTO;
import com.example.my.entities.Characteristic;
import com.example.my.entities.Offer;

import java.util.List;

public interface CharacteristicService {
    CharacteristicDTO getDTObyObj(Characteristic characteristic);
    Characteristic getObjByDTO(CharacteristicDTO characteristicDTO);
    Characteristic getObjByDTO(CharacteristicDTO characteristicDTO, Offer offer);
    void update(CharacteristicDTO characteristicDTO);
    boolean delete(Long id);
    void addCharacteristic(CharacteristicDTO characteristicDTO);
    List<CharacteristicDTO> getAllCharacteristicsByOrderId(Long id);
    CharacteristicDTO getById(Long id);
    boolean isExists(Long id);
}

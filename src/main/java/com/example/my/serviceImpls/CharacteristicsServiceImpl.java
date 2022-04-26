package com.example.my.serviceImpls;

import com.example.my.DTOs.CharacteristicDTO;
import com.example.my.entities.Characteristic;
import com.example.my.entities.Offer;
import com.example.my.repositories.CharacteristicRepo;
import com.example.my.repositories.OfferRepo;
import com.example.my.services.CharacteristicService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CharacteristicsServiceImpl implements CharacteristicService {
    private OfferRepo offerRepo;
    private CharacteristicRepo characteristicRepo;

    @Inject
    public CharacteristicsServiceImpl(OfferRepo offerRepo, CharacteristicRepo characteristicRepo) {
        this.offerRepo = offerRepo;
        this.characteristicRepo=characteristicRepo;
    }

    public CharacteristicDTO getDTObyObj(Characteristic characteristic){
        return CharacteristicDTO.builder()
                .id(characteristic.getId())
                .title(characteristic.getTitle())
                .value(characteristic.getValue())
                .offerId(characteristic.getOffer().getId())
                .build();
    }

    public Characteristic getObjByDTO(CharacteristicDTO characteristicDTO){
        return Characteristic.builder()
                .id(characteristicDTO.getId())
                .title(characteristicDTO.getTitle())
                .value(characteristicDTO.getValue())
                .offer(offerRepo.findById(characteristicDTO.getOfferId()))
                .build();
    }

    public Characteristic getObjByDTO(CharacteristicDTO characteristicDTO, Offer offer){
        return Characteristic.builder()
                .id(characteristicDTO.getId())
                .title(characteristicDTO.getTitle())
                .value(characteristicDTO.getValue())
                .offer(offer)
                .build();
    }

    public CharacteristicDTO getById(Long id){
        Characteristic characteristic=characteristicRepo.findByIdOptional(id).orElse(null);
        if (characteristic==null) return null;
        return getDTObyObj(characteristic);
    }

    public List<CharacteristicDTO> getAllCharacteristicsByOrderId(Long id){
        Offer offer=offerRepo.findByIdOptional(id).orElse(null);
        if (offer==null) return null;
        return characteristicRepo.find("offer", offer).stream().map(x->getDTObyObj(x)).collect(Collectors.toList());
    }

    public void addCharacteristic(CharacteristicDTO characteristicDTO){
        Characteristic characteristic=Characteristic.builder()
                .title(characteristicDTO.getTitle())
                .value(characteristicDTO.getValue())
                .offer(offerRepo.findById(characteristicDTO.getOfferId()))
                .build();
        characteristicRepo.persist(characteristic);
        log.info("characteristic {} has been added", characteristicDTO);
    }

    public boolean delete(Long id){
        boolean f=characteristicRepo.deleteById(id);
        log.info("characteristic id={} has been deleted", id);
        return f;
    }

    @Override
    public void update(CharacteristicDTO characteristicDTO) {
        Characteristic characteristic=characteristicRepo.findById(characteristicDTO.getId());
        characteristic.setTitle(characteristicDTO.getTitle());
        characteristic.setValue(characteristicDTO.getValue());
        characteristicRepo.persist(characteristic);
        log.info("characteristic {} has been updated", characteristicDTO);
    }

    public boolean isExists(Long id){
        return characteristicRepo.findByIdOptional(id).isPresent();
    }
}

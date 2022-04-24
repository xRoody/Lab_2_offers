package com.example.my.serviceImpls;

import com.example.my.DTOs.CharacteristicDTO;
import com.example.my.DTOs.OfferDTO;
import com.example.my.entities.Category;
import com.example.my.entities.Offer;
import com.example.my.repositories.CategoryRepo;
import com.example.my.repositories.OfferRepo;
import com.example.my.services.CharacteristicService;
import com.example.my.services.OfferService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class OfferServiceImpl implements OfferService {
    private OfferRepo offerRepo;
    private CategoryRepo categoryRepo;
    private CharacteristicService characteristicService;

    @Inject
    public OfferServiceImpl(OfferRepo offerRepo, CharacteristicService characteristicService, CategoryRepo categoryRepo) {
        this.offerRepo = offerRepo;
        this.characteristicService=characteristicService;
        this.categoryRepo=categoryRepo;
    }

    public OfferDTO getDTOByObj(Offer offer){
        return OfferDTO.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .price(offer.getPrice())
                .categoryId(offer.getCategory().getId())
                .payMethod(offer.getPay_id())
                .characteristics(offer.getCharacteristics().stream().map(x->characteristicService.getDTObyObj(x)).collect(Collectors.toList()))
                .build();
    }

    public OfferDTO getDTOById(Long id){
        Offer offer=offerRepo.findById(id);
        if (offer==null) return null;
        return getDTOByObj(offer);
    }

    public List<OfferDTO> getAllOffers(){
       return offerRepo.findAll().stream().map(x->getDTOByObj(x)).collect(Collectors.toList());
    }

    public boolean deleteOffer(Long id){
        Offer offer=offerRepo.findById(id);
        if (offer!=null) {
            Category category=offer.getCategory();
            offerRepo.deleteById(id);
            if (offerRepo.count("category", category) == 0) {
                categoryRepo.deleteById(category.getId());
            }
            return true;
        }
        return false;
    }

    public void createOffer(OfferDTO offerDTO){
        Offer offer= Offer.builder()
                .title(offerDTO.getTitle())
                .price(offerDTO.getPrice())
                .category(categoryRepo.findById(offerDTO.getCategoryId()))
                .pay_id(offerDTO.getPayMethod())
                .characteristics(new HashSet<>())
                .build();
        offerRepo.persist(offer);
        offer.getCharacteristics().addAll(offerDTO.getCharacteristics().stream().map(x->{
            x.setOfferId(offer.getId());
            return characteristicService.getObjByDTO(x);
        }).collect(Collectors.toList()));
    }

    public void updateOffer(OfferDTO offerDTO){
        if (offerDTO.getId()==null) throw new IllegalStateException("id must be not null");
        Offer offer=offerRepo.findById(offerDTO.getId());
        offer.setPrice(offerDTO.getPrice());
        offer.setTitle(offerDTO.getTitle());
        offer.setPay_id(offerDTO.getPayMethod());
        offer.setCategory(categoryRepo.findById(offerDTO.getCategoryId()));
        for (CharacteristicDTO characteristicDTO:offerDTO.getCharacteristics()){
            if (characteristicDTO.getId()!=null){
                characteristicService.update(characteristicDTO);
            }else {
                characteristicDTO.setOfferId(offer.getId());
                offer.getCharacteristics().add(characteristicService.getObjByDTO(characteristicDTO));
            }
        }
        offerRepo.persist(offer);
    }

    public boolean isExists(Long id){
        return offerRepo.findByIdOptional(id).isPresent();
    }

    public List<OfferDTO> findByPayMethod(Long pay){
        return offerRepo.find("pay_id", pay).stream().map(x->getDTOByObj(x)).collect(Collectors.toList());
    }
}

package com.example.my.services;

import com.example.my.DTOs.OfferDTO;
import com.example.my.entities.Offer;

import java.util.List;

public interface OfferService {
    OfferDTO getDTOById(Long id);
    List<OfferDTO> getAllOffers();
    boolean deleteOffer(Long id);
    void createOffer(OfferDTO offerDTO);
    void updateOffer(OfferDTO offerDTO);
    OfferDTO getDTOByObj(Offer offer);
    boolean isExists(Long id);
    List<OfferDTO> findByPayMethod(Long pay);
}

package com.example.my.repositories;

import com.example.my.entities.Offer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OfferRepo implements PanacheRepository<Offer> {

}

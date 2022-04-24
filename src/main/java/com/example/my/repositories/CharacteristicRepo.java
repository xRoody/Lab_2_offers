package com.example.my.repositories;

import com.example.my.entities.Characteristic;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CharacteristicRepo implements PanacheRepository<Characteristic> {

}

package com.example.my.validators;

import com.example.my.DTOs.CharacteristicDTO;
import com.example.my.exceptions.BodyExceptionWrapper;
import com.example.my.services.CharacteristicService;
import com.example.my.services.OfferService;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor
public class CharValidator {
    private final OfferService offerService;
    private final CharacteristicService characteristicService;
    private final static Pattern titlePattern=Pattern.compile("[A-Z][A-Za-z]*");

    public List<BodyExceptionWrapper> validate(CharacteristicDTO characteristicDTO){
        List<BodyExceptionWrapper> reports=new ArrayList<>();
        validateTitle(characteristicDTO.getTitle(), reports);
        validateValue(characteristicDTO.getValue(), reports);
        validateOffer(characteristicDTO.getOfferId(), reports);
        return reports;
    }

    private void validateTitle(String title, List<BodyExceptionWrapper> reports){
        if (title==null || title.isBlank()) reports.add(new BodyExceptionWrapper("e-001", "Title must be not empty"));
        else if (!titlePattern.matcher(title).matches()) reports.add(new BodyExceptionWrapper("e-002", "Incorrect title format"));
    }

    private void validateValue(String value, List<BodyExceptionWrapper> reports){
        if(value==null||value.isBlank()) reports.add(new BodyExceptionWrapper("e-001", "Value must be not empty"));
    }

    private void validateOffer(Long offerId, List<BodyExceptionWrapper> reports){
        if (!offerService.isExists(offerId)) reports.add(new BodyExceptionWrapper("e-003", "Offer with id="+offerId+" is not exists"));
    }

    public List<BodyExceptionWrapper> validateUpdate(CharacteristicDTO characteristicDTO){
        List<BodyExceptionWrapper> reports=new ArrayList<>();
        if (!characteristicService.isExists(characteristicDTO.getId())) reports.add(new BodyExceptionWrapper("e-003", "Characteristic with id="+characteristicDTO.getId()+" is not exists"));
        else reports.addAll(validate(characteristicDTO));
        return reports;
    }
}

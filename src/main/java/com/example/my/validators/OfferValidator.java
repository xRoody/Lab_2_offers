package com.example.my.validators;

import com.example.my.DTOs.OfferDTO;
import com.example.my.exceptions.BodyExceptionWrapper;
import com.example.my.services.CategoryService;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@ApplicationScoped
public class OfferValidator {
    private static final Pattern pattern = Pattern.compile("[A-Za-z]*");
    private CategoryService categoryService;
    private final ResteasyClient resteasyClient = new ResteasyClientBuilderImpl().build();
    @Inject
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<BodyExceptionWrapper> validateNewOffer(OfferDTO offerDTO) {
        List<BodyExceptionWrapper> reports = new ArrayList<>();
        if (offerDTO.getPrice() <= 0) reports.add(new BodyExceptionWrapper("e-002", "Price must be higher than 0"));
        if (offerDTO.getTitle() == null || offerDTO.getTitle().isBlank())
            reports.add(new BodyExceptionWrapper("e-001", "Title must be not empty"));
        else if (!pattern.matcher(offerDTO.getTitle()).matches())
            reports.add(new BodyExceptionWrapper("e-002", "Incorrect format of title"));
        if (offerDTO.getCategoryId() == null)
            reports.add(new BodyExceptionWrapper("e-001", "Category must be not null"));
        else if (!categoryService.isExists(offerDTO.getCategoryId()))
            reports.add(new BodyExceptionWrapper("e-003", "This category is not exists"));
        Response response = resteasyClient
                .target("http://localhost:8081")
                .path("/payMethods/{id}/isExists")
                .resolveTemplate("id", offerDTO.getPayMethod())
                .request()
                .get();
        if (response.getStatus() == 404) reports.add(new BodyExceptionWrapper("e-003", "This pay method is not exists"));
        response.close();
        return reports;
    }

    public List<BodyExceptionWrapper> validateExistsOffer(OfferDTO offerDTO) {
        List<BodyExceptionWrapper> reports = new ArrayList<>(validateNewOffer(offerDTO));
        for (int i = 0; i < offerDTO.getCharacteristics().size(); i++) {
            if (offerDTO.getCharacteristics().get(i).getId() != null && !Objects.equals(offerDTO.getCharacteristics().get(i).getOfferId(), offerDTO.getId()))
                reports.add(new BodyExceptionWrapper("e-002", "Incorrect ratio of ids (characteristic[" + i + "] is exists, but for other offer)"));
        }
        return reports;
    }
}

package com.example.my.controllers;

import com.example.my.DTOs.CatPriceWrapper;
import com.example.my.DTOs.OfferDTO;
import com.example.my.exceptions.BodyExceptionWrapper;
import com.example.my.serviceImpls.OfferServiceImpl;
import com.example.my.services.CategoryService;
import com.example.my.services.CharacteristicService;
import com.example.my.validators.OfferValidator;


import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.reactive.RestResponse;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Path("/offers")
public class OffersController {
    private OfferServiceImpl offerService;
    private OfferValidator offerValidator;
    private CharacteristicService characteristicService;
    private CategoryService categoryService;
    private final ResteasyClient resteasyClient = new ResteasyClientBuilderImpl().build();

    @Inject
    public void setOfferService(OfferServiceImpl offerService) {
        this.offerService = offerService;
    }

    @Inject
    public void setOfferValidator(OfferValidator offerValidator) {
        this.offerValidator = offerValidator;
    }

    @Inject
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Inject
    public void setCharacteristicService(CharacteristicService characteristicService) {
        this.characteristicService = characteristicService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<OfferDTO> getAll() {
        return offerService.getAllOffers();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getById(@PathParam("id") Long id) {
        OfferDTO offerDTO = offerService.getDTOById(id);
        if (offerDTO == null) {
            log.info("No offers with id={} found", id);
            return RestResponse.notFound();
        }
        return RestResponse.ok(offerDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> addNewOffer(OfferDTO offerDTO) {
        List<BodyExceptionWrapper> reports = offerValidator.validateNewOffer(offerDTO);
        if (reports.size() != 0) {
            log.info(reports.toString());
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, (Object) reports).build();
        }
        offerService.createOffer(offerDTO);
        return RestResponse.created(URI.create("/offers"));
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> delete(@PathParam("id") Long id) {
        Response response = resteasyClient
                .target("http://localhost:8082")
                .path("/card/countByOfferId/{id}")
                .resolveTemplate("id", id)
                .request()
                .get();
        if (response.readEntity(Long.class)!=0){
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, (Object) new BodyExceptionWrapper("Not deleted", "This offer has attachments with order card")).build();
        }
        boolean f = offerService.deleteOffer(id);
        if (f) return RestResponse.ok();
        log.info("No category with id={} deleted", id);
        return RestResponse.noContent();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> updateOffer(OfferDTO offerDTO) {
        List<BodyExceptionWrapper> reports = offerValidator.validateExistsOffer(offerDTO);
        if (reports.size() != 0) {
            log.info("No category with id={} updated (conflict)", offerDTO.getId());
            return RestResponse.ResponseBuilder.create(RestResponse.Status.CONFLICT, (Object) reports).build();
        }
        offerService.updateOffer(offerDTO);
        return RestResponse.ok();
    }

    @GET
    @Path("/{id}/characteristics")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getCharsByOfferId(@PathParam("id") Long id) {
        if (!offerService.isExists(id)) {
            log.info("No offer with id={} found", id);
            return RestResponse.notFound();
        }
        return RestResponse.ResponseBuilder.create(RestResponse.Status.OK, (Object) characteristicService.getAllCharacteristicsByOrderId(id)).build();
    }

    @POST
    @Path("/help")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getOffersForCustomer(Set<Long> ids) {
        log.debug("Try to find all offers for set ids={}", ids);
        List<OfferDTO> offerDTOS = findAllByPayList(ids);
        log.debug("Found {} offers", offerDTOS.size());
        return RestResponse.ResponseBuilder.create(RestResponse.Status.OK, (Object) offerDTOS).build();
    }

    @GET
    @Path("/countWithPayMethod/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getOfferCountByPayId(@PathParam("id") Long id) {
        Integer l = offerService.findByPayMethod(id).size();
        return RestResponse.ok(l);
    }

    private List<OfferDTO> findAllByPayList(Set<Long> ids) {
        List<OfferDTO> dtos = new ArrayList<>();
        for (Long payId : ids) {
            dtos.addAll(offerService.findByPayMethod(payId));
        }
        return dtos;
    }

    @GET
    @Path("/isExists/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> isOfferExists(@PathParam("id") Long id) {
        if (offerService.isExists(id)) return RestResponse.ok();
        return RestResponse.notFound();
    }

    @GET
    @Path("/{id}/categoryAndPrice")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getCategoryAndPrice(@PathParam("id") Long id) {
        OfferDTO offerDTO=offerService.getDTOById(id);
        if (offerDTO==null) return RestResponse.notFound();
        return RestResponse.ResponseBuilder.create(RestResponse.Status.OK,(Object) new CatPriceWrapper(categoryService.getById(offerDTO.getCategoryId()).getTitle(),offerDTO.getPrice())).build();
    }
}
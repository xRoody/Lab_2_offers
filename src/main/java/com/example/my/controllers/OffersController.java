package com.example.my.controllers;

import com.example.my.DTOs.OfferDTO;
import com.example.my.DTOs.PayMethod;
import com.example.my.exceptions.BodyExceptionWrapper;
import com.example.my.serviceImpls.OfferServiceImpl;
import com.example.my.services.CharacteristicService;
import com.example.my.validators.OfferValidator;

import org.eclipse.microprofile.rest.client.inject.RestClient;
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
import java.util.stream.Collectors;

@Path("/offers")
public class OffersController {
    private OfferServiceImpl offerService;
    private OfferValidator offerValidator;
    private CharacteristicService characteristicService;
    @Inject
    public void setOfferService(OfferServiceImpl offerService) {
        this.offerService = offerService;
    }
    @Inject
    public void setOfferValidator(OfferValidator offerValidator) {
        this.offerValidator = offerValidator;
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
    public RestResponse<Object> getById(@PathParam("id") Long id){
        OfferDTO offerDTO=offerService.getDTOById(id);
        if (offerDTO==null) return RestResponse.notFound();
        return RestResponse.ok(offerDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> addNewOffer(OfferDTO offerDTO){
        List<BodyExceptionWrapper> reports=offerValidator.validateNewOffer(offerDTO);
        if (reports.size()!=0) return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, (Object) reports).build();
        offerService.createOffer(offerDTO);
        return RestResponse.created(URI.create("/offers"));
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> delete(@PathParam("id") Long id){
        boolean f=offerService.deleteOffer(id);
        if (f) return RestResponse.ok();
        return RestResponse.noContent();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> updateOffer(OfferDTO offerDTO){
        List<BodyExceptionWrapper> reports=offerValidator.validateExistsOffer(offerDTO);
        if (reports.size()!=0) return RestResponse.ResponseBuilder.create(RestResponse.Status.CONFLICT, (Object) reports).build();
        offerService.updateOffer(offerDTO);
        return RestResponse.ok();
    }

    @GET
    @Path("/{id}/characteristics")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getCharsByOfferId(@PathParam("id") Long id){
        if (!offerService.isExists(id)) return RestResponse.notFound();
        return RestResponse.ResponseBuilder.create(RestResponse.Status.OK, (Object) characteristicService.getAllCharacteristicsByOrderId(id)).build();
    }

    @GET
    @Path("/{id}/help")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getOffersForCustomer(@PathParam("id") Long id, HttpHeaders httpHeaders){
        ResteasyClient resteasyClient = new ResteasyClientBuilderImpl().build();
        try {
            Response response=resteasyClient
                    .target("http://localhost:8081")
                    .path("/customers/{id}/payMethods")
                    .resolveTemplate("id", id)
                    .request()
                    .header(HttpHeaders.AUTHORIZATION, httpHeaders.getRequestHeaders().get(HttpHeaders.AUTHORIZATION).get(0))
                    .get();
            if (response.getStatus()==404) return RestResponse.notFound();
            if (response.getStatus()==401) return RestResponse.ResponseBuilder.create(RestResponse.Status.fromStatusCode(401),response.getEntity()).build();
            List<PayMethod> payMethods=response.readEntity(new GenericType<>(){});
            List<OfferDTO> offerDTOS=findAllByPayList(payMethods);
            return RestResponse.ResponseBuilder.create(RestResponse.Status.OK, (Object) offerDTOS).build();
        }
        catch (ProcessingException e) {
            e.printStackTrace();
            return RestResponse.serverError();
        }
    }

    //Если статус != ОК, то Quarkus добавляет к ответу заголовки Content-Length и Transfer-Encoding одновременно, что приводит к ошибке Postman (ТОЛЬКО!) в браузере всё нормально

    @GET
    @Path("/countWithPayMethod/{id}") //silly endpoint's name
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getOfferCountByPayId(@PathParam("id") Long id){
        Integer l=offerService.findByPayMethod(id).size();
        return RestResponse.ok(l);
    }

    private List<OfferDTO> findAllByPayList(List<PayMethod> payMethods){
        List<OfferDTO> dtos=new ArrayList<>();
        Set<Long> l=payMethods.stream().map(x->x.getPayId()).collect(Collectors.toSet());
        for (Long payId:l){
            dtos.addAll(offerService.findByPayMethod(payId));
        }
        return dtos;
    }
}
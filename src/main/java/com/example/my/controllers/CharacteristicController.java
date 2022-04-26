package com.example.my.controllers;

import com.example.my.DTOs.CharacteristicDTO;
import com.example.my.exceptions.BodyExceptionWrapper;
import com.example.my.services.CharacteristicService;
import com.example.my.validators.CharValidator;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;

@Slf4j
@Path("/characteristics")
public class CharacteristicController {
    private CharacteristicService characteristicService;
    private CharValidator validator;

    @Inject
    public CharacteristicController(CharacteristicService characteristicService, CharValidator validator) {
        this.validator=validator;
        this.characteristicService = characteristicService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getById(@PathParam("id") Long id) {
        CharacteristicDTO dto =characteristicService.getById(id);
        if (dto==null) {
            log.info("No characteristic with id={} found", id);
            return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, null).build();
        }
        return RestResponse.ResponseBuilder.create(RestResponse.Status.OK, (Object) dto).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> addNewChar(CharacteristicDTO characteristicDTO){
        List<BodyExceptionWrapper> reports=validator.validate(characteristicDTO);
        if (reports.size()!=0) {
            log.info(reports.toString());
            return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, (Object) reports).build();
        }
        characteristicService.addCharacteristic(characteristicDTO);
        return RestResponse.created(URI.create("/characteristics"));
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> update(CharacteristicDTO characteristicDTO){
        List<BodyExceptionWrapper> reports=validator.validateUpdate(characteristicDTO);
        if (reports.size()!=0) {
            log.info("No category with id={} has been updated (conflict)", characteristicDTO.getId());
            return RestResponse.ResponseBuilder.create(RestResponse.Status.CONFLICT, (Object) reports).build();
        }
        characteristicService.update(characteristicDTO);
        return RestResponse.ResponseBuilder.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> deleteChar(@PathParam("id") Long id){
        if(characteristicService.delete(id)) return RestResponse.ok();
        log.info("No category with id={} deleted", id);
        return RestResponse.noContent();
    }
}

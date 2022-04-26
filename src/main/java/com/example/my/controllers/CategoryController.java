package com.example.my.controllers;

import com.example.my.DTOs.CategoryDTO;
import com.example.my.services.CategoryService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/category")
public class CategoryController {
    private CategoryService categoryService;

    @Inject
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CategoryDTO> getAll(){
        return categoryService.getAllCategories();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Object> getById(@PathParam("id") Long id){
        CategoryDTO categoryDTO=categoryService.getById(id);
        if (categoryDTO==null) return RestResponse.notFound();
        return RestResponse.ResponseBuilder.create(RestResponse.Status.OK, (Object) categoryDTO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> add(CategoryDTO categoryDTO){
        categoryService.add(categoryDTO);
        return null;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Object> update(CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return null;
    }
}

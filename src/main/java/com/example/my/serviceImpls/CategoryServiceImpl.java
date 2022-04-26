package com.example.my.serviceImpls;

import com.example.my.DTOs.CategoryDTO;
import com.example.my.DTOs.OfferDTO;
import com.example.my.entities.Category;
import com.example.my.repositories.CategoryRepo;
import com.example.my.services.CategoryService;
import com.example.my.services.CharacteristicService;
import com.example.my.services.OfferService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepo categoryRepo;
    private OfferService offerService;

    @Inject
    public CategoryServiceImpl(CategoryRepo categoryRepo, OfferService offerService) {
        this.categoryRepo = categoryRepo;
        this.offerService=offerService;
    }

    @Override
    public boolean isExists(Long categoryId) {
        return categoryRepo.findById(categoryId)!=null;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepo.findAll().stream().map(x->getDTOByObj(x)).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getById(Long id) {
        return getDTOByObj(categoryRepo.findById(id));
    }

    @Override
    public void add(CategoryDTO categoryDTO) {
        Category category=Category.builder()
                .title(categoryDTO.getTitle())
                .build();
        categoryRepo.persist(category);
        log.info("Category {} has been added", categoryDTO);
    }

    public void update(CategoryDTO categoryDTO){
        Category category=categoryRepo.findById(categoryDTO.getId());
        category.setTitle(categoryDTO.getTitle());
        category.getOffers().clear();
        for (OfferDTO dto:categoryDTO.getOffers()){
            dto.setCategoryId(category.getId());
            if (offerService.isExists(dto.getId())){
                offerService.updateOffer(dto);
            }else {
                dto.setId(null);
                offerService.createOffer(dto);
            }
        }
        categoryRepo.persist(category);
        log.info("category id={} has been updated", categoryDTO.getId());
    }

    private CategoryDTO getDTOByObj(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .title(category.getTitle())
                .offers(category.getOffers().stream().map(x->offerService.getDTOByObj(x)).collect(Collectors.toList()))
                .build();
    }



}

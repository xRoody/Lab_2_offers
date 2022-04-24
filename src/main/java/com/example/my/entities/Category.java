package com.example.my.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_id")
    private Long id;
    @Column(name = "title")
    private String title;
    @OneToMany(mappedBy = "category", orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Collection<Offer> offers=new HashSet<>();
}

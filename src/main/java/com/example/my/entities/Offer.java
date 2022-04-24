package com.example.my.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "Offer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private Double price;
    @Column(name = "pay_method")
    private Long pay_id;
    @OneToMany(mappedBy = "offer", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Collection<Characteristic> characteristics=new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "cat_id")
    private Category category;
}

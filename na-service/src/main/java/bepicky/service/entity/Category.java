package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "category")
@EqualsAndHashCode(callSuper = true)
@ToString
public class Category extends IdEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent")
    @JsonIgnore
    @ToString.Exclude
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Category> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CategoryLocalisation> localisations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "source_page_category",
        joinColumns = {@JoinColumn(name = "id_category")},
        inverseJoinColumns = {@JoinColumn(name = "id_source_page")}
    )
    @ToString.Exclude
    private List<SourcePage> sourcePages;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reader_category",
        joinColumns = {@JoinColumn(name = "id_category")},
        inverseJoinColumns = {@JoinColumn(name = "id_reader")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<Reader> readers;
}

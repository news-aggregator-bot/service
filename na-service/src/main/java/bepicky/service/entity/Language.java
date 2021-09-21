package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "language")
@ToString(includeFieldNames = false)
public class Language {

    @Id
    @NotBlank
    private String lang;

    @Column(nullable = false)
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String name;

    @Column(nullable = false)
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String localized;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "source_page_language",
        joinColumns = {@JoinColumn(name = "language")},
        inverseJoinColumns = {@JoinColumn(name = "id_source_page")}
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SourcePage> sourcePages;

    @OneToMany(mappedBy = "primaryLanguage", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reader> readers = new ArrayList<>();
}

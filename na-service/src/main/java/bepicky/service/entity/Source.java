package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "source")
@EqualsAndHashCode(callSuper = true)
@ToString
public class Source extends DatedEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private List<SourcePage> pages;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "reader_source",
        joinColumns = {@JoinColumn(name = "id_source")},
        inverseJoinColumns = {@JoinColumn(name = "id_reader")}
    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Reader> readers;
}

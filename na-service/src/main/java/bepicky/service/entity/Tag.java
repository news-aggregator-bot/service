package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tag")
@EqualsAndHashCode(callSuper = true)
public class Tag extends DatedEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String value;

    @Column(nullable = false, length = 50)
    private String normalisedValue;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reader_tag",
        joinColumns = {@JoinColumn(name = "id_tag")},
        inverseJoinColumns = {@JoinColumn(name = "id_reader")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<Reader> readers = new HashSet<>();
}

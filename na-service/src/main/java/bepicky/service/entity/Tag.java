package bepicky.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "tag")
@EqualsAndHashCode(callSuper = true)
public class Tag extends DatedEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String value;

    @Column(nullable = false, length = 50)
    private String normalisedValue;
}

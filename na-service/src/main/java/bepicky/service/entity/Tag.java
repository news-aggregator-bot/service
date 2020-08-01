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
public class Tag extends IdEntity {

    @Column(nullable = false)
    private String name;
}

package bepicky.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "localisation")
@EqualsAndHashCode(callSuper = true)
@ToString(includeFieldNames = false)
public class LocalisationEntity extends IdEntity {

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language")
    private LanguageEntity language;

}

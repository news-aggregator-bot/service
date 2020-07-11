package vlad110kg.news.aggregator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tag")
@EqualsAndHashCode(callSuper = true)
public class Tag extends IdEntity {

    @Column(nullable = false)
    private String value;
}

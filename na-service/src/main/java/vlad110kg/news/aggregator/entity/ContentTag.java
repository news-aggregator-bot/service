package vlad110kg.news.aggregator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "content_tag")
@EqualsAndHashCode(callSuper = true)
public class ContentTag extends IdEntity {

    @Enumerated(EnumType.STRING)
    private ContentTagType type;

    @Column(nullable = false)
    private String value;
}

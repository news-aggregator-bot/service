package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(nullable = false, name = "match_strategy")
    @Enumerated(EnumType.STRING)
    @JsonProperty("match_strategy")
    private ContentTagMatchStrategy matchStrategy;

}

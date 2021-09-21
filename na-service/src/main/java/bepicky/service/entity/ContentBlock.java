package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "content_block")
@EqualsAndHashCode(callSuper = true)
@ToString(includeFieldNames = false)
public class ContentBlock extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_page")
    @JsonIgnore
    @ToString.Exclude
    private SourcePage sourcePage;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "content_block_tag",
        joinColumns = {@JoinColumn(name = "id_block")},
        inverseJoinColumns = {@JoinColumn(name = "id_tag")}
    )
    private Set<ContentTag> tags;

    @Transient
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Map<ContentTagType, ContentTag> typeMap;

    public ContentTag findByType(ContentTagType type) {
        if (typeMap == null) {
            typeMap = tags.stream()
                .collect(Collectors.toMap(ContentTag::getType, Function.identity()));
        }
        return typeMap.get(type);
    }
}

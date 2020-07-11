package vlad110kg.news.aggregator.entity;

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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "content_block")
@EqualsAndHashCode(callSuper = true)
@ToString
public class ContentBlock extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_page")
    @JsonIgnore
    @ToString.Exclude
    private SourcePage sourcePage;

    @ManyToMany
    @JoinTable(
        name = "content_block_tag",
        joinColumns = {@JoinColumn(name = "id_block")},
        inverseJoinColumns = {@JoinColumn(name = "id_tag")}
    )
    @EqualsAndHashCode.Exclude
    private List<ContentTag> tags;

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

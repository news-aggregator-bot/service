package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@Table(name = "source_page")
@EqualsAndHashCode(callSuper = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SourcePage extends DatedEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Transient
    @EqualsAndHashCode.Exclude
    private String host;

    @Column(nullable = false, name = "url_normalisation")
    @Enumerated(EnumType.STRING)
    @JsonProperty("url_normalisation")
    private UrlNormalisation urlNormalisation;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;

    @ManyToMany
    @JoinTable(
        name = "source_page_language",
        joinColumns = {@JoinColumn(name = "id_source_page")},
        inverseJoinColumns = {@JoinColumn(name = "language")}
    )
    @EqualsAndHashCode.Exclude
    private Set<Language> languages;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_source")
    @JsonIgnore
    @ToString.Exclude
    private Source source;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "source_page_category",
        joinColumns = {@JoinColumn(name = "id_source_page")},
        inverseJoinColumns = {@JoinColumn(name = "id_category")}
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Category> categories;

    @OneToMany(mappedBy = "sourcePage", cascade = ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonProperty("content_blocks")
    private Set<ContentBlock> contentBlocks;

    @Transient
    private Multimap<CategoryType, Category> typedCategories;

    public Collection<Category> getRegions() {
        initTypedCategories();
        return typedCategories.get(CategoryType.REGION);
    }

    public Collection<Category> getCommon() {
        initTypedCategories();
        return typedCategories.get(CategoryType.COMMON);
    }

    public String getHost() {
        if (host == null) {
            try {
                host = new URL(this.getUrl()).getHost();
            } catch (MalformedURLException ignore) {
            }
        }
        return host;
    }

    private void initTypedCategories() {
        if (typedCategories == null || typedCategories.isEmpty()) {
            typedCategories = Multimaps.index(categories, Category::getType);
        }
    }
}

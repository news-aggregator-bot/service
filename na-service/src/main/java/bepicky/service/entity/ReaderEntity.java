package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "reader")
@EqualsAndHashCode(callSuper = true)
@ToString(includeFieldNames = false)
public class ReaderEntity extends DatedEntity {

    @JsonProperty("chat_id")
    @Column(nullable = false, unique = true)
    private Long chatId;

    @Column
    private String username;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_language")
    @JsonProperty("primary_language")
    private LanguageEntity primaryLanguage;

    @JsonProperty("tags_limit")
    @Column(name = "tags_limit", nullable = false)
    private Long tagsLimit;

    @ManyToMany
    @JoinTable(
        name = "reader_category",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "id_category")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<CategoryEntity> categories;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "reader_lang",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "language")}
    )
    @EqualsAndHashCode.Exclude
    private Set<LanguageEntity> languages;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "reader_source",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "id_source")}
    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SourceEntity> sources;

    @ManyToMany
    @JoinTable(
        name = "reader_tag",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "id_tag")}
    )
    @EqualsAndHashCode.Exclude
    private Set<TagEntity> tags;

    public void addCategory(CategoryEntity category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        categories.add(category);
    }

    public void addAllCategories(CategoryEntity category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        categories.add(category);
        category.getSubcategories().forEach(this::addCategory);
    }

    public void removeAllCategory(CategoryEntity category) {
        if (this.categories != null) {
            categories.remove(category);
            category.getSubcategories().forEach(this::removeAllCategory);
            removeTopParent(category.getParent());
        }
    }

    public void removeCategory(CategoryEntity category) {
        if (this.categories != null) {
            categories.remove(category);
        }
    }

    private void removeTopParent(CategoryEntity parent) {
        if (parent != null) {
            if (parent.getParent() != null) {
                removeTopParent(parent.getParent());
            } else {
                categories.remove(parent);
            }
        }
    }

    public void addSource(SourceEntity source) {
        if (this.sources == null) {
            this.sources = new HashSet<>();
        }
        this.sources.add(source);
    }

    public void addSources(Set<SourceEntity> sources) {
        if (this.sources == null) {
            this.sources = new HashSet<>(sources.size());
        }
        this.sources.addAll(sources);
    }

    public void removeSources(Set<SourceEntity> sources) {
        this.sources.removeAll(sources);
    }

    public void removeSource(SourceEntity source) {
        this.sources.remove(source);
    }

    public void addLanguage(LanguageEntity language) {
        if (languages == null) {
            languages = new HashSet<>();
        }
        languages.add(language);
        Set<SourceEntity> langSources = language.getSourcePages().stream()
            .map(SourcePageEntity::getSource)
            .filter(SourceEntity::isPrimary)
            .collect(Collectors.toSet());
        addSources(langSources);
    }

    public void removeLanguage(LanguageEntity language) {
        if (languages != null) {
            languages.remove(language);
        }
    }

    public boolean isActive() {
        return status == Status.ENABLED || status == Status.IN_SETTINGS;
    }

    public enum Status {
        ENABLED, DISABLED, BLOCKED, PAUSED, IN_SETTINGS
    }
}

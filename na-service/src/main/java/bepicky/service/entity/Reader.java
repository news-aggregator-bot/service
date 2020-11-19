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
@ToString
public class Reader extends DatedEntity {

    @JsonProperty("chat_id")
    @Column(nullable = false, unique = true)
    private Long chatId;

    @Column(nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
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
    private Language primaryLanguage;

    @ManyToMany
    @JoinTable(
        name = "reader_category",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "id_category")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
        name = "reader_lang",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "language")}
    )
    @EqualsAndHashCode.Exclude
    private Set<Language> languages;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "notify_queue",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "id_news_note")}
    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<NewsNote> notifyQueue;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "reader_source",
        joinColumns = {@JoinColumn(name = "id_reader")},
        inverseJoinColumns = {@JoinColumn(name = "id_source")}
    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Source> sources;

    public void addCategory(Category category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        categories.add(category);
    }

    public void addAllCategories(Category category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        categories.add(category);
        category.getSubcategories().forEach(this::addCategory);
    }

    public void removeCategory(Category category) {
        if (this.categories != null) {
            categories.remove(category);
            category.getSubcategories().forEach(this::removeCategory);
            removeTopParent(category.getParent());
        }
    }

    private void removeTopParent(Category parent) {
        if (parent != null) {
            if (parent.getParent() != null) {
                removeTopParent(parent.getParent());
            } else {
                categories.remove(parent);
            }
        }
    }

    public void addQueueNewsNote(Set<NewsNote> newsNote) {
        if (notifyQueue == null) {
            notifyQueue = new HashSet<>(newsNote.size());
        }
        notifyQueue.addAll(newsNote);
    }

    public void removeQueueNewsNote(Set<NewsNote> newsNote) {
        notifyQueue.removeAll(newsNote);
    }

    public void addSource(Source source) {
        if (this.sources == null) {
            this.sources = new HashSet<>();
        }
        this.sources.add(source);
    }

    public void addSources(Set<Source> sources) {
        if (this.sources == null) {
            this.sources = new HashSet<>(sources.size());
        }
        this.sources.addAll(sources);
    }

    public void removeSources(Set<Source> sources) {
        this.sources.removeAll(sources);
    }

    public void removeSource(Source source) {
        this.sources.remove(source);
    }

    public void addLanguage(Language language) {
        if (languages == null) {
            languages = new HashSet<>();
        }
        languages.add(language);
        Set<Source> langSources = language.getSourcePages().stream()
            .map(SourcePage::getSource)
            .filter(Source::isPrimary)
            .collect(Collectors.toSet());
        addSources(langSources);
    }

    public void removeLanguage(Language language) {
        if (languages != null) {
            languages.remove(language);
        }
    }

    public enum Status {
        ENABLED, DISABLED
    }
}

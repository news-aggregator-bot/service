package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "news_note")
@EqualsAndHashCode(callSuper = true)
@ToString(includeFieldNames = false)
public class NewsNoteEntity extends DatedEntity {

    @Column(nullable = false)
    private String title;

    @ToString.Exclude
    @Column(name = "normalised_title", nullable = false)
    private String normalisedTitle;

    @Column(nullable = false)
    private String url;

    @ToString.Exclude
    private String author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "news_note_source_page",
        joinColumns = {@JoinColumn(name = "id_news_note")},
        inverseJoinColumns = {@JoinColumn(name = "id_source_page")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<SourcePageEntity> sourcePages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "news_note_tag",
        joinColumns = {@JoinColumn(name = "id_news_note")},
        inverseJoinColumns = {@JoinColumn(name = "id_tag")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<TagEntity> tags = new HashSet<>();

    public void addSourcePage(SourcePageEntity page) {
        sourcePages.add(page);
    }

    public Set<LanguageEntity> getLanguages() {
        return sourcePages.stream()
            .flatMap(sp -> sp.getLanguages().stream())
            .collect(Collectors.toSet());
    }

}

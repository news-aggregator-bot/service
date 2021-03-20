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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "news_note")
@EqualsAndHashCode(callSuper = true)
public class NewsNote extends DatedEntity {

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
    private Set<SourcePage> sourcePages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "news_note_tag",
        joinColumns = {@JoinColumn(name = "id_news_note")},
        inverseJoinColumns = {@JoinColumn(name = "id_tag")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();

    public void addSourcePage(SourcePage page) {
        sourcePages.add(page);
    }

}

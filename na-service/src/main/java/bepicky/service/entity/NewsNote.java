package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "news_note")
@EqualsAndHashCode(callSuper = true)
@ToString
public class NewsNote extends DatedEntity {

    @Column(nullable = false)
    private String title;

    @Column(name = "normalised_title", nullable = false)
    private String normalisedTitle;

    @Column(nullable = false)
    private String url;

    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_page")
    @JsonIgnore
    @ToString.Exclude
    private SourcePage sourcePage;
}

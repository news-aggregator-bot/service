package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "news_note")
@EqualsAndHashCode(callSuper = true)
@ToString
public class NewsNote extends DatedEntity {

    private String title;

    private String url;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_page")
    @JsonIgnore
    @ToString.Exclude
    private SourcePage sourcePage;
}

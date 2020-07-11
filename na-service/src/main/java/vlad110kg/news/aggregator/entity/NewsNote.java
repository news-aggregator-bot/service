package vlad110kg.news.aggregator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "news_note")
@EqualsAndHashCode(callSuper = true)
public class NewsNote extends DatedEntity {

    private String title;

    private String url;

    private String description;

    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_page")
    @JsonIgnore
    private SourcePage sourcePage;
}

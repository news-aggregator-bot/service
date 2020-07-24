package vlad110kg.news.aggregator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@Table(name = "source_page")
@EqualsAndHashCode(callSuper = true)
@ToString
public class SourcePage extends DatedEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language")
    private Language language;

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
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
        name = "reader_source_page",
        joinColumns = {@JoinColumn(name = "id_source_page")},
        inverseJoinColumns = {@JoinColumn(name = "id_reader")}
    )
    @ToString.Exclude
    private List<Reader> readers;

    @OneToMany(mappedBy = "sourcePage", cascade = ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<ContentBlock> contentBlocks;

}

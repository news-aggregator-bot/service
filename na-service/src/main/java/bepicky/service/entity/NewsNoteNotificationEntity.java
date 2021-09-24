package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import java.util.Date;

import static bepicky.service.entity.NewsNoteNotificationEntity.State.NEW;

@Data
@Entity
@Table(name = "news_note_notification")
@NoArgsConstructor
@ToString(includeFieldNames = false)
public class NewsNoteNotificationEntity {

    @EmbeddedId
    private NewsNoteNotificationIdEntity id;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Link link;

    @Column(name = "link_key")
    private String linkKey;

    @MapsId("readerId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reader", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private ReaderEntity reader;

    @MapsId("noteId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_note", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private NewsNoteEntity note;

    @Column(name = "creation_date", nullable = false)
    @EqualsAndHashCode.Exclude
    private Date creationDate;

    public NewsNoteNotificationEntity(ReaderEntity reader, NewsNoteEntity note) {
        this.id = new NewsNoteNotificationIdEntity(reader.getId(), note.getId());
        this.reader = reader;
        this.note = note;
        this.state = NEW;
    }

    public enum State {
        NEW, SENT
    }

    public enum Link {
        CATEGORY, TAG
    }

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = new Date();
        }
    }
}

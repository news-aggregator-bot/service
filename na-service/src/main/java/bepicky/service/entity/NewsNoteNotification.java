package bepicky.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import static bepicky.service.entity.NewsNoteNotification.State.NEW;

@Data
@Entity
@Table(name = "news_note_notification")
@NoArgsConstructor
public class NewsNoteNotification {

    @EmbeddedId
    private NewsNoteNotificationId id;

    @Enumerated(EnumType.STRING)
    private State state;

    @MapsId("readerId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reader", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private Reader reader;

    @MapsId("noteId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_note", referencedColumnName = "id")
    @JsonIgnore
    @ToString.Exclude
    private NewsNote note;

    public NewsNoteNotification(Reader reader, NewsNote note) {
        this.id = new NewsNoteNotificationId(reader.getId(), note.getId());
        this.reader = reader;
        this.note = note;
        this.state = NEW;
    }

    public enum State {
        NEW, SENT
    }

}

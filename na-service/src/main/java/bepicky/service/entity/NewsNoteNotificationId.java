package bepicky.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsNoteNotificationId implements Serializable {

    @Column(name = "id_reader")
    private long readerId;

    @Column(name = "id_note")
    private long noteId;
}

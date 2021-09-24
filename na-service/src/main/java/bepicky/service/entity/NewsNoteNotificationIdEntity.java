package bepicky.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = false)
public class NewsNoteNotificationIdEntity implements Serializable {

    @Column(name = "id_reader")
    private long readerId;

    @Column(name = "id_note")
    private long noteId;
}

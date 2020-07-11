package vlad110kg.news.aggregator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public class DatedEntity extends IdEntity {

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;
    @Column(name = "update_date", nullable = false)
    private Date updateDate;

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            Date date = new Date();
            this.creationDate = date;
            this.updateDate = date;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = new Date();
    }
}

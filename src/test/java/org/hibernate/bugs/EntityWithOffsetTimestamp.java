package org.hibernate.bugs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "entity_with_offset_timestamp")
public class EntityWithOffsetTimestamp {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    private EntityWithOffsetTimestamp() {
    }

    public EntityWithOffsetTimestamp(int id, OffsetDateTime currentTime) {
        this.id = id;
        this.createdAt = currentTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

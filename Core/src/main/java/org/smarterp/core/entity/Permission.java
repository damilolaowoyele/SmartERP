package org.smarterp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "permission_id", updatable = false, nullable = false)
    private UUID permissionId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 200)
    private String description;
}

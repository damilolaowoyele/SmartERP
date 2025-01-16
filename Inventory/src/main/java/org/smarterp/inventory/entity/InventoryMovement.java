package org.smarterp.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.smarterp.core.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "inventory_movement")
public class InventoryMovement {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "movement_id", updatable = false, nullable = false)
    private UUID movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_batch_id")
    private ItemBatch itemBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_section_id")
    private WarehouseSection sourceWarehouseSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_section_id")
    private WarehouseSection destinationWarehouseSection;

    @Column(name = "movement_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "remarks")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public enum MovementType {
        INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT
    }

    @PrePersist
    protected void onCreate() {
        movementDate = LocalDateTime.now();
    }
}

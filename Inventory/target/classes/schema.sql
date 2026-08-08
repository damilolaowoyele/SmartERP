CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE category (
                          category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          parent_category_id UUID REFERENCES category(category_id) ON DELETE SET NULL, -- For nested categories
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product (
                         product_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         product_name VARCHAR(255) NOT NULL,
                         description TEXT,
                         price DECIMAL(10, 2) NOT NULL,
                         default_cost DECIMAL(12, 2),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item (
                      item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                      product_id UUID REFERENCES product(product_id) ON DELETE SET NULL, -- Nullable for non-commercial items
                      item_name VARCHAR(255) NOT NULL,
                      barcode VARCHAR(100),
                      description TEXT,
                      category_id UUID REFERENCES category(category_id) ON DELETE SET NULL,
                      unit_of_measure VARCHAR(50) NOT NULL,
                      weight DECIMAL(10, 2),
                      volume DECIMAL(10, 2),
                      temperature_sensitive BOOLEAN DEFAULT FALSE,
                      storage_conditions TEXT, -- E.g., "Keep frozen at -18°C"
                      handling_instructions TEXT,
                      remarks text,
                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item_category (
                               item_id UUID REFERENCES item(item_id) ON DELETE CASCADE,
                               category_id UUID REFERENCES category(category_id) ON DELETE CASCADE,
                               PRIMARY KEY (item_id, category_id)
);

CREATE TABLE item_batch (
                            batch_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            item_id UUID REFERENCES item(item_id) ON DELETE CASCADE, -- Links batch to specific items
                            batch_number VARCHAR(100) NOT NULL, -- Unique identifier for each batch
                            manufacture_date DATE,
                            expiry_date DATE,
                            quantity INT NOT NULL CHECK (quantity >= 0), -- Total quantity in this batch
                            warehouse_id UUID REFERENCES warehouse(warehouse_id) ON DELETE SET NULL, -- Optional: Initial warehouse
                            section_id UUID REFERENCES warehouse_section(section_id) ON DELETE SET NULL, -- Optional: Initial section
                            created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                            UNIQUE (item_id, batch_number) -- Ensures uniqueness within an item context
);

CREATE TABLE stock_count (
                              count_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(), -- Unique identifier for each stock record
                              item_id UUID NOT NULL REFERENCES item(item_id) ON DELETE CASCADE,
                              warehouse_id UUID NOT NULL REFERENCES warehouse(warehouse_id) ON DELETE CASCADE,
                              section_id UUID REFERENCES warehouse_section(section_id) ON DELETE SET NULL,
                              item_batch_id UUID REFERENCES item_batch(batch_id) ON DELETE CASCADE, -- NULL for aggregated records
                              count_type VARCHAR(50) NOT NULL CHECK (count_type IN ('Batch', 'Aggregated')), -- 'Batch' or 'Aggregated'
                              quantity INT NOT NULL DEFAULT 0,
                              last_updated TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE warehouse (
                           warehouse_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name VARCHAR(255) NOT NULL,
                           location VARCHAR(255),
                           capacity DECIMAL(12, 2),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE warehouse_section (
                                   section_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   name VARCHAR(255) NOT NULL,
                                   warehouse_id UUID REFERENCES warehouse(warehouse_id) ON DELETE CASCADE,
                                   capacity DECIMAL(12, 2),
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_movement (
                                    movement_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    item_batch_id UUID REFERENCES item_batch(batch_id) ON DELETE CASCADE,
                                    source_warehouse_id UUID REFERENCES warehouse(warehouse_id) ON DELETE SET NULL,
                                    destination_warehouse_id UUID REFERENCES warehouse(warehouse_id) ON DELETE SET NULL,
                                    source_warehouse_section_id UUID REFERENCES warehouse_section(section_id) ON DELETE SET NULL,
                                    destination_warehouse_section_id UUID REFERENCES warehouse_section(section_id) ON DELETE SET NULL,
                                    movement_type VARCHAR(50) NOT NULL CHECK (movement_type IN ('INBOUND', 'OUTBOUND', 'TRANSFER', 'ADJUSTMENT')),
                                    quantity INT NOT NULL CHECK (quantity >= 0),
                                    movement_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    remarks TEXT, -- Additional notes about the movement
                                    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL -- Track the user who initiated the movement
);

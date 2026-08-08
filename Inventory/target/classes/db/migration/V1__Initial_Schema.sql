CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create a foreign key table for reference
CREATE TABLE user_references (
    user_id UUID PRIMARY KEY,
    CONSTRAINT fk_core_user CHECK (user_id IS NOT NULL)
);

-- Create ENUM type for movement
CREATE TYPE movement_type AS ENUM ('INBOUND', 'OUTBOUND', 'TRANSFER', 'ADJUSTMENT');

-- Updated tables with TIMESTAMPTZ and proper naming
CREATE TABLE categories (
    category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    default_cost DECIMAL(12, 2),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE items (
    item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID REFERENCES products(product_id) ON DELETE SET NULL,
    item_name VARCHAR(255) NOT NULL,
    barcode VARCHAR(100),
    description TEXT,
    category_id UUID REFERENCES categories(category_id) ON DELETE SET NULL,
    unit_of_measure VARCHAR(50) NOT NULL,
    weight DECIMAL(10, 2),
    volume DECIMAL(10, 2),
    temperature_sensitive BOOLEAN DEFAULT FALSE,
    storage_conditions TEXT,
    handling_instructions TEXT,
    remarks TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item_categories (
    item_id UUID REFERENCES items(item_id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(category_id) ON DELETE CASCADE,
    PRIMARY KEY (item_id, category_id)
);

CREATE TABLE warehouses (
    warehouse_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    capacity DECIMAL(12, 2),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE warehouse_sections (
    section_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    warehouse_id UUID REFERENCES warehouses(warehouse_id) ON DELETE CASCADE,
    capacity DECIMAL(12, 2),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE item_batches (
    batch_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID REFERENCES items(item_id) ON DELETE CASCADE,
    batch_number VARCHAR(100) NOT NULL,
    manufacture_date DATE,
    expiry_date DATE,
    quantity INT NOT NULL CHECK (quantity >= 0),
    warehouse_id UUID REFERENCES warehouses(warehouse_id) ON DELETE SET NULL,
    section_id UUID REFERENCES warehouse_sections(section_id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (item_id, batch_number)
);

CREATE TABLE stock_counts (
    count_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
    warehouse_id UUID NOT NULL REFERENCES warehouses(warehouse_id) ON DELETE CASCADE,
    section_id UUID REFERENCES warehouse_sections(section_id) ON DELETE SET NULL,
    item_batch_id UUID REFERENCES item_batches(batch_id) ON DELETE CASCADE,
    count_type VARCHAR(50) NOT NULL CHECK (count_type IN ('Batch', 'Aggregated')),
    quantity INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (item_id, warehouse_id, section_id, item_batch_id)
);

CREATE TABLE inventory_movements (
    movement_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_batch_id UUID REFERENCES item_batches(batch_id) ON DELETE CASCADE,
    source_warehouse_id UUID REFERENCES warehouses(warehouse_id) ON DELETE SET NULL,
    destination_warehouse_id UUID REFERENCES warehouses(warehouse_id) ON DELETE SET NULL,
    source_warehouse_section_id UUID REFERENCES warehouse_sections(section_id) ON DELETE SET NULL,
    destination_warehouse_section_id UUID REFERENCES warehouse_sections(section_id) ON DELETE SET NULL,
    movement_type movement_type NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    movement_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    created_by UUID REFERENCES user_references(user_id) ON DELETE SET NULL
);

--CREATE TABLE inventory_movements (
--    movement_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    item_batch_id UUID REFERENCES item_batches(batch_id) ON DELETE CASCADE,
--    source_warehouse_id UUID REFERENCES warehouses(warehouse_id) ON DELETE SET NULL,
--    destination_warehouse_id UUID REFERENCES warehouses(warehouse_id) ON DELETE SET NULL,
--    source_warehouse_section_id UUID REFERENCES warehouse_sections(section_id) ON DELETE SET NULL,
--    destination_warehouse_section_id UUID REFERENCES warehouse_sections(section_id) ON DELETE SET NULL,
--    movement_type movement_type NOT NULL,
--    quantity INT NOT NULL CHECK (quantity >= 0),
--    movement_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
--    remarks TEXT,
--    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL
--);

-- Create updated_at triggers for all tables
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for all tables with updated_at column
CREATE TRIGGER update_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_items_updated_at
    BEFORE UPDATE ON items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_warehouses_updated_at
    BEFORE UPDATE ON warehouses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_warehouse_sections_updated_at
    BEFORE UPDATE ON warehouse_sections
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_item_batches_updated_at
    BEFORE UPDATE ON item_batches
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
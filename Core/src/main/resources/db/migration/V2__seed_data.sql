-- Insert default roles
INSERT INTO roles (role_id, name, description) VALUES 
    (uuid_generate_v4(), 'ROLE_ADMIN', 'Administrator with full system access'),
    (uuid_generate_v4(), 'ROLE_USER', 'Standard user with basic access'),
    (uuid_generate_v4(), 'ROLE_INVENTORY_MANAGER', 'Inventory management role'),
    (uuid_generate_v4(), 'ROLE_WAREHOUSE_STAFF', 'Warehouse operations staff');

-- Insert default permissions
INSERT INTO permissions (permission_id, name, description) VALUES 
    (uuid_generate_v4(), 'USER_READ', 'View user information'),
    (uuid_generate_v4(), 'USER_CREATE', 'Create new users'),
    (uuid_generate_v4(), 'USER_UPDATE', 'Update user information'),
    (uuid_generate_v4(), 'USER_DELETE', 'Delete users'),
    (uuid_generate_v4(), 'ROLE_READ', 'View roles'),
    (uuid_generate_v4(), 'ROLE_CREATE', 'Create roles'),
    (uuid_generate_v4(), 'ROLE_UPDATE', 'Update roles'),
    (uuid_generate_v4(), 'ROLE_DELETE', 'Delete roles'),
    (uuid_generate_v4(), 'INVENTORY_READ', 'View inventory'),
    (uuid_generate_v4(), 'INVENTORY_WRITE', 'Modify inventory'),
    (uuid_generate_v4(), 'WAREHOUSE_READ', 'View warehouse information'),
    (uuid_generate_v4(), 'WAREHOUSE_WRITE', 'Modify warehouse information');

-- Assign permissions to ADMIN role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.user_id, r.role_id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Link all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

-- Link basic permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' AND p.name IN ('USER_READ');

-- Link inventory permissions to INVENTORY_MANAGER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_INVENTORY_MANAGER' 
AND p.name IN ('INVENTORY_READ', 'INVENTORY_WRITE', 'WAREHOUSE_READ', 'WAREHOUSE_WRITE');

-- Link warehouse permissions to WAREHOUSE_STAFF role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r, permissions p
WHERE r.name = 'ROLE_WAREHOUSE_STAFF' 
AND p.name IN ('WAREHOUSE_READ', 'INVENTORY_READ');

-- RetailCore development seed data.
-- Applied by scripts/seed.ps1 through psql inside the postgres container.



INSERT INTO "Products" ("Id", "Sku", "Name", "Category", "UnitPrice", "WeightKg", "Discontinued") VALUES
    (1, 'SKU-1001', 'Aurora Desk Lamp',                  'Lighting',    49.90, 1.200, false),
    (2, 'SKU-1002', 'Nimbus Wireless Mouse',             'Electronics', 24.50, 0.090, false),
    (3, 'SKU-1003', 'Cascade Mechanical Keyboard',       'Electronics', 89.00, 0.950, false),
    (4, 'SKU-1004', 'Trailhead Water Bottle 750ml',      'Outdoors',    18.75, 0.300, false),
    (5, 'SKU-1005', 'Legacy Gift Card 25',               'GiftCards',   25.00, 0.010, false),
    (6, 'SKU-1006', 'Summit Laptop Stand',               'Office',      39.99, 1.400, false),
    (7, 'SKU-1007', 'Drift Noise-Cancelling Headphones', 'Electronics', 129.00, 0.350, false),
    (8, 'SKU-1008', 'Ember Ceramic Mug',                 'Kitchen',     12.40, 0.420, false);



INSERT INTO "StockItems" ("ProductId", "WarehouseCode", "QuantityAvailable", "UpdatedAtUtc") VALUES
    (1, 'MAIN', 120, CURRENT_TIMESTAMP),
    (2, 'MAIN', 300, CURRENT_TIMESTAMP),
    (3, 'MAIN',  45, CURRENT_TIMESTAMP),
    (4, 'MAIN', 200, CURRENT_TIMESTAMP),
    (5, 'MAIN', 500, CURRENT_TIMESTAMP),
    (6, 'MAIN',  80, CURRENT_TIMESTAMP),
    (7, 'MAIN',   5, CURRENT_TIMESTAMP),
    (8, 'MAIN', 150, CURRENT_TIMESTAMP);

INSERT INTO "Orders" ("Id", "Reference", "CustomerEmail", "ShippingRegion", "Status", "Subtotal", "DiscountAmount", "TaxAmount", "ShippingAmount", "Total", "PaymentId", "CreatedAtUtc", "ConfirmedAtUtc", "Notified") VALUES
    (1, 'RC-20260710-4821', 'dana.smith@example.com', 'CA', 'Confirmed',  98.90, 1.98, 7.03, 8.47, 112.42, 'txn_5f0a2b6c1d9e480fb3a1', '2026-07-10T09:14:00Z', '2026-07-10T09:14:03Z', true),
    (2, 'RC-20260715-2210', 'lee.wong@example.com',   'NY', 'Pending',   129.00, 2.58, 5.06, 7.34, 138.82, NULL,                       '2026-07-15T16:40:00Z', NULL,                    false),
    (3, 'RC-20260716-7743', 'mia.jones@example.com',  'TX', 'Confirmed',  62.40, 1.25, 3.82, 7.43,  72.40, 'txn_9c81e4d2ab7f43e0a6c2', '2026-07-16T11:05:00Z', '2026-07-16T11:05:02Z', true);



INSERT INTO "OrderLines" ("OrderId", "ProductId", "Quantity", "UnitPrice", "LineTotal") VALUES
    (1, 1, 1,  49.90,  49.90),
    (1, 2, 2,  24.50,  49.00),
    (2, 7, 1, 129.00, 129.00),
    (3, 5, 2,  25.00,  50.00),
    (3, 8, 1,  12.40,  12.40);

-- The gift card program was discontinued; merchandising removed the product.
DELETE FROM "StockItems" WHERE "ProductId" = 5;
DELETE FROM "Products" WHERE "Id" = 5;

ALTER TABLE "Products" ALTER COLUMN "Id" RESTART WITH 9;
ALTER TABLE "Orders" ALTER COLUMN "Id" RESTART WITH 4;

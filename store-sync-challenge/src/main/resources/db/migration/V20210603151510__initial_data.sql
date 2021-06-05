INSERT INTO category (id, "name", created_at, updated_at) values
         (1, 'boots', now(), now()),
         (2, 'sandals', now(), now()),
         (3, 'sneakers', now(), now());

INSERT INTO product(id, sku, "name", category_id, price, created_at, updated_at) values
        (1, '000001', 'BV Lean leather ankle boots', 1, '89000', now(), now()),
        (2, '000002', 'Ashlington Lean leather ankle boots', 1, '99000', now(), now()),
        (3, '000003', 'Ashlington leather ankle boots', 1, '71000', now(), now()),
        (4, '000004', 'Naima embellished suede sandals', 2, '79500', now(), now()),
        (5, '000005', 'Nathane leather sneakers', 3, '59000', now(), now());

INSERT INTO discount("type", reference_id, "value", created_at, updated_at) values
        ('CATEGORY', 1, 30, now(), now()),
        ('PRODUCT', 3, 10, now(), now());
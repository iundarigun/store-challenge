INSERT INTO category (id, "name") values
         (1, 'boots'),
         (2, 'sandals'),
         (3, 'sneakers');

INSERT INTO product(id, sku, "name", category_id, price) values
        (1, '000001', 'BV Lean leather ankle boots', 1, '89000'),
        (2, '000002', 'Ashlington Lean leather ankle boots', 1, '99000'),
        (3, '000003', 'Ashlington leather ankle boots', 1, '71000'),
        (4, '000004', 'Naima embellished suede sandals', 2, '79500'),
        (5, '000005', 'Nathane leather sneakers', 3, '59000');

INSERT INTO discount("type", reference_id, "value") values
        ('CATEGORY', 1, 30),
        ('PRODUCT', 3, 10);
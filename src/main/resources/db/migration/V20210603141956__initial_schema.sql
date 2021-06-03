CREATE TABLE category(
   id                    BIGSERIAL PRIMARY KEY,
   "name"                character varying(255)       NOT NULL
);

ALTER TABLE ONLY category
    ADD CONSTRAINT category_unique01 UNIQUE ("name");

CREATE TABLE product (
   id                    BIGSERIAL PRIMARY KEY,
   sku                   character varying(255)       NOT NULL,
   "name"                character varying(255)       NOT NULL,
   category_id           bigint                       NOT NULL,
   price                 bigint                       NOT NULL
);

ALTER TABLE ONLY product
    ADD CONSTRAINT product_unique01 UNIQUE ("name", category_id);

ALTER TABLE ONLY product
    ADD CONSTRAINT product_unique02 UNIQUE (sku);

ALTER TABLE ONLY product
    ADD CONSTRAINT fk_product_category_id
    FOREIGN KEY (category_id)
    REFERENCES category(id);
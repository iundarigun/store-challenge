CREATE TABLE category(
   id                    BIGSERIAL PRIMARY KEY,
   "name"                character varying(255)       NOT NULL,
   created_at            timestamp without time zone  NOT NULL,
   updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY category
    ADD CONSTRAINT category_unique01 UNIQUE ("name");

CREATE TABLE product (
   id                    BIGSERIAL PRIMARY KEY,
   sku                   character varying(255)       NOT NULL,
   "name"                character varying(255)       NOT NULL,
   category_id           bigint                       NOT NULL,
   price                 bigint                       NOT NULL,
   created_at            timestamp without time zone  NOT NULL,
   updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY product
    ADD CONSTRAINT product_unique01 UNIQUE ("name", category_id);

ALTER TABLE ONLY product
    ADD CONSTRAINT product_unique02 UNIQUE (sku);

ALTER TABLE ONLY product
    ADD CONSTRAINT fk_product_category_id
    FOREIGN KEY (category_id)
    REFERENCES category(id);

CREATE TABLE discount (
     id                    BIGSERIAL PRIMARY KEY,
     "type"                character varying(100)       NOT NULL,
     reference_id          bigint                       NOT NULL,
     "value"               bigint                       NOT NULL,
     created_at            timestamp without time zone  NOT NULL,
     updated_at            timestamp without time zone  NOT NULL
);

ALTER TABLE ONLY discount
    ADD CONSTRAINT discount_unique01 UNIQUE ("type", reference_id);

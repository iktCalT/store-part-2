CREATE TABLE carts 
(
    id              BINARY(16) DEFAULT(uuid_to_bin(uuid()))
                    NOT NULL PRIMARY KEY,
    date_created    DATE DEFAULT(curdate()) NOT NULL
);

CREATE TABLE cart_items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id     BINARY(16) NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT DEFAULT 1 NOT NULL,
    CONSTRAINT cart_items_cart_product_unique
        UNIQUE(cart_id, product_id),
    CONSTRAINT cart_items_cart_id_fk
        FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT cart_items_product_id_fk
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
CREATE TABLE orders (
    id          BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status    VARCHAR(20) NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT orders_customer_id_fk 
        FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    unit_price  DECIMAL(10, 2) NOT NULL,
    quantity    INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,

    CONSTRAINT unit_price_positive_chk CHECK (unit_price > 0),
    CONSTRAINT quantity_positive_chk CHECK (quantity > 0),
    CONSTRAINT total_price_chk CHECK (total_price = unit_price * quantity),

    CONSTRAINT order_items_order_id_fk 
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT order_items_product_id_fk
        FOREIGN KEY (product_id) REFERENCES products(id)
);
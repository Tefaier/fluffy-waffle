CREATE TABLE account (
   id BIGSERIAL,
   login TEXT NOT NULL UNIQUE,
   firstname TEXT NOT NULL,
   lastname TEXT NOT NULL,
   password_hash TEXT NOT NULL,
   email TEXT NOT NULL UNIQUE,
   integer_part BIGINT NOT NULL CHECK(integer_part >= 0),
   value_decimal BIGINT NOT NULL CHECK(integer_part >= 0),
   currency TEXT NOT NULL,
   CONSTRAINT pk_user PRIMARY KEY (id)
);


CREATE TABLE lot (
   id BIGSERIAL,
   lot_dealer BIGINT NOT NULL,
   start_time TIMESTAMP WITH TIME ZONE NOT NULL,
   finish_time TIMESTAMP WITH TIME ZONE NOT NULL,
   description TEXT NOT NULL,
   images TEXT[] NOT NULL,
   state TEXT NOT NULL,
   initial_price_value BIGINT NOT NULL CHECK(initial_price_value >= 0),
   initial_price_value_decimal BIGINT NOT NULL CHECK(initial_price_value_decimal >= 0),
   initial_price_currency TEXT NOT NULL,
   minimum_price_value BIGINT NOT NULL CHECK(minimum_price_value >= 0),
   minimum_price_value_decimal BIGINT NOT NULL CHECK(minimum_price_value_decimal >= 0),
   minimum_price_currency TEXT NOT NULL,
   CONSTRAINT pk_lot PRIMARY KEY (id)
);

ALTER TABLE lot ADD CONSTRAINT FK_LOT_ON_LOT_DEALER FOREIGN KEY (lot_dealer) REFERENCES account (id) ON DELETE SET NULL;


CREATE TABLE bet (
   id BIGSERIAL,
   bet_maker BIGINT NOT NULL,
   bet_lot BIGINT NOT NULL,
   integer_part BIGINT NOT NULL CHECK(integer_part >= 0),
   value_decimal BIGINT NOT NULL CHECK(integer_part >= 0),
   currency TEXT NOT NULL,
   CONSTRAINT pk_bet PRIMARY KEY (id)
);

ALTER TABLE bet ADD CONSTRAINT FK_BET_ON_BET_LOT FOREIGN KEY (bet_lot) REFERENCES lot (id) ON DELETE CASCADE;

ALTER TABLE bet ADD CONSTRAINT FK_BET_ON_BET_MAKER FOREIGN KEY (bet_maker) REFERENCES account (id) ON DELETE SET NULL;

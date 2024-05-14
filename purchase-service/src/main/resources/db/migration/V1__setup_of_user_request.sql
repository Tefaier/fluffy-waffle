CREATE TABLE account (
	 id BIGSERIAL,
   main_db_id UUID NOT NULL UNIQUE,
   integer_part BIGINT NOT NULL CHECK(integer_part >= 0),
   value_decimal BIGINT NOT NULL CHECK(value_decimal >= 0),
   currency TEXT NOT NULL,
   CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE requests (
   id UUID,
   CONSTRAINT pk_request PRIMARY KEY (id)
);

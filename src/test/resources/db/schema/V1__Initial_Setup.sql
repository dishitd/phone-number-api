

create EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE phone_number (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	customer_id varchar NOT NULL,
	phone_no varchar NOT NULL,
	is_active bool NULL DEFAULT false,
	created_time timestamp NULL DEFAULT now(),
	CONSTRAINT phone_number_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX phone_number_customer_id_idx ON phone_number USING btree (customer_id, phone_no);
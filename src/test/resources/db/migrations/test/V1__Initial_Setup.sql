

create EXTENSION IF NOT EXISTS "uuid-ossp";

create TABLE if not exists public.phone_number (
	id uuid default uuid_generate_v4(),
	customerId VARCHAR not null,
	phone_number VARCHAR not null,
	is_active BOOLEAN default false,
	created_time TIMESTAMP default now(),
	primary key (id)
);
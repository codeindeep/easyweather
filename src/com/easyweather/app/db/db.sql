create table province (
	id integer primary key autoincrement,
	province_name text,
	province_code text
)

create table city (
    id integer primary key autoincrement,
    city_name text,
    city_code text,
    province_id integer
)

create table county (
    id integer primary key autoincrement,
    county_name text,
    county_code text,
    city_id integer
)
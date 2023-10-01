insert into students
select 
	s,
	substr(md5(random()::text), 0, (10 * random())::int + 5),
	substr(md5(random()::text), 0, (10 * random())::int + 5),
	'1/1/2000'::date + ('1 day'::interval * (1000 * random())::int),
	substr(md5(random()::text), 0, 10),
	substr(md5(random()::text), 0, (10 * random())::int + 5),
	now(),
	now()
from generate_series(1, 100000) as s;

insert into subjects
select
	s,
	substr(md5(random()::text), 0, (10 * random())::int + 5),
	substr(md5(random()::text), 0, (10 * random())::int + 5)
from generate_series(1, 1000) as s;

insert into exam_results
select
	(999999 * random())::int + 1,
	(999 * random())::int + 1,
	(4 * random())::int + 1
from generate_series(1, 1000000);

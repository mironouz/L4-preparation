-- Add trigger that will update column updated_datetime
-- to current date in case of updating any of student.
create or replace function update_datetime() returns trigger
    language plpgsql
    immutable
as
$$
begin
   new.updated_datetime = now(); 
   return new;
end;
$$;

create or replace trigger update_students_datetime
before update on students
for each row execute procedure update_datetime();
	
explain analyze update students set name='new_name' where student_id = 1;

-- Add validation on DB level that will check username
-- on special characters (reject student name with next characters '@', '#', '$'). 
alter table students drop constraint if exists valid_name;
alter table students add constraint valid_name check(name !~* '[@#$]+');
update students set name='new_name@' where student_id = 1;

-- Create snapshot that will contain next data:
-- student name, student surname, subject name, mark
-- (snapshot means that in case of changing some data in source table –
-- your snapshot should not change). 
copy
(select name, surname, subject_name, mark from students s
join exam_results er on s.student_id=er.student_id
join subjects sub on sub.subject_id=er.subject_id)
to '/tmp/snapshot.csv';

-- Create function that will return average mark for input user.
create or replace function user_avg(integer) returns numeric
    language plpgsql
    immutable
as
$$
begin
    return (select avg(er.mark) from exam_results as er join students as s on er.student_id = s.student_id where s.student_id = $1);
end;
$$;

select user_avg(100);

-- Create function that will return avarage mark for input subject name.
create or replace function subject_avg(text) returns numeric
    language plpgsql
    immutable
as
$$
begin
  return (select avg(er.mark) from exam_results er join subjects s on er.subject_id = s.subject_id where s.subject_name = $1);
end;
$$;

select subject_avg('Confetti and Saw');

-- Create function that will return student at "red zone"
-- (red zone means at least 2 marks <=3). 
create or replace function red_students() returns integer
    language plpgsql
    immutable
as
$$
begin
	return (select count(student_id) from 
		(select s.student_id, sum(er.mark) from students s
		 join exam_results er on er.student_id = s.student_id
		 where er.mark <= 3 group by s.student_id) as red
	where sum >= 2);
end;
$$;

select red_students();

-- Implement immutable data trigger. Create new table student_address.
-- Add several rows with test data and do not give acces to update any information inside it.
-- Hint: you can create trigger that will reject any update operation for target table,
-- but save new row with updated (merged with original) data into separate table.
drop table if exists student_address cascade;
create table student_address(
    student_id int references students(student_id) unique,
    city text not null,
	street text not null,
	building text not null
);

drop table if exists student_address_update_history cascade;
create table student_address_update_history(
    student_id int references students(student_id),
    city text not null,
	street text not null,
	building text not null,
	updated timestamp not null
);

insert into student_address values(1, 'Budapest', 'Szobor', '777');
insert into student_address values(13, 'Debrecen', 'Izabella', '13');

create or replace function update_student_address_immutable() returns trigger
    language plpgsql
as
$$
begin
	if (tg_op = 'UPDATE') then
		insert
			into student_address_update_history(student_id, city, street, building, updated)
			values(new.student_id, new.city, new.street, new.building, now());
	end if;
	return null;
end;
$$;

create or replace trigger update_student_address
before update on student_address
for each row execute procedure update_student_address_immutable();

update student_address set city='New York' where student_id=1;
update student_address set city='Dubai', building=6 where student_id=1;

select * from student_address;
select * from student_address_update_history;

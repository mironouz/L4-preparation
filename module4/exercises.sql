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

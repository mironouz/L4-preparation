drop table if exists students cascade;

create table students(
    student_id serial primary key,
    name varchar(20) not null,
    surname varchar(20) not null,
    date_of_birth date not null,
    phone_number varchar(11) unique check (char_length(phone_number) = 11),
    primary_skill varchar(50),
    created_datetime timestamp not null,
    updated_datetime timestamp not null
);

drop table if exists subjects cascade;

create table subjects(
    subject_id serial primary key,
    subject_name text unique not null,
    tutor varchar(40) not null
);

drop table if exists exam_results;

create table exam_results(
    student_id int references students(student_id),
    subject_id int references subjects(subject_id),
    mark int not null check (mark between 1 and 5)
);

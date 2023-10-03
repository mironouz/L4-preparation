-- Get size and type of indexes
select indexrelname "Index name", pg_size_pretty(pg_relation_size(indexrelid)) "Index size", amname "Index type"
from pg_stat_all_indexes sai
join pg_class c ON sai.indexrelid=c.oid
join pg_am am ON c.relam=am.oid
where sai.relname in ('students', 'subjects', 'exam_results');

-- enable gin/gist indexes defaults
create extension btree_gin;
create extension btree_gist;
create extension pg_trgm;

-- Create custom indexes
create index students_name_btree on students using btree(name);
create index students_name_hash on students using hash(name);
create index students_name_gin on students using gin(name);
create index students_name_gin_trgm on students using gin(name gin_trgm_ops);
create index students_name_gist on students using gist(name);
create index students_name_gist_trgm on students using gist(name gist_trgm_ops);

create index subjects_subject_name_btree on subjects using btree(subject_name);
create index subjects_subject_name_hash on subjects using hash(subject_name);
create index subjects_subject_name_gin on subjects using gin(subject_name);
create index subjects_subject_name_gin_trgm on subjects using gin(subject_name gin_trgm_ops);
create index subjects_subject_name_gist on subjects using gist(subject_name);
create index subjects_subject_name_gist_trgm on subjects using gist(subject_name gist_trgm_ops);

create index exam_results_mark_btree on exam_results using btree(mark);
create index exam_results_mark_hash on exam_results using hash(mark);
create index exam_results_mark_gin on exam_results using gin(mark);
create index exam_results_mark_gist on exam_results using gist(mark);

-- Delete custom indexes
drop index students_name_btree;
drop index students_name_hash;
drop index students_name_gin;
drop index students_name_gin_trgm;
drop index students_name_gist;
drop index students_name_gist_trgm;

drop index subjects_subject_name_btree;
drop index subjects_subject_name_hash;
drop index subjects_subject_name_gin;
drop index subjects_subject_name_gin_trgm;
drop index subjects_subject_name_gist;
drop index subjects_subject_name_gist_trgm;

drop index exam_results_mark_btree;
drop index exam_results_mark_hash;
drop index exam_results_mark_gin;
drop index exam_results_mark_gist;

-- Queries for analysys
explain analyse select * from students where name = 'Mora';
explain analyse select * from students where surname like '%ram%';
explain analyse select * from students where phone_number like '%130%';
explain analyse select distinct s.student_id, name, surname from students s join exam_results er on s.student_id = er.student_id where surname like '%ram%';

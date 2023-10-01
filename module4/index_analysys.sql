select indexrelname "Index name", pg_size_pretty(pg_relation_size(indexrelid)) "Index size", amname "Index type"
from pg_stat_all_indexes sai
join pg_class c ON sai.indexrelid=c.oid
join pg_am am ON c.relam=am.oid
where sai.relname in ('students', 'subjects', 'exam_results')
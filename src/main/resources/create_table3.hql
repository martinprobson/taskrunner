drop table test3;
create table test3 as 
select a.emp_id,
       a.emp_name,
       b.dept_id
FROM   test1 as a
JOIN   test2 as b on a.emp_id = b.emp_id;



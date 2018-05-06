drop table taskrunner_test1_test3;
create table test3 as 
select a.emp_id,
       a.emp_name,
       b.dept_id
FROM   taskrunner_test1_test1 as a
JOIN   taskrunner_test1_test2 as b on a.emp_id = b.emp_id;



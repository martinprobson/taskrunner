---------------------------------------------------------------------------------------
------
------ Martin Robson 12/03/2018
------
------ 12-03-2018 - Initial version.
------ 10-04-2018 - Replace GERNR with EQUNR.
------ 10-04-2018 - Add VKONTO.
------ 10-04-2018 - Parameterize for testing.
------
---------------------------------------------------------------------------------------
set hivevar:measure_date='2018-04-06';
set tez.queue.name=[=queue];
set mapred.job.queue.name=[=queue];
set hive.execution.engine=[=execution_engine];

set ever=[=ever];
set eabl=[=eabl];
set eablg=[=eablg];
set equi=[=equi];
set fkkvkp=[=fkkvkp];
set but000=[=but000];
use [=use_schema];
--
-- New Opts
--
-- Predicate push down - try and filter at the storage level
--
SET hive.optimize.ppd=true;
SET hive.optimize.ppd.storage=true;
--
-- Enable vectorized execution (process rows in blocks of 1024)
--
SET hive.vectorized.execution.enabled=true;
SET hive.vectorized.execution.reduce.enabled = true;
--
-- Use stats
--
SET hive.cbo.enable=true;
SET hive.compute.query.using.stats=true;
SET hive.stats.fetch.column.stats=true;
SET hive.stats.fetch.partition.stats=true;
--
-- Try and limit no of reducers but with a max of 128Mb per reducer
-- Same as DFS blocksize of 128Mb
--
SET hive.tez.auto.reducer.parallelism=true;
SET hive.tez.max.partition.factor=20;
SET hive.exec.reducers.bytes.per.reducer=128000000;
-- New Opts End
set hive.auto.convert.join=true;
set hive.stats.autogather=true;
-- Try with a size of 2Gb
--set hive.auto.convert.join.noconditionaltask.size=2000000000;
--set hive.exec.reducers.max=50; Switch off so that Tez automatically determines number of reducers....
--
-- Extra
--
-- The following needs to be set to get Tez to merge small files......
set hive.merge.tezfiles=true;
set hive.merge.mapredfiles=true;
set hive.merge.mapfiles=true ;
set hive.merge.size.per.task=128000000;
set hive.merge.smallfiles.avgsize=120000000;
set hive.hadoop.supports.splittable.combineinputformat=true;
set hive.exec.compress.output=true;
--
--
--
-----------------------------------------------------------------------------------
-- STEP 1
--
-- Build list of active (gas) contracts with a move in date prior to 09/09/2017.
-----------------------------------------------------------------------------------
DROP TABLE IF EXISTS robsom12_contracts PURGE;
CREATE TABLE IF NOT EXISTS robsom12_contracts (
                        vertrag   string COMMENT 'Contract',
                        vkonto    string COMMENT 'Account',
                        anlage    string COMMENT 'Installation')
		        COMMENT 'Contract candidates'
                        STORED AS ORC TBLPROPERTIES ("orc.compress" = "ZLIB");
INSERT INTO TABLE robsom12_contracts 
    SELECT      vertrag,
                vkonto,
                anlage
    FROM        ${hiveconf:ever}
    WHERE       tech_datestamp = ${hivevar:measure_date}
    AND         tech_type      = 'OPEN'
    AND         einzdat        <= '20170909'
    AND         auszdat        =  '99991231'
    AND         LTRIM(RTRIM(anlage)) <> ""
    AND         sparte         IN ('01','02');
ANALYZE table robsom12_contracts COMPUTE STATISTICS FOR COLUMNS;
ANALYZE table robsom12_contracts COMPUTE STATISTICS ;
-------------------------------------------------------------------------------------
---- STEP 2
--.
-- Get corresponding EABLG entries.
-------------------------------------------------------------------------------------
DROP TABLE IF EXISTS robsom12_eablg PURGE;
CREATE TABLE IF NOT EXISTS robsom12_eablg (
                        anlage    string COMMENT 'Installation',
                        ablbelnr  string COMMENT 'Meter reading doc')
                        STORED AS ORC TBLPROPERTIES ("orc.compress" = "ZLIB");
INSERT INTO TABLE robsom12_eablg
    SELECT      a.anlage,
                a.ablbelnr
    FROM        ${hiveconf:eablg} AS a 
    JOIN        robsom12_contracts  AS b ON b.anlage = a.anlage
    WHERE       a.tech_datestamp = ${hivevar:measure_date}
    AND         a.tech_type      = 'OPEN';
ANALYZE table robsom12_eablg COMPUTE STATISTICS FOR COLUMNS;
ANALYZE table robsom12_eablg COMPUTE STATISTICS ;
-----------------------------------------------------------------------------------
-- STEP 3
--
-- Get meter readings from EABL.
-----------------------------------------------------------------------------------
DROP TABLE IF EXISTS robsom12_eabl PURGE;
CREATE TABLE IF NOT EXISTS robsom12_eabl (
                        ablbelnr  string COMMENT 'Meter Reading doc',
                        equnr     string COMMENT 'Equip No.',
                        zwnummer  string COMMENT 'Register', 
                        v_zwstand string COMMENT 'Meter Reading',
                        adat      string COMMENT 'Meter reading date')
                        STORED AS ORC TBLPROPERTIES ("orc.compress" = "ZLIB");
INSERT INTO TABLE robsom12_eabl
    SELECT      a.ablbelnr,
                a.equnr,
                a.zwnummer,
                a.v_zwstand,
                a.adat
    FROM        ${hiveconf:eabl} AS a 
    JOIN        robsom12_eablg AS b ON b.ablbelnr = a.ablbelnr
    WHERE       a.tech_datestamp = ${hivevar:measure_date}
    AND         a.tech_type      = 'OPEN';
ANALYZE table robsom12_eabl COMPUTE STATISTICS FOR COLUMNS;
ANALYZE table robsom12_eabl COMPUTE STATISTICS ;
---------------------------------------------------------------------------------
-- STEP 4
--
-- Build analysis table.
---------------------------------------------------------------------------------
DROP TABLE IF EXISTS robsom12_meter_reads PURGE;
CREATE TABLE IF NOT EXISTS robsom12_meter_reads (
                        vertrag   string COMMENT 'Contract number',
                        vkonto    string COMMENT 'Account',
                        anlage    string COMMENT 'Installation',
                        ablbelnr  string COMMENT 'Meter Reading doc',
                        equnr     string COMMENT 'Equip No.',
                        zwnummer  smallint    COMMENT 'Register', 
                        v_zwstand string COMMENT 'Meter Reading',
                        adat      date   COMMENT 'Meter reading date')
                        STORED AS ORC TBLPROPERTIES ("orc.compress" = "ZLIB");
INSERT INTO TABLE robsom12_meter_reads
    SELECT      a.vertrag,
                a.vkonto,
                a.anlage,
                b.ablbelnr,
                c.equnr,
                CAST(c.zwnummer AS smallint),
                c.v_zwstand,
                CAST(CONCAT(SUBSTR(c.adat,1,4),'-',SUBSTR(c.adat,5,2),'-',SUBSTR(c.adat,7,2)) as date)
    FROM        robsom12_contracts AS a
    JOIN        robsom12_eablg     AS b ON a.anlage   = b.anlage
    JOIN        robsom12_eabl      AS c ON b.ablbelnr = c.ablbelnr 
    WHERE       c.adat >= '20160101';
ANALYZE table robsom12_meter_reads COMPUTE STATISTICS FOR COLUMNS;
ANALYZE table robsom12_meter_reads COMPUTE STATISTICS ;

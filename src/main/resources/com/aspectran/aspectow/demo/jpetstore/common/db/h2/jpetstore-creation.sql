CREATE SCHEMA IF NOT EXISTS jpetstore;
RUNSCRIPT FROM 'classpath:com/aspectran/aspectow/demo/jpetstore/common/db/h2/jpetstore-schema.sql';
RUNSCRIPT FROM 'classpath:com/aspectran/aspectow/demo/jpetstore/common/db/h2/jpetstore-dataload.sql';

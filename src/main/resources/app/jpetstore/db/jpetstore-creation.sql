CREATE SCHEMA IF NOT EXISTS jpetstore;
RUNSCRIPT FROM 'classpath:app/jpetstore/mybatis/jpetstore-schema.sql';
RUNSCRIPT FROM 'classpath:app/jpetstore/mybatis/jpetstore-dataload.sql';

CREATE SCHEMA IF NOT EXISTS jpetstore;
RUNSCRIPT FROM 'classpath:app/jpetstore/db/jpetstore-schema.sql';
RUNSCRIPT FROM 'classpath:app/jpetstore/db/jpetstore-dataload.sql';

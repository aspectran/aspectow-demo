CREATE SCHEMA IF NOT EXISTS petclinic;
RUNSCRIPT FROM 'classpath:app/petclinic/common/db/h2/petclinic-schema.sql';
RUNSCRIPT FROM 'classpath:app/petclinic/common/db/h2/petclinic-dataload.sql';

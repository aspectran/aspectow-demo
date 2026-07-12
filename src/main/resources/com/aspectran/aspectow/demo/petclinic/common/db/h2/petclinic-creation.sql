CREATE SCHEMA IF NOT EXISTS petclinic;
RUNSCRIPT FROM 'classpath:com/aspectran/aspectow/demo/petclinic/common/db/h2/petclinic-schema.sql';
RUNSCRIPT FROM 'classpath:com/aspectran/aspectow/demo/petclinic/common/db/h2/petclinic-dataload.sql';

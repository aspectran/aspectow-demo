INSERT INTO vets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'James', 'Carter');
INSERT INTO vets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Helen', 'Leary');
INSERT INTO vets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Linda', 'Douglas');
INSERT INTO vets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Henry', 'Stevens');
INSERT INTO vets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (BASE_ENTITY_ID_SEQ.nextval, 'radiology');
INSERT INTO specialties VALUES (BASE_ENTITY_ID_SEQ.nextval, 'surgery');
INSERT INTO specialties VALUES (BASE_ENTITY_ID_SEQ.nextval, 'dentistry');

INSERT INTO vet_specialties VALUES (2, BASE_ENTITY_ID_SEQ.currval - 3 + 1);
INSERT INTO vet_specialties VALUES (3, BASE_ENTITY_ID_SEQ.currval - 3 + 2);
INSERT INTO vet_specialties VALUES (3, BASE_ENTITY_ID_SEQ.currval - 3 + 3);
INSERT INTO vet_specialties VALUES (4, BASE_ENTITY_ID_SEQ.currval - 3 + 2);
INSERT INTO vet_specialties VALUES (5, BASE_ENTITY_ID_SEQ.currval - 3 + 1);

INSERT INTO types VALUES (BASE_ENTITY_ID_SEQ.nextval, 'cat');
INSERT INTO types VALUES (BASE_ENTITY_ID_SEQ.nextval, 'dog');
INSERT INTO types VALUES (BASE_ENTITY_ID_SEQ.nextval, 'lizard');
INSERT INTO types VALUES (BASE_ENTITY_ID_SEQ.nextval, 'snake');
INSERT INTO types VALUES (BASE_ENTITY_ID_SEQ.nextval, 'bird');
INSERT INTO types VALUES (BASE_ENTITY_ID_SEQ.nextval, 'hamster');

INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owners VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Leo', TO_DATE('2010-09-07', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 16 + 1, BASE_ENTITY_ID_SEQ.currval - 10 + 1);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Basil', TO_DATE('2012-08-06', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 17 + 5, BASE_ENTITY_ID_SEQ.currval - 11 + 2);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Rosy', TO_DATE('2011-04-17', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 18 + 2, BASE_ENTITY_ID_SEQ.currval - 12 + 3);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Jewel', TO_DATE('2010-03-07', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 19 + 2, BASE_ENTITY_ID_SEQ.currval - 13 + 3);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Iggy', TO_DATE('2010-11-30', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 20 + 3, BASE_ENTITY_ID_SEQ.currval - 14 + 4);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'George', TO_DATE('2010-01-20', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 21 + 4, BASE_ENTITY_ID_SEQ.currval - 15 + 5);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Samantha', TO_DATE('2012-09-04', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 22 + 1, BASE_ENTITY_ID_SEQ.currval - 16 + 6);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Max', TO_DATE('2012-09-04', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 23 + 1, BASE_ENTITY_ID_SEQ.currval - 17 + 6);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Lucky', TO_DATE('2011-08-06', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 24 + 5, BASE_ENTITY_ID_SEQ.currval - 18 + 7);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Mulligan', TO_DATE('2007-02-24', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 25 + 2, BASE_ENTITY_ID_SEQ.currval - 19 + 8);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Freddy', TO_DATE('2010-03-09', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 26 + 5, BASE_ENTITY_ID_SEQ.currval - 20 + 9);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Lucky', TO_DATE('2010-06-24', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 27 + 2, BASE_ENTITY_ID_SEQ.currval - 21 + 9);
INSERT INTO pets VALUES (BASE_ENTITY_ID_SEQ.nextval, 'Sly', TO_DATE('2012-06-08', 'YYYY-MM-DD'), BASE_ENTITY_ID_SEQ.currval - 28 + 1, BASE_ENTITY_ID_SEQ.currval - 22 + 9);

INSERT INTO visits VALUES (BASE_ENTITY_ID_SEQ.nextval, BASE_ENTITY_ID_SEQ.currval - 13 + 7, TO_DATE('2013-01-01', 'YYYY-MM-DD'), 'rabies shot');
INSERT INTO visits VALUES (BASE_ENTITY_ID_SEQ.nextval, BASE_ENTITY_ID_SEQ.currval - 14 + 8, TO_DATE('2013-01-02', 'YYYY-MM-DD'), 'rabies shot');
INSERT INTO visits VALUES (BASE_ENTITY_ID_SEQ.nextval, BASE_ENTITY_ID_SEQ.currval - 15 + 8, TO_DATE('2013-01-03', 'YYYY-MM-DD'), 'neutered');
INSERT INTO visits VALUES (BASE_ENTITY_ID_SEQ.nextval, BASE_ENTITY_ID_SEQ.currval - 16 + 7, TO_DATE('2013-01-04', 'YYYY-MM-DD'), 'spayed');
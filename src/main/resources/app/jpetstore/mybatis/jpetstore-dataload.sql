--
--    Copyright 2010-2013 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

INSERT IGNORE INTO sequence VALUES ('ordernum',1000);

INSERT IGNORE INTO signon VALUES ('j2ee','j2ee');
INSERT IGNORE INTO signon VALUES ('aspectran','aspectran');

INSERT IGNORE INTO account VALUES ('j2ee','yourname@yourdomain.com','ABC', 'XYX', 'OK', '901 San Antonio Road', 'MS UCUP02-206', 'Palo Alto', 'CA', '94303', 'USA',  '555-555-5555');
INSERT IGNORE INTO account VALUES ('aspectran','aspectran@yourdomain.com','ABC', 'XYX', 'OK', '901 San Antonio Road', 'MS UCUP02-206', 'Palo Alto', 'CA', '94303', 'USA',  '555-555-5555');

INSERT IGNORE INTO profile VALUES ('j2ee','english','DOGS',1,1);
INSERT IGNORE INTO profile VALUES ('aspectran','english','CATS',1,1);

INSERT IGNORE INTO bannerdata VALUES ('FISH','/images/banner_fish.gif');
INSERT IGNORE INTO bannerdata VALUES ('CATS','/images/banner_cats.gif');
INSERT IGNORE INTO bannerdata VALUES ('DOGS','/images/banner_dogs.gif');
INSERT IGNORE INTO bannerdata VALUES ('REPTILES','/images/banner_reptiles.gif');
INSERT IGNORE INTO bannerdata VALUES ('BIRDS','/images/banner_birds.gif');

INSERT IGNORE INTO category VALUES ('FISH','Fish','/images/fish_icon.gif', '<font size="5" color="blue">Fish</font>');
INSERT IGNORE INTO category VALUES ('DOGS','Dogs','/images/dogs_icon.gif', '<font size="5" color="blue">Dogs</font>');
INSERT IGNORE INTO category VALUES ('REPTILES','Reptiles','/images/reptiles_icon.gif', '<font size="5" color="blue">Reptiles</font>');
INSERT IGNORE INTO category VALUES ('CATS','Cats','/images/cats_icon.gif', '<font size="5" color="blue">Cats</font>');
INSERT IGNORE INTO category VALUES ('BIRDS','Birds','/images/birds_icon.gif', '<font size="5" color="blue">Birds</font>');

INSERT IGNORE INTO product VALUES ('FI-SW-01','FISH','Angelfish','/images/fish1.gif', 'Salt Water fish from Australia');
INSERT IGNORE INTO product VALUES ('FI-SW-02','FISH','Tiger Shark','/images/fish4.gif', 'Salt Water fish from Australia');
INSERT IGNORE INTO product VALUES ('FI-FW-01','FISH', 'Koi','/images/fish3.gif', 'Fresh Water fish from Japan');
INSERT IGNORE INTO product VALUES ('FI-FW-02','FISH', 'Goldfish','/images/fish2.gif', 'Fresh Water fish from China');
INSERT IGNORE INTO product VALUES ('K9-BD-01','DOGS','Bulldog','/images/dog2.gif', 'Friendly dog from England');
INSERT IGNORE INTO product VALUES ('K9-PO-02','DOGS','Poodle','/images/dog6.gif', 'Cute dog from France');
INSERT IGNORE INTO product VALUES ('K9-DL-01','DOGS', 'Dalmation','/images/dog5.gif', 'Great dog for a Fire Station');
INSERT IGNORE INTO product VALUES ('K9-RT-01','DOGS', 'Golden Retriever','/images/dog1.gif', 'Great family dog');
INSERT IGNORE INTO product VALUES ('K9-RT-02','DOGS', 'Labrador Retriever','/images/dog5.gif', 'Great hunting dog');
INSERT IGNORE INTO product VALUES ('K9-CW-01','DOGS', 'Chihuahua','/images/dog4.gif', 'Great companion dog');
INSERT IGNORE INTO product VALUES ('RP-SN-01','REPTILES','Rattlesnake','/images/snake1.gif', 'Doubles as a watch dog');
INSERT IGNORE INTO product VALUES ('RP-LI-02','REPTILES','Iguana','/images/lizard1.gif', 'Friendly green friend');
INSERT IGNORE INTO product VALUES ('FL-DSH-01','CATS','Manx','/images/cat2.gif', 'Great for reducing mouse populations');
INSERT IGNORE INTO product VALUES ('FL-DLH-02','CATS','Persian','/images/cat1.gif', 'Friendly house cat, doubles as a princess');
INSERT IGNORE INTO product VALUES ('AV-CB-01','BIRDS','Amazon Parrot','/images/bird2.gif', 'Great companion for up to 75 years');
INSERT IGNORE INTO product VALUES ('AV-SB-02','BIRDS','Finch','/images/bird1.gif', 'Great stress reliever');

INSERT IGNORE INTO supplier VALUES (1,'XYZ Pets','AC','600 Avon Way','','Los Angeles','CA','94024','212-947-0797');
INSERT IGNORE INTO supplier VALUES (2,'ABC Pets','AC','700 Abalone Way','','San Francisco ','CA','94024','415-947-0797');

INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-1','FI-SW-01',16.50,10.00,1,'P','Large');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-2','FI-SW-01',16.50,10.00,1,'P','Small');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-3','FI-SW-02',18.50,12.00,1,'P','Toothless');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-4','FI-FW-01',18.50,12.00,1,'P','Spotted');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-5','FI-FW-01',18.50,12.00,1,'P','Spotless');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-6','K9-BD-01',18.50,12.00,1,'P','Male Adult');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-7','K9-BD-01',18.50,12.00,1,'P','Female Puppy');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-8','K9-PO-02',18.50,12.00,1,'P','Male Puppy');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-9','K9-DL-01',18.50,12.00,1,'P','Spotless Male Puppy');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-10','K9-DL-01',18.50,12.00,1,'P','Spotted Adult Female');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-11','RP-SN-01',18.50,12.00,1,'P','Venomless');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-12','RP-SN-01',18.50,12.00,1,'P','Rattleless');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-13','RP-LI-02',18.50,12.00,1,'P','Green Adult');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-14','FL-DSH-01',58.50,12.00,1,'P','Tailless');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-15','FL-DSH-01',23.50,12.00,1,'P','With tail');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-16','FL-DLH-02',93.50,12.00,1,'P','Adult Female');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-17','FL-DLH-02',93.50,12.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-18','AV-CB-01',193.50,92.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-19','AV-SB-02',15.50, 2.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-20','FI-FW-02',5.50, 2.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-21','FI-FW-02',5.29, 1.00,1,'P','Adult Female');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-22','K9-RT-02',135.50, 100.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-23','K9-RT-02',145.49, 100.00,1,'P','Adult Female');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-24','K9-RT-02',255.50, 92.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-25','K9-RT-02',325.29, 90.00,1,'P','Adult Female');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-26','K9-CW-01',125.50, 92.00,1,'P','Adult Male');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-27','K9-CW-01',155.29, 90.00,1,'P','Adult Female');
INSERT IGNORE INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-28','K9-RT-01',155.29, 90.00,1,'P','Adult Female');

INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-1',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-2',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-3',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-4',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-5',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-6',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-7',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-8',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-9',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-10',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-11',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-12',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-13',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-14',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-15',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-16',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-17',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-18',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-19',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-20',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-21',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-22',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-23',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-24',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-25',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-26',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-27',10000);
INSERT IGNORE INTO inventory (itemid, qty) VALUES ('EST-28',10000);

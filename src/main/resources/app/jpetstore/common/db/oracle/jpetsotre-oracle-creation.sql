create table supplier (
    suppid int not null,
    name varchar2(80) null,
    status varchar2(2) not null,
    addr1 varchar2(80) null,
    addr2 varchar2(80) null,
    city varchar2(80) null,
    state varchar2(80) null,
    zip varchar2(5) null,
    phone varchar2(80) null,
    constraint pk_supplier primary key (suppid)
);

create table signon (
    username varchar2(25) not null,
    password varchar2(25) not null,
    constraint pk_signon primary key (username)
);

create table account (
    userid varchar2(80) not null,
    email varchar2(80) not null,
    firstname varchar2(80) not null,
    lastname varchar2(80) not null,
    status varchar2(2) null,
    addr1 varchar2(80) not null,
    addr2 varchar2(40) null,
    city varchar2(80) not  null,
    state varchar2(80) not null,
    zip varchar2(20) not null,
    country varchar2(30) not null,
    phone varchar2(80) not null,
    constraint pk_account primary key (userid)
);

create table profile (
    userid varchar2(80) not null,
    langpref varchar2(80) not null,
    favcategory varchar2(30),
    mylistopt NUMBER(5),
    banneropt NUMBER(5),
    constraint pk_profile primary key (userid)
);

create table bannerdata (
    favcategory varchar2(80) not null,
    bannername varchar2(255) null,
    constraint pk_bannerdata primary key (favcategory)
);

create table orders (
    orderid int not null,
    userid varchar2(80) not null,
    orderdate DATE not null,
    shipaddr1 varchar2(80) not null,
    shipaddr2 varchar2(80) null,
    shipcity varchar2(80) not null,
    shipstate varchar2(80) not null,
    shipzip varchar2(20) not null,
    shipcountry varchar2(30) not null,
    billaddr1 varchar2(80) not null,
    billaddr2 varchar2(80)  null,
    billcity varchar2(80) not null,
    billstate varchar2(80) not null,
    billzip varchar2(20) not null,
    billcountry varchar2(30) not null,
    courier varchar2(80) not null,
    totalprice decimal(10,2) not null,
    billtofirstname varchar2(80) not null,
    billtolastname varchar2(80) not null,
    shiptofirstname varchar2(80) not null,
    shiptolastname varchar2(80) not null,
    creditcard varchar2(80) not null,
    exprdate varchar2(7) not null,
    cardtype varchar2(80) not null,
    locale varchar2(80) not null,
    constraint pk_orders primary key (orderid)
);

create index ordersByUser on orders (userid, orderdate desc);

create table orderstatus (
    orderid NUMBER(15) not null,
    linenum NUMBER(5) not null,
    timestamp DATE not null,
    status varchar2(2) not null,
    constraint pk_orderstatus primary key (orderid, linenum)
);

create table lineitem (
    orderid NUMBER(15) not null,
    linenum NUMBER(5) not null,
    itemid varchar2(10) not null,
    quantity int not null,
    unitprice decimal(10,2) not null,
    constraint pk_lineitem primary key (orderid, linenum)
);

create table category (
    catid varchar2(10) not null,
    name varchar2(80) null,
    image varchar2(128) null,
    descn varchar2(255) null,
    constraint pk_category primary key (catid)
);

create table product (
    productid varchar2(10) not null,
    category varchar2(10) not null,
    name varchar2(80) null,
    image varchar2(128) null,
    descn varchar2(255) null,
    constraint pk_product primary key (productid),
    constraint fk_product_1 foreign key (category) references category (catid)
);

create index productCat on product (category);
create index productName on product (name);

create table item (
    itemid varchar2(10) not null,
    productid varchar2(10) not null,
    listprice decimal(10,2) null,
    unitcost decimal(10,2) null,
    supplier NUMBER(15) null,
    status varchar2(2) null,
    attr1 varchar2(80) null,
    attr2 varchar2(80) null,
    attr3 varchar2(80) null,
    attr4 varchar2(80) null,
    attr5 varchar2(80) null,
    constraint pk_item primary key (itemid),
    constraint fk_item_1 foreign key (productid) references product (productid),
    constraint fk_item_2 foreign key (supplier) references supplier (suppid)
);

create index itemProd on item (productid);

create table inventory (
    itemid varchar2(10) not null,
    qty NUMBER(5) not null,
    constraint pk_inventory primary key (itemid)
);

create table sequence (
    name varchar2(30) not null,
    nextid NUMBER(15) not null,
    constraint pk_sequence primary key (name)
);


INSERT INTO sequence VALUES ('ordernum',1000);

INSERT INTO signon VALUES ('j2ee','j2ee');
INSERT INTO signon VALUES ('aspectran','aspectran');

INSERT INTO account VALUES ('j2ee','yourname@yourdomain.com','ABC', 'XYX', 'OK', '901 San Antonio Road', 'MS UCUP02-206', 'Palo Alto', 'CA', '94303', 'USA',  '555-555-5555');
INSERT INTO account VALUES ('aspectran','aspectran@yourdomain.com','ABC', 'XYX', 'OK', '901 San Antonio Road', 'MS UCUP02-206', 'Palo Alto', 'CA', '94303', 'USA',  '555-555-5555');

INSERT INTO profile VALUES ('j2ee','english','DOGS',1,1);
INSERT INTO profile VALUES ('aspectran','english','CATS',1,1);

INSERT INTO bannerdata VALUES ('FISH','/images/banner_fish.gif');
INSERT INTO bannerdata VALUES ('CATS','/images/banner_cats.gif');
INSERT INTO bannerdata VALUES ('DOGS','/images/banner_dogs.gif');
INSERT INTO bannerdata VALUES ('REPTILES','/images/banner_reptiles.gif');
INSERT INTO bannerdata VALUES ('BIRDS','/images/banner_birds.gif');

INSERT INTO category VALUES ('FISH','Fish','/images/fish_icon.gif', '<font size="5" color="blue">Fish</font>');
INSERT INTO category VALUES ('DOGS','Dogs','/images/dogs_icon.gif', '<font size="5" color="blue">Dogs</font>');
INSERT INTO category VALUES ('REPTILES','Reptiles','/images/reptiles_icon.gif', '<font size="5" color="blue">Reptiles</font>');
INSERT INTO category VALUES ('CATS','Cats','/images/cats_icon.gif', '<font size="5" color="blue">Cats</font>');
INSERT INTO category VALUES ('BIRDS','Birds','/images/birds_icon.gif', '<font size="5" color="blue">Birds</font>');

INSERT INTO product VALUES ('FI-SW-01','FISH','Angelfish','/images/fish1.gif', 'Salt Water fish from Australia');
INSERT INTO product VALUES ('FI-SW-02','FISH','Tiger Shark','/images/fish4.gif', 'Salt Water fish from Australia');
INSERT INTO product VALUES ('FI-FW-01','FISH', 'Koi','/images/fish3.gif', 'Fresh Water fish from Japan');
INSERT INTO product VALUES ('FI-FW-02','FISH', 'Goldfish','/images/fish2.gif', 'Fresh Water fish from China');
INSERT INTO product VALUES ('K9-BD-01','DOGS','Bulldog','/images/dog2.gif', 'Friendly dog from England');
INSERT INTO product VALUES ('K9-PO-02','DOGS','Poodle','/images/dog6.gif', 'Cute dog from France');
INSERT INTO product VALUES ('K9-DL-01','DOGS', 'Dalmation','/images/dog5.gif', 'Great dog for a Fire Station');
INSERT INTO product VALUES ('K9-RT-01','DOGS', 'Golden Retriever','/images/dog1.gif', 'Great family dog');
INSERT INTO product VALUES ('K9-RT-02','DOGS', 'Labrador Retriever','/images/dog5.gif', 'Great hunting dog');
INSERT INTO product VALUES ('K9-CW-01','DOGS', 'Chihuahua','/images/dog4.gif', 'Great companion dog');
INSERT INTO product VALUES ('RP-SN-01','REPTILES','Rattlesnake','/images/snake1.gif', 'Doubles as a watch dog');
INSERT INTO product VALUES ('RP-LI-02','REPTILES','Iguana','/images/lizard1.gif', 'Friendly green friend');
INSERT INTO product VALUES ('FL-DSH-01','CATS','Manx','/images/cat2.gif', 'Great for reducing mouse populations');
INSERT INTO product VALUES ('FL-DLH-02','CATS','Persian','/images/cat1.gif', 'Friendly house cat, doubles as a princess');
INSERT INTO product VALUES ('AV-CB-01','BIRDS','Amazon Parrot','/images/bird2.gif', 'Great companion for up to 75 years');
INSERT INTO product VALUES ('AV-SB-02','BIRDS','Finch','/images/bird1.gif', 'Great stress reliever');

INSERT INTO supplier VALUES (1,'XYZ Pets','AC','600 Avon Way','','Los Angeles','CA','94024','212-947-0797');
INSERT INTO supplier VALUES (2,'ABC Pets','AC','700 Abalone Way','','San Francisco ','CA','94024','415-947-0797');

INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-1','FI-SW-01',16.50,10.00,1,'P','Large');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-2','FI-SW-01',16.50,10.00,1,'P','Small');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-3','FI-SW-02',18.50,12.00,1,'P','Toothless');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-4','FI-FW-01',18.50,12.00,1,'P','Spotted');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-5','FI-FW-01',18.50,12.00,1,'P','Spotless');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-6','K9-BD-01',18.50,12.00,1,'P','Male Adult');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-7','K9-BD-01',18.50,12.00,1,'P','Female Puppy');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-8','K9-PO-02',18.50,12.00,1,'P','Male Puppy');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-9','K9-DL-01',18.50,12.00,1,'P','Spotless Male Puppy');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-10','K9-DL-01',18.50,12.00,1,'P','Spotted Adult Female');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-11','RP-SN-01',18.50,12.00,1,'P','Venomless');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-12','RP-SN-01',18.50,12.00,1,'P','Rattleless');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-13','RP-LI-02',18.50,12.00,1,'P','Green Adult');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-14','FL-DSH-01',58.50,12.00,1,'P','Tailless');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-15','FL-DSH-01',23.50,12.00,1,'P','With tail');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-16','FL-DLH-02',93.50,12.00,1,'P','Adult Female');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-17','FL-DLH-02',93.50,12.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-18','AV-CB-01',193.50,92.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-19','AV-SB-02',15.50, 2.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-20','FI-FW-02',5.50, 2.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-21','FI-FW-02',5.29, 1.00,1,'P','Adult Female');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-22','K9-RT-02',135.50, 100.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-23','K9-RT-02',145.49, 100.00,1,'P','Adult Female');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-24','K9-RT-02',255.50, 92.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-25','K9-RT-02',325.29, 90.00,1,'P','Adult Female');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-26','K9-CW-01',125.50, 92.00,1,'P','Adult Male');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-27','K9-CW-01',155.29, 90.00,1,'P','Adult Female');
INSERT INTO item (itemid, productid, listprice, unitcost, supplier, status, attr1) VALUES ('EST-28','K9-RT-01',155.29, 90.00,1,'P','Adult Female');

INSERT INTO inventory (itemid, qty) VALUES ('EST-1',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-2',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-3',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-4',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-5',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-6',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-7',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-8',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-9',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-10',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-11',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-12',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-13',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-14',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-15',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-16',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-17',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-18',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-19',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-20',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-21',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-22',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-23',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-24',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-25',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-26',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-27',10000);
INSERT INTO inventory (itemid, qty) VALUES ('EST-28',10000);

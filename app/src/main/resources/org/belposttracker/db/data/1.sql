# Patch file. Each line - one complete statement. Do not split lines! Comments allowed with # as first char.

CREATE TABLE parcel(_id INTEGER PRIMARY KEY, number TEXT NOT NULL, name TEXT NOT NULL, updatable INTEGER NOT NULL, last_update_date INTEGER NOT NULL)

INSERT INTO parcel(number,name,updatable,last_update_date) VALUES('RL050023653CN','джинсы',1,1421225428823)
INSERT INTO parcel(number,name,updatable,last_update_date) VALUES('RK229377791CN','сумка',1,1421225428823)
INSERT INTO parcel(number,name,updatable,last_update_date) VALUES('TT','вымышленный трек',1,1421225428823)
INSERT INTO parcel(number,name,updatable,last_update_date) VALUES('RH076382256CN','мозаика',1,1421225428823)



CREATE INDEX parcel_number ON parcel(number)

CREATE TABLE status(_id INTEGER PRIMARY KEY, parcel_id INTEGER NOT NULL, _date TEXT NOT NULL, description TEXT NOT NULL)

INSERT INTO status(parcel_id,_date,description) VALUES(1,'14.01.2015 11:50:29','Отправлено')
INSERT INTO status(parcel_id,_date,description) VALUES(2,'14.01.2015 11:50:29','Нет данных')
INSERT INTO status(parcel_id,_date,description) VALUES(3,'14.01.2015 11:50:29','Нет данных')
INSERT INTO status(parcel_id,_date,description) VALUES(4,'14.01.2015 11:50:29','Нет данных')


CREATE INDEX status_parcel_id ON status(parcel_id)


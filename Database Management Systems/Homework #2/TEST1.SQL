SELECT * FROM TELEPHONE;
SELECT * FROM CELL;

INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (1, sysdate, '333000010', 3, 3, 'O');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (2, sysdate, '333000009', 15, 15, 'O');

SELECT * FROM TELEPHONE;
SELECT * FROM CELL;

INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (3, sysdate, '333000009', 15, 15, 'F');

SELECT * FROM TELEPHONE;
SELECT * FROM CELL;

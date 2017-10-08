SELECT * FROM TELEPHONE;
SELECT * FROM CELL;

INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (4, sysdate, '333000001', 3, 3, 'O');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (6, sysdate, '333000004', 5, 5, 'O');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (7, sysdate, '333000004', 5, 5, 'C');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (8, sysdate, '333000001', 3, 3, 'C');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (9, sysdate, '333000010', 3, 3, 'C');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (10, sysdate, '333000020', 4, 4, 'O');
INSERT INTO STATE_CHANGE(ChangeId, TimeStamp, PhoneNo, x, y, ChangeType) values (11, sysdate, '333000020', 4, 4, 'C');

SELECT * FROM TELEPHONE;
SELECT * FROM CELL;

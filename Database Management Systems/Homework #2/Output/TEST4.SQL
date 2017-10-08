SELECT * FROM CELL;

UPDATE CELL SET MaxCalls = MaxCalls+30; -- Incrementing by 30 so that the initial constraint is respected

SELECT * FROM CELL;

UPDATE CELL SET MaxCalls = MaxCalls-1;
UPDATE CELL SET MaxCalls = MaxCalls-10;

SELECT * FROM CELL;

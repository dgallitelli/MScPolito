CREATE OR REPLACE TRIGGER PhoneStateChange
-- we want to generate modify (generate or delete) entries in TELEPHONE table and CELL table.
-- because we want to do so AFTER the state change happens
-- the trigger will be an AFTER TRIGGER
AFTER INSERT ON STATE_CHANGE
-- while the granularity is ROW-LEVEL TRIGGER, beacuse we work on the specific rows.
FOR EACH ROW
DECLARE
  -- variable to increment or decrement the number of phones in the targeted cell
  x NUMBER;

BEGIN
  IF (:NEW.CHANGETYPE = 'O') THEN
    -- Branch for phone switching ON
    INSERT INTO TELEPHONE(PHONENO,X,Y,PHONESTATE) VALUES (:NEW.PHONENO,:NEW.X,:NEW.Y,'ON');
    x := 1;
  ELSE
    IF (:NEW.CHANGETYPE = 'F') THEN
        -- Branch for phone switching OFF
        DELETE FROM TELEPHONE WHERE PHONENO=:NEW.PHONENO;
        x := -1;
    END IF;
  END IF;

  UPDATE CELL
  SET CURRENTPHONE# = CURRENTPHONE# + x
  WHERE (:NEW.x BETWEEN X0 AND X1) AND (:NEW.y BETWEEN Y0 AND Y1);

END;

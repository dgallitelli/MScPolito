CREATE OR REPLACE TRIGGER PhoneStateChange
AFTER INSERT ON STATE_CHANGE
FOR EACH ROW
DECLARE
  x NUMBER; active# NUMBER; excounter NUMBER; myCellID NUMBER; myCellMaxCalls NUMBER;
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
    ELSE
      IF (:NEW.CHANGETYPE = 'C') THEN
        -- Branch for phone wishing to CALL

        x := 0; -- We do not have more or less phones in the cell. We will not update CURRENTPHONE#
        -- We get the CELLID and the number of max calls of the cell to which the changing telephone is connected
        SELECT CELLID,MAXCALLS INTO myCellID,myCellMaxCalls  FROM CELL WHERE (:NEW.x BETWEEN X0 AND X1) AND (:NEW.y BETWEEN Y0 AND Y1);

        -- We count the number of phones calling from the same cell (PHONESTATE='Active')
        SELECT COUNT(*) INTO active#
        FROM TELEPHONE, CELL
        WHERE PHONESTATE='Active' AND (X BETWEEN X0 AND X1) AND (Y BETWEEN Y0 AND Y1) AND CELLID = myCellID;

        -- If the number of active phones is less then the maximum allowed by the cell
        IF (active# < myCellMaxCalls) THEN
          -- Then we can update the state of the telephone that is changing state
          UPDATE TELEPHONE SET PHONESTATE='Active' WHERE PHONENO=:NEW.PHONENO;
        ELSE
          -- Otherwise we generate an exception
          SELECT COUNT(EXID) INTO excounter FROM EXCEPTION_LOG;
          INSERT INTO EXCEPTION_LOG VALUES (excounter, myCellID, 'F'); -- we use excounter here because records go from 0 to n-1, we count n elements
        END IF;
      END IF;
    END IF;
  END IF;

  UPDATE CELL
  SET CURRENTPHONE# = CURRENTPHONE# + x
  WHERE ((:NEW.x BETWEEN X0 AND X1) AND (:NEW.y BETWEEN Y0 AND Y1));

END;

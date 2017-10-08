CREATE OR REPLACE TRIGGER maxCallsMgmt
-- The objective of this trigger is to manage the reduction of maxCalls.
-- The number of MaxCalls has to be at least equal to the number of active phones.
-- That is why, BEFORE UPDATING the MAXCALLS from CELL table, we check ALL RECORDS for ACTIVE phones.
BEFORE UPDATE OF MAXCALLS ON CELL
FOR EACH ROW
DECLARE
  -- Vars
  active# NUMBER;
BEGIN
  -- Trigger body

  SELECT COUNT(*) INTO active#
  FROM TELEPHONE
  WHERE PHONESTATE='Active' AND (X BETWEEN :NEW.X0 AND :NEW.X1) AND (Y BETWEEN :NEW.Y0 AND :NEW.Y1);

  IF (active# > :NEW.MAXCALLS) THEN :NEW.MAXCALLS := active#;
  END IF;

END;

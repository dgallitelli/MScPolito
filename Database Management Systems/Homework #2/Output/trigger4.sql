CREATE OR REPLACE TRIGGER maxCallsMgmt
-- The objective of this trigger is to manage the reduction of maxCalls.
-- A minimum of 30 calls should always be guaranteed.
-- That is why BEFORE the update we check if the new value to be written is lower than 30
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

  IF (active# > :NEW.MAXCALLS) THEN
    :NEW.MAXCALLS := active#;
  END IF;

  IF (:NEW.MAXCALLS < 30) THEN
    raise_application_error(-20000,'Service of 30 minimum calls has to be guaranteed.',TRUE);
  END IF;

END;

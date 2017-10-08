forfiles /p ".\input" /m "Co_30_1_NT_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_30_1_T_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_30_20_NT_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_30_20_T_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"

forfiles /p ".\input" /m "Co_100_1_NT_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_100_1_T_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_100_20_NT_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_100_20_T_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"

forfiles /p ".\input" /m "Co_300_20_NT_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"
forfiles /p ".\input" /m "Co_300_20_T_*.txt" /c "cmd /c java -jar ^0x22%cd%\Assignment.jar^0x22 -i @path -o ^0x22%cd%\output\summary.csv^0x22 -s ^0x22%cd%\output\Sol_@fname.csv^0x22"

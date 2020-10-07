@echo off

set ARGS= fgl

:setupArgs
if ""%1""=="""" goto doneStart
set ARGS=%ARGS% "%1"
shift
goto setupArgs

:doneStart

java -server -Xms1G -Xmx1G -cp lib\DataStructures.jar data_structures.tests.UnitTestRunner %ARGS%

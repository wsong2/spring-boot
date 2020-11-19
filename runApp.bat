@echo off

setlocal

set CLASSPATH=.\bin;lib\*
java -cp %CLASSPATH% swx.springboot.App
REM java -Dfile.encoding=GBK -cp %CLASSPATH% swx.springboot.App
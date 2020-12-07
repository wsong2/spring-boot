@echo off

setlocal

set CLASSPATH=.\bin;lib\*
java -cp %CLASSPATH% swx.springboot.App --spring.profiles.active=development

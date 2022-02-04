@echo off

setlocal

set CLASSPATH=.\bin;lib\*;lib\ulib\*
java -cp %CLASSPATH% swx.springboot.App --spring.profiles.active=development

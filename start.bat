@echo off
set path=C:\Program Files\Java\jdk1.8.0_45\jre\bin
START "plmservice" "%path%\javaw" -jar plmservice.war
echo 启动 plmservice......
pause
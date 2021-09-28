@echo off
wmic  process where (commandline LIKE "%%plmservice%%" and caption="javaw.exe") delete
echo 关闭进程结束
pause
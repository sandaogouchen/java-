@echo off
set CLASSPATH=.;src;bin
set LIB_PATH=lib\sys-libs

for %%f in ("%LIB_PATH%\*.jar") do set CLASSPATH=!CLASSPATH!;%%f

javac -cp "%CLASSPATH%" -d bin src\com\ascent\util\*.java
java -cp "%CLASSPATH%;bin" com.ascent.MainApplication

pause

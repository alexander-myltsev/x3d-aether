@echo off
echo Starting the jWebSocket Server...

rem if JWEBSOCKET_HOME not set try to create a temporary one
if not "%JWEBSOCKET_HOME%"=="" goto start
pushd ..
set JWEBSOCKET_HOME=%cd%
popd

:start
rem Allowed options:
rem logtarget=[console|rollingfile|singlefile]
rem loglevel=[debug|info|error|warn]

java -jar ..\libs\jWebSocketServer-0.10.jar logtarget=console loglevel=info %1 %2 %3 %4 %5 %6 %7 %8 %9

rem pause

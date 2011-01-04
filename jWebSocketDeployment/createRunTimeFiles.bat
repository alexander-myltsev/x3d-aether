@echo off

if "%1"=="/y" goto dontAsk1
echo This will create the runtime files for jWebSocket v%JWEBSOCKET_VER%. Are you sure?
pause
:dontAsk1

set ver=%JWEBSOCKET_VER%

pushd jWebSocket-%ver%

rem Create Windows Executables to RTE bin folder
exe4jc jWebSocketServer32.exe4j
exe4jc jWebSocketServer64.exe4j
exe4jc jWebSocketService32.exe4j
exe4jc jWebSocketService64.exe4j
exe4jc jWebSocketAdmin32.exe4j
exe4jc jWebSocketAdmin64.exe4j

popd

if "%1"=="/y" goto dontAsk2
pause
:dontAsk2

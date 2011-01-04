@echo off

if "%1"=="/y" goto dontAsk1
echo This will create the download files for jWebSocket v%JWEBSOCKET_VER%. Are you sure?
pause
:dontAsk1

set ver=%JWEBSOCKET_VER%
set src=..\

set rte=..\jWebSocketRTE\jWebSocket-%ver%\
set bin=%rte%bin\
set conf=%rte%conf\
set libs=%rte%libs\
set logs=%rte%logs\

set depl=..\jWebSocketDeployment\jWebSocket-%ver%\
set down=..\jWebSocketDownloads\jWebSocket-%ver%\

rem goto package


rem --- jWebSocket full sources
set dest=%down%jWebSocketFullSources-%ver%.zip
if exist %dest% del %dest%

7z u -mx9 -tzip "%dest%" "%src%jWebSocketCommon" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketServerAPI" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketServer" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketAppServer" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketAppSrvDemo" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketSamples" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketClientAPI" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketJavaSEClient" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketSwingGUI" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%jWebSocketClient" -xr!target -xr!devguide -xr!quickguide -xr!javadocs -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%shared" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%libs" -xr!target -xr!.svn
7z u -mx9 -tzip "%dest%" "%src%pom.xml

7z u -mx9 -tzip "%dest%" "%src%jWebSocketAndroid" -xr!target -xr!.svn

7z u -mx9 -tzip "%dest%" "%src%jWebSocketRTE" -xr!.svn -xr!jWebSocket*.jar -xr!jWebSocket*.war -xr!jWebSocket-0.9.0 -xr!jWebSocket-0.9.5 -xr!*.exe -xr!*.log
7z u -mx9 -tzip "%dest%" "%src%jWebSocketDeployment" -xr!.svn -xr!jWebSocket-0.7.0 -xr!jWebSocket-0.8.0 -xr!jWebSocket-0.8.5 -xr!jWebSocket-0.9.0 -xr!jWebSocket-0.9.5 -xr!*.tmp*

rem goto end


:server

rem ---  jWebSocket Server 
set tempDir=%down%jWebSocket-%ver%\
set dest=%down%jWebSocketServer-%ver%.zip
if exist %dest% del %dest%

if not exist "%tempdir%" md "%tempdir%"
if not exist "%tempdir%\logs" md "%tempdir%\logs"
if not exist "%tempdir%\bin" md "%tempdir%\bin"

xcopy %bin%jWebSocketServer.bat %tempdir%bin\ /s /i /y
xcopy %bin%jWebSocketServer.sh %tempdir%bin\ /s /i /y
xcopy %bin%jWebSocketAdmin.bat %tempdir%bin\ /s /i /y
xcopy %bin%jWebSocketAdmin.sh %tempdir%bin\ /s /i /y
xcopy %libs%commons-lang-2.5.jar %tempdir%libs\ /s /i /y
xcopy %libs%commons-io-1.4.jar %tempdir%libs\ /s /i /y
xcopy %libs%commons-codec-1.4.jar %tempdir%libs\ /s /i /y
xcopy %libs%log4j-1.2.15.jar %tempdir%libs\ /s /i /y
xcopy %libs%javolution-5.5.1.jar %tempdir%libs\ /s /i /y
xcopy %libs%netty-3.2.0.BETA1.jar %tempdir%libs\ /s /i /y
xcopy %libs%mysql-connector-java-5.1.13-bin.jar %tempdir%libs\ /s /i /y
xcopy %libs%servlet-api-2.5-6.1.14.jar %tempdir%libs\ /s /i /y
xcopy %libs%json-2-RELEASE65.jar %tempdir%libs\ /s /i /y
xcopy %libs%slf4j-api-1.5.10.jar %tempdir%libs\ /s /i /y
xcopy %libs%slf4j-jdk14-1.5.10.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketCommon-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketServerAPI-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketServer-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketClientAPI-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketJavaSEClient-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketSwingGUI-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %libs%jWebSocketSamples-%ver%.jar %tempdir%libs\ /s /i /y
xcopy %conf%jWebSocket.xml %tempdir%conf\ /s /i /y

7z u -mx9 -tzip -r  "%dest%" %tempdir%

rd %tempdir% /q/s

rem goto end


:serverbundle

set dest=%down%jWebSocketServer-Bundle-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -r -tzip "%dest%" "%libs%jWebSocketServer-Bundle-%ver%.jar"
7z u -mx9 -r -tzip "%dest%" "%depl%ReadMe_ServerBundle.txt"
7z u -mx9 -r -tzip "%dest%" "%conf%jWebSocket.xml"

rem goto end


:winexe

rem --- jWebSocket Windows executable (32bit)
set dest=%down%jWebSocketServer32-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketServer32.exe"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketAdmin32.exe"
7z u -mx9 -tzip "%dest%" "%depl%ReadMe_Server32.txt"

rem --- jWebSocket Windows executable (64bit)
set dest=%down%jWebSocketServer64-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketServer64.exe"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketAdmin64.exe"
7z u -mx9 -tzip "%dest%" "%depl%ReadMe_Server64.txt"

rem goto end


:winservice

rem --- jWebSocket Windows service  (32bit)
set dest=%down%jWebSocketService32-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketService32.exe"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketInstallService32.bat"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketUninstallService32.bat"
7z u -mx9 -tzip "%dest%" "%depl%ReadMe_Service32.txt"

rem ---  jWebSocket Windows service (64bit)
set dest=%down%jWebSocketService64-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketService64.exe"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketInstallService64.bat"
7z u -mx9 -tzip "%dest%" "%bin%jWebSocketUninstallService64.bat"
7z u -mx9 -tzip "%dest%" "%depl%ReadMe_Service64.txt"

rem goto end


:client

rem --- jWebSocket Client (website as zip archive)
echo on
pushd ..\jWebSocketClient
move web jWebSocketClient-%ver%
popd
set src=..\jWebSocketClient\
set dest=%down%jWebSocketClient-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -r -tzip "%dest%" "%src%*.*" -xr!.svn -xr!quickguide -xr!devguide -xr!javadocs -xr!target -xr!jsdoc
pushd ..\jWebSocketClient
move jWebSocketClient-%ver% web
popd

rem goto end


:appserver

set dest=%down%jWebSocketAppServer-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -r -tzip "%dest%" "%libs%jWebSocketAppServer-%ver%.war"
7z u -mx9 -r -tzip "%dest%" "%libs%jWebSocketSamples-%ver%.jar"
7z u -mx9 -r -tzip "%dest%" "%depl%ReadMe_AppServer.txt"

set dest=%down%jWebSocketAppSrvDemo-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -r -tzip "%dest%" "%libs%jWebSocketAppSrvDemo-%ver%.war"
7z u -mx9 -r -tzip "%dest%" "%libs%jWebSocketSamples-%ver%.jar"
7z u -mx9 -r -tzip "%dest%" "%depl%ReadMe_AppSrvDemo.txt"

rem goto end


:android_demo

set android_base=..\jWebSocketAndroid\
set android_demo=%android_base%jWebSocketAndroidDemo\
set dest=%down%jWebSocketAndroidDemo-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -r -tzip "%dest%" "%android_demo%dist\jWebSocketAndroidDemo.apk"
7z u -mx9 -r -tzip "%dest%" "%depl%ReadMe_Android.txt"


:package
set dest=%down%jWebSocket-%ver%.zip
if exist "%dest%" del "%dest%"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketAndroidDemo-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketAppServer-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketAppSrvDemo-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketClient-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketFullSources-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketServer-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketServer32-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketServer64-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketServer-Bundle-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketService32-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%down%jWebSocketService64-0.10.zip"
7z u -mx9 -r -tzip "%dest%" "%depl%ReadMe_jWebSocket.txt"


:end

if "%1"=="/y" goto dontAsk2
pause
:dontAsk2

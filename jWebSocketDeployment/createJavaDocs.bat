@echo off
if "%1"=="/y" goto dontAsk1
echo Auto Generation of jWebSocket v%JWEBSOCKET_VER% Java Docs, are you sure?
pause
:dontAsk1

rem set log=..\jWebSocketDeployment\createJavaDocs.log
set log=con

cd /d ..\jWebSocketToolkit
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketTCPEngine
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketNettyEngine
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketCore
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketCustomServer
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketTokenServer
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketPlugins
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketAdmin
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketSharedObjects
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketCluster
call mvn generate-sources javadoc:javadoc >> %log%

cd /d ..\jWebSocketFactory
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketServer
call mvn generate-sources javadoc:javadoc >> %log%

cd /d ..\jWebSocketSamples
call mvn generate-sources javadoc:javadoc >> %log%

cd /d ..\jWebSocketWebAppSupport
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketAppServer
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketAppSrvDemo
call mvn generate-sources javadoc:javadoc >> %log%

cd /d ..\jWebSocketJavaClient
call mvn generate-sources javadoc:javadoc >> %log%
cd /d ..\jWebSocketSwingGUI
call mvn generate-sources javadoc:javadoc >> %log%

rem switch back to deployment folder
cd ..\jWebSocketDeployment

echo finished! Please check if JavaDocs have been created.
if "%1"=="/y" goto dontAsk2
pause
:dontAsk2
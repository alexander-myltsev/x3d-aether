if [ "$JWEBSOCKET_HOME" == "" ]; then
 pushd ..
 JWEBSOCKET_HOME=`pwd`;
 export JWEBSOCKET_HOME
 popd
fi

java -jar ..\libs\jWebSocketServer-0.10.jar logtarget=console loglevel=info %1 %2 %3 %4 %5 %6 %7 %8 %9
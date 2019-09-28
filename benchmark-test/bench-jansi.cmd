REM SETLOCAL EnableDelayedExpansion

REM REM dummy hack equivalent to $() under cmd? .. cf https://stackoverflow.com/questions/2768608/batch-equivalent-of-bash-backticks
REM ???
REM mvn -q -DforceStdout help:evaluate -Dexpression=project.version > tmpPomVersion.txt
REM set /p pomVersion=<tmpPomVersion.txt
REM del /f tmpPomVersion.txt
REM 
REM mvn -q -DforceStdout help:evaluate -Dexpression=jansi.version > tmpJansiVersion.txt
REM set /p jansiVersion=<tmpJansiVersion.txt
REM del /f tmpJansiVersion.txt

set pomVersion=1.0.0-SNAPSHOT
set jansiVersion=1.18-SNAPSHOT
echo using pomVersion: %pomVersion%, jansiVersion:%jansiVersion%

set JVM_ARGS=
REM set JVM_ARGS=%JVM_ARGS% -Dcom.sun.management.jmxremote
REM set JVM_ARGS=%JVM_ARGS% -Dcom.sun.management.jmxremote.port=7091
REM set JVM_ARGS=%JVM_ARGS% -Dcom.sun.management.jmxremote.authenticate=false
REM set JVM_ARGS=%JVM_ARGS% -Dcom.sun.management.jmxremote.ssl=false
REM set JVM_ARGS=%JVM_ARGS% -XX:+UnlockCommercialFeatures -XX:+FlightRecorder

REM set JVM_ARGS=%JVM_ARGS% -Djansi.passthrough=true

set CLASSPATH=target/benchmark-jansi-%pomVersion%.jar;target/dependency/jansi-%jansiVersion%.jar

echo ... java %JVM_ARGS% -cp "%CLASSPATH%" org.fusesource.jansi.BenchmarkMain
java %JVM_ARGS% -cp "%CLASSPATH%" org.fusesource.jansi.BenchmarkMain &


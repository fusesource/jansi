
source ./bench-setenv.sh

JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote"
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote.port=7091"
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote.authenticate=false"
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote.ssl=false"
JVM_ARGS="${JVM_ARGS} -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"

echo ... java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain
java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain &

pid=$!
echo "launched pid: ${pid}"
echo ${pid} > application.pid

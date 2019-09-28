
source ./bench-setenv.sh

JVM_ARGS="${JVM_ARGS} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

echo ... java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain
java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain


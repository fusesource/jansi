
source ./bench-setenv.sh

JVM_ARGS="${JVM_ARGS} -Djansi.force=true"

echo ... java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain
java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain

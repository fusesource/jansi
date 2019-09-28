
source ./bench-setenv.sh

echo ... java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain
java ${JVM_ARGS} -cp "${JVM_CP}" org.fusesource.jansi.BenchmarkMain

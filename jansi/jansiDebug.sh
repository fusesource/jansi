
pomVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
echo using pomVersion: ${pomVersion}

JVM_ARGS=""
JVM_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8001"

echo ... java ${JVM_ARGS} -jar "target/jansi-${pomVersion}.jar"
java ${JVM_ARGS} -jar "target/jansi-${pomVersion}.jar"

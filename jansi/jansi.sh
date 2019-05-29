
pomVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
echo using pomVersion: ${pomVersion}

JVM_ARGS=""

echo ... java ${JVM_ARGS} -jar "target/jansi-${pomVersion}.jar"
java ${JVM_ARGS} -jar "target/jansi-${pomVersion}.jar"

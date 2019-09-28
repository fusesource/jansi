pomVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
echo using pomVersion: ${pomVersion}

JVM_ARGS=""

JVM_CP="target/benchmark-test-${pomVersion}.jar;../jansi/target/jansi-${pomVersion}.jar"


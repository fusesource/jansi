
# Benchmark performance test for JAnsi

Execute either from maven, or from shell script to override jvm arguments...
```
mvn install -Pbenchmark-test
```

Using -Djansi.force=true
```
./bench-jansi-force.sh
```

Using -Djansi.passthrough=true
```
./bench-jansi-passthrough.sh
```


## Results

Example of benchmark execution as of 1.18-SNAPSHOT
on a windows PC,
(using plain old cmd.exe, or using cmder + cygwin + -Djansi.force=true)

There is a factor ~10 of performances degradation after installing JAnsi (143ms versus 1556ms).

```
loop Stdout Before 5x100 took 143ms
loop Jansi 5x100 took 1556ms
loop Stdout 5x100 took 1468ms
loop Jansi BigLines 5x100 took 2379ms
loop Stdout BigLines 5x100 took 2249ms
loop Jansi NoFlush 5x100 took 1876ms
loop Stdout NoFlush 5x100 took 1736ms
loop Overhead ansi + JansiPrintSteam 5x100 took 20ms
loop Overhead Raw JansiPrintSteam 5x100 took 4ms
count flush() ansi+JansiPrintStream 5x100 : 2
count flush() raw+JansiPrintStream 5x100 : 2
```

Notice that by default, on cmder + cygwin... the detected mode can be PASSTHOUGH, so NO performances degradations



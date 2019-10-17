GRAALVM = ~/graalvm-ce-19.2.0.1
SRC = src/bootleg/core.clj

all: build/bootleg

clean:
	-rm -rf build target
	lein clean

target/uberjar/bootleg-0.1.1-SNAPSHOT-standalone.jar: $(SRC)
	lein uberjar

analyse:
	$(GRAALVM)/bin/java -agentlib:native-image-agent=config-output-dir=config-dir \
		-jar target/uberjar/bootleg-0.1.1-SNAPSHOT-standalone.jar

build/bootleg: target/uberjar/bootleg-0.1.1-SNAPSHOT-standalone.jar
	-mkdir build
	$(GRAALVM)/bin/native-image \
		-jar target/uberjar/bootleg-0.1.1-SNAPSHOT-standalone.jar \
		-Dsun.java2d.opengl=false \
		-H:Name=build/bootleg \
		-H:+ReportExceptionStackTraces \
		-J-Dclojure.spec.skip-macros=true \
		-J-Dclojure.compiler.direct-linking=true \
		-H:ConfigurationFileDirectories=graal-configs/ \
		--initialize-at-build-time \
		-H:Log=registerResource: \
		--verbose \
		--allow-incomplete-classpath \
		--no-fallback \
		--no-server \
		"-J-Xmx3g" \
		-H:+TraceClassInitialization -H:+PrintClassInitialization

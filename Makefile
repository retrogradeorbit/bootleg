GRAALVM = $(HOME)/graalvm-ce-19.2.1
SRC = src/bootleg/core.clj
VERSION = 0.1.5-SNAPSHOT

all: build/bootleg

clean:
	-rm -rf build target
	lein clean

target/uberjar/bootleg-$(VERSION)-standalone.jar: $(SRC)
	GRAALVM_HOME=$(GRAALVM) lein uberjar

analyse:
	$(GRAALVM)/bin/java -agentlib:native-image-agent=config-output-dir=config-dir \
		-jar target/uberjar/bootleg-$(VERSION)-standalone.jar

build/bootleg: target/uberjar/bootleg-$(VERSION)-standalone.jar
	-mkdir build
	export
	$(GRAALVM)/bin/native-image \
		-jar target/uberjar/bootleg-$(VERSION)-standalone.jar \
		-Dsun.java2d.opengl=false \
		-H:Name=build/bootleg \
		-H:+ReportExceptionStackTraces \
		-J-Dclojure.spec.skip-macros=true \
		-J-Dclojure.compiler.direct-linking=true \
		-H:ConfigurationFileDirectories=graal-configs/ \
		--initialize-at-build-time \
		-H:Log=registerResource: \
		-H:EnableURLProtocols=http,https \
		--verbose \
		--allow-incomplete-classpath \
		--no-fallback \
		--no-server \
		"-J-Xmx4g" \
		-H:+TraceClassInitialization -H:+PrintClassInitialization

copy-libs-to-resource:
	-cp $(GRAALVM)/jre/lib/sunec.lib resources
	-cp $(GRAALVM)/jre/bin/sunec.dll resources
	-cp $(GRAALVM)/jre/lib/libsunec.dylib resources
	-cp $(GRAALVM)/jre/lib/amd64/libsunec.so resources

package-linux:
	cd build && tar cvzf bootleg-$(VERSION)-linux-amd64.tgz bootleg
	cp build/*.tgz ./
	#cp target/uberjar/bootleg-$(VERSION)-standalone.jar bootleg-$(VERSION).jar

package-macos:
	cd build && tar cvzf bootleg-$(VERSION)-darwin-amd64.tgz bootleg
	cp build/*.tgz ./
	#cp target/uberjar/bootleg-$(VERSION)-standalone.jar bootleg-$(VERSION).jar

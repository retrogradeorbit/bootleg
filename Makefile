GRAALVM = $(HOME)/graalvm-ce-java11-19.3.1
JAVA_HOME = $(GRAALVM)
PATH = $(GRAALVM)/bin:$(shell echo $$PATH)
SRC = src/bootleg/core.clj
VERSION = $(shell cat .meta/VERSION | xargs)

all: sci-reflector-install build/bootleg

clean:
	-rm -rf build target
	lein clean

sci-reflector-install:
	cd sci-reflector && lein install

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
		"-J-Xmx6g"

copy-libs-to-resource:
	-cp $(GRAALVM)/jre/lib/sunec.lib resources
	-cp $(GRAALVM)/jre/bin/sunec.dll resources
	-cp $(GRAALVM)/jre/lib/libsunec.dylib resources
	-cp $(GRAALVM)/jre/lib/amd64/libsunec.so resources

package-linux:
	cd build && tar cvzf bootleg-$(VERSION)-linux-amd64.tgz bootleg
	cp build/*.tgz ./
	cp target/uberjar/bootleg-$(VERSION)-standalone.jar bootleg-$(VERSION).jar

package-macos:
	cd build && zip bootleg-$(VERSION)-macos-amd64.zip bootleg
	cp build/*.zip .

	rm -rf /tmp/release
	mkdir -p /tmp/release
	cp build/*.zip /tmp/release
	#cp target/uberjar/bootleg-$(VERSION)-standalone.jar bootleg-$(VERSION).jar

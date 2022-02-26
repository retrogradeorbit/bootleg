GRAALVM = $(HOME)/graalvm-ce-java11-22.0.0.2
JAVA_HOME = $(GRAALVM)
PATH = $(GRAALVM)/bin:toolchain/x86_64-linux-musl-native/bin:$(shell echo $$PATH)
SRC = src/bootleg/core.clj
VERSION = $(shell cat .meta/VERSION | xargs)
CURRENT_DIR = $(shell pwd)
MUSL_PREBUILT_TOOLCHAIN_VERSION=10.2.1
ZLIB_VERSION=1.2.11
ARCH=x86_64

all: build/bootleg build/bootleg-static

clean:
	-rm -rf build target toolchain
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

build/bootleg-static: target/uberjar/bootleg-$(VERSION)-standalone.jar build-toolchain
	-mkdir build
	export
	$(GRAALVM)/bin/native-image \
		-jar target/uberjar/bootleg-$(VERSION)-standalone.jar \
		-H:Name=build/bootleg-static \
		-H:+ReportExceptionStackTraces \
		-J-Dclojure.spec.skip-macros=true \
		-J-Dclojure.compiler.direct-linking=true \
		-H:ConfigurationFileDirectories=graal-configs/ \
		--initialize-at-build-time \
		-H:Log=registerResource: \
		-H:EnableURLProtocols=http,https \
		--static \
		--libc=musl \
		--verbose \
		--allow-incomplete-classpath \
		--no-fallback \
		--no-server \
		"-J-Xmx6g"

tests:
	lein test

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

#
# musl toolchain
#
toolchain/$(ARCH)-linux-musl-native/bin/gcc:
	-mkdir toolchain
	curl -L -o toolchain/$(ARCH)-linux-musl-native.tgz https://more.musl.cc/$(MUSL_PREBUILT_TOOLCHAIN_VERSION)/$(ARCH)-linux-musl/$(ARCH)-linux-musl-native.tgz
	cd toolchain && tar xvfz $(ARCH)-linux-musl-native.tgz

musl: toolchain/$(ARCH)-linux-musl-native/bin/gcc

build/zlib/zlib-$(ZLIB_VERSION).tar.gz:
	-mkdir -p build/zlib
	curl -L -o build/zlib/zlib-$(ZLIB_VERSION).tar.gz https://zlib.net/zlib-$(ZLIB_VERSION).tar.gz

build/zlib/zlib-$(ZLIB_VERSION)/src: build/zlib/zlib-$(ZLIB_VERSION).tar.gz
	-mkdir -p build/zlib/zlib-$(ZLIB_VERSION)
	tar -xvzf build/zlib/zlib-$(ZLIB_VERSION).tar.gz -C build/zlib/zlib-$(ZLIB_VERSION) --strip-components 1
	touch build/zlib/zlib-$(ZLIB_VERSION)/src

toolchain/$(ARCH)-linux-musl-native/lib/libz.a: build/zlib/zlib-$(ZLIB_VERSION)/src
	-mkdir toolchain
	cd build/zlib/zlib-$(ZLIB_VERSION) && \
	./configure --static --prefix=$(CURRENT_DIR)/toolchain/$(ARCH)-linux-musl-native && \
	make PATH=$(CURRENT_DIR)/toolchain/$(ARCH)-linux-musl-native/bin:$$PATH CC=$(ARCH)-linux-musl-gcc && \
	make install

libz: toolchain/$(ARCH)-linux-musl-native/lib/libz.a

build-toolchain: musl libz

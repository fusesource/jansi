
include Makefile.common

RESOURCE_DIR = src/main/resources

.phony: all package native native-all deploy

all: jni-header package

deploy: 
	mvn package deploy -DperformRelease=true

MVN:=mvn
SRC:=src/main/native
JANSI_OUT:=$(TARGET)/native-$(OS_NAME)-$(OS_ARCH)

CCFLAGS:= -I$(JANSI_OUT) $(CCFLAGS)

$(JANSI_ARCHIVE):
#	@mkdir -p $(@D)
#	curl -L --max-redirs 0 -f -o$@ https://www.JANSI.org/2020/$(JANSI_AMAL_PREFIX).zip || \
#	curl -L --max-redirs 0 -f -o$@ https://www.JANSI.org/$(JANSI_AMAL_PREFIX).zip || \
#	curl -L --max-redirs 0 -f -o$@ https://www.JANSI.org/$(JANSI_OLD_AMAL_PREFIX).zip

$(JANSI_UNPACKED): $(JANSI_ARCHIVE)
#	unzip -qo $< -d $(TARGET)/tmp.$(version)
#	(mv $(TARGET)/tmp.$(version)/$(JANSI_AMAL_PREFIX) $(TARGET) && rmdir $(TARGET)/tmp.$(version)) || mv $(TARGET)/tmp.$(version)/ $(TARGET)/$(JANSI_AMAL_PREFIX)
#	touch $@


test:
	mvn test

clean: clean-native clean-java clean-tests

jni-header:

$(JANSI_OUT)/%.o: $(SRC)/%.c
	@mkdir -p $(@D)
	$(info running: $(CC) $(CCFLAGS) -c $< -o $@)
	$(CC) $(CCFLAGS) -c $< -o $@

$(JANSI_OUT)/$(LIBNAME): $(JANSI_OUT)/hawtjni.o $(JANSI_OUT)/jansi.o $(JANSI_OUT)/jansi_isatty.o $(JANSI_OUT)/jansi_structs.o $(JANSI_OUT)/jansi_ttyname.o
	@mkdir -p $(@D)
	$(CC) $(CCFLAGS) -o $@ $(JANSI_OUT)/hawtjni.o $(JANSI_OUT)/jansi.o $(JANSI_OUT)/jansi_isatty.o $(JANSI_OUT)/jansi_structs.o $(JANSI_OUT)/jansi_ttyname.o $(LINKFLAGS)
	$(STRIP) $@

NATIVE_DIR=src/main/resources/org/fusesource/jansi/internal/native/$(OS_NAME)/$(OS_ARCH)
NATIVE_TARGET_DIR:=$(TARGET)/classes/org/fusesource/jansi/internal/native/$(OS_NAME)/$(OS_ARCH)
NATIVE_DLL:=$(NATIVE_DIR)/$(LIBNAME)

# For cross-compilation, install docker. See also https://github.com/dockcross/dockcross
# Disabled linux-armv6 build because of this issue; https://github.com/dockcross/dockcross/issues/190
native-all: linux-x86 linux-x86_64 linux-arm linux-armv7 \
	linux-arm64 linux-ppc64 win-x86 win-x86_64 mac-x86 mac-x86_64 freebsd-x86 freebsd-x86_64

native: $(NATIVE_DLL)

$(NATIVE_DLL): $(JANSI_OUT)/$(LIBNAME)
	@mkdir -p $(@D)
	cp $< $@
	@mkdir -p $(NATIVE_TARGET_DIR)
	cp $< $(NATIVE_TARGET_DIR)/$(LIBNAME)

DOCKER_RUN_OPTS=--rm

linux-x86: $(JANSI_UNPACKED) jni-header
	./docker/dockcross-linux-x86 bash -c 'make clean-native native OS_NAME=Linux OS_ARCH=x86'

linux-x86_64: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=x86_64-linux-gnu multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=x86_64

linux-arm: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=arm-linux-gnueabi multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=arm

linux-armv7: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=arm-linux-gnueabihf multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=armv7

linux-arm64: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=aarch64-linux-gnu multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=arm64

linux-ppc64: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=powerpc64le-linux-gnu multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=ppc64

win-x86: $(JANSI_UNPACKED) jni-header
	./docker/dockcross-windows-static-x86 bash -c 'make clean-native native CROSS_PREFIX=i686-w64-mingw32.static- OS_NAME=Windows OS_ARCH=x86'

win-x86_64: $(JANSI_UNPACKED) jni-header
	./docker/dockcross-windows-static-x64 bash -c 'make clean-native native CROSS_PREFIX=x86_64-w64-mingw32.static- OS_NAME=Windows OS_ARCH=x86_64'

mac-x86: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=i386-apple-darwin multiarch/crossbuild make clean-native native OS_NAME=Mac OS_ARCH=x86

mac-x86_64: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/workdir -e CROSS_TRIPLE=x86_64-apple-darwin multiarch/crossbuild make clean-native native OS_NAME=Mac OS_ARCH=x86_64

freebsd-x86: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/build empterdose/freebsd-cross-build:9.3 make -C /build clean-native native CROSS_PREFIX=i386-freebsd9- OS_NAME=FreeBSD OS_ARCH=x86

freebsd-x86_64: $(JANSI_UNPACKED) jni-header
	docker run -it $(DOCKER_RUN_OPTS) -v $$PWD:/build empterdose/freebsd-cross-build:9.3 make -C /build clean-native native CROSS_PREFIX=x86_64-freebsd9- OS_NAME=FreeBSD OS_ARCH=x86_64

#sparcv9:
#	$(MAKE) native OS_NAME=SunOS OS_ARCH=sparcv9


package: native-all
	rm -rf target/dependency-maven-plugin-markers
	$(MVN) package

clean-native:
	rm -rf $(JANSI_OUT)

clean-java:
	rm -rf $(TARGET)/*classes
	rm -rf $(TARGET)/common-lib/*
	rm -rf $(TARGET)/JANSI-jdbc-*jar

clean-tests:
	rm -rf $(TARGET)/{surefire*,testdb.jar*}



include Makefile.common

.phony: all package native native-all deploy

all: package

JANSI_OUT:=target/native-$(OS_NAME)-$(OS_ARCH)

CCFLAGS:= -I$(JANSI_OUT) $(CCFLAGS)


clean-native:
	rm -rf $(JANSI_OUT)

$(JANSI_OUT)/%.o: src/main/native/%.c
	@mkdir -p $(@D)
	$(info running: $(CC) $(CCFLAGS) -c $< -o $@)
	$(CC) $(CCFLAGS) -c $< -o $@

$(JANSI_OUT)/$(LIBNAME): $(JANSI_OUT)/jansi.o $(JANSI_OUT)/jansi_isatty.o $(JANSI_OUT)/jansi_structs.o $(JANSI_OUT)/jansi_ttyname.o
	@mkdir -p $(@D)
	$(CC) $(CCFLAGS) -o $@ $(JANSI_OUT)/jansi.o $(JANSI_OUT)/jansi_isatty.o $(JANSI_OUT)/jansi_structs.o $(JANSI_OUT)/jansi_ttyname.o $(LINKFLAGS)

NATIVE_DIR=src/main/resources/org/fusesource/jansi/internal/native/$(OS_NAME)/$(OS_ARCH)
NATIVE_TARGET_DIR:=target/classes/org/fusesource/jansi/internal/native/$(OS_NAME)/$(OS_ARCH)
NATIVE_DLL:=$(NATIVE_DIR)/$(LIBNAME)

# For cross-compilation, install docker. See also https://github.com/dockcross/dockcross
native-all: linux-x86 linux-x86_64 linux-arm linux-armv6 linux-armv7 \
	linux-arm64 linux-ppc64 win-x86 win-x86_64 mac-x86 mac-x86_64 freebsd-x86 freebsd-x86_64

native: $(NATIVE_DLL)

$(NATIVE_DLL): $(JANSI_OUT)/$(LIBNAME)
	@mkdir -p $(@D)
	cp $< $@
	@mkdir -p $(NATIVE_TARGET_DIR)
	cp $< $(NATIVE_TARGET_DIR)/$(LIBNAME)

linux-x86:
	./docker/dockcross-linux-x86 bash -c 'make clean-native native OS_NAME=Linux OS_ARCH=x86'

linux-x86_64:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=x86_64-linux-gnu multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=x86_64

linux-arm:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=arm-linux-gnueabi multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=arm

linux-armv6:
	./docker/dockcross-linux-armv6 bash -c 'make clean-native native OS_NAME=Linux OS_ARCH=armv6'

linux-armv7:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=arm-linux-gnueabihf multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=armv7

linux-arm64:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=aarch64-linux-gnu multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=arm64

linux-ppc64:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=powerpc64le-linux-gnu multiarch/crossbuild make clean-native native OS_NAME=Linux OS_ARCH=ppc64

win-x86:
	./docker/dockcross-windows-static-x86 bash -c 'make clean-native native CROSS_PREFIX=i686-w64-mingw32.static- OS_NAME=Windows OS_ARCH=x86'

win-x86_64:
	./docker/dockcross-windows-static-x64 bash -c 'make clean-native native CROSS_PREFIX=x86_64-w64-mingw32.static- OS_NAME=Windows OS_ARCH=x86_64'

mac-x86:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=i386-apple-darwin multiarch/crossbuild make clean-native native OS_NAME=Mac OS_ARCH=x86

mac-x86_64:
	docker run -it --rm -v $$PWD:/workdir -e CROSS_TRIPLE=x86_64-apple-darwin multiarch/crossbuild make clean-native native OS_NAME=Mac OS_ARCH=x86_64

freebsd-x86:
	docker run -it --rm -v $$PWD:/workdir empterdose/freebsd-cross-build:9.3 make clean-native native CROSS_PREFIX=i386-freebsd9- OS_NAME=FreeBSD OS_ARCH=x86

freebsd-x86_64:
	docker run -it --rm -v $$PWD:/workdir empterdose/freebsd-cross-build:9.3 make clean-native native CROSS_PREFIX=x86_64-freebsd9- OS_NAME=FreeBSD OS_ARCH=x86_64

#sparcv9:
#	$(MAKE) native OS_NAME=SunOS OS_ARCH=sparcv9




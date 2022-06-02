#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
include Makefile.common

.PHONY: all package native native-all deploy crossbuild ducible clean-native

linux-armv6-digest:=@sha256:7bad6ab302af34bdf6634c8c2b02c8dc6ac932c67da9ecc199c549ab405e971e
linux-x86-digest:=@sha256:7a8fda5ff1bb436ac1f2e7d40043deb630800fce33d123d04779d48f85702dcd
windows-static-x86-digest:=@sha256:896bd4a43bbc89502904afdc8d00e6f2422f8f35852cc59777d6426bfc8491e8
windows-static-x64-digest:=@sha256:f159861bc80b29e5dafb223477167bec53ecec6cdacb051d31e90c5823542100
windows-arm64-digest:=@sha256:f4b3c1a49ec8b53418cef1499dc3f9a54a5570b7a3ecdf42fc8c83eb94b01b7d
cross-build-digest:=@sha256:8dbaa86462270db93ae1b1b319bdd88d89272faf3a68632daf4fa36b414a326e
freebsd-crossbuild-digest:=@sha256:cda62697a15d8bdc0bc26e780b1771ee78f12c55e7d5813e62c478af5a747c43
mcandre-snek-digest:=@sha256:9f84e9fcdf66daafc1f1c3fb772a6c97977714e17800aeac2e3bbe5dc5039dd0

all: package

JANSI_OUT:=target/native-$(OS_NAME)-$(OS_ARCH)

CCFLAGS:= -I$(JANSI_OUT) $(CCFLAGS)

target:
	@test -d target || mkdir target

download-includes: target
	@test -d target/inc || mkdir target/inc
	@test -d target/inc/unix || mkdir target/inc/unix
	@test -d target/inc/windows || mkdir target/inc/windows
	test -f target/inc/jni.h || wget -O target/inc/jni.h https://raw.githubusercontent.com/openjdk/jdk/jdk-11%2B28/src/java.base/share/native/include/jni.h
	test -f target/inc/unix/jni_md.h || wget -O target/inc/unix/jni_md.h https://raw.githubusercontent.com/openjdk/jdk/jdk-11%2B28/src/java.base/unix/native/include/jni_md.h
	test -f target/inc/windows/jni_md.h || wget -O target/inc/windows/jni_md.h https://raw.githubusercontent.com/openjdk/jdk/jdk-11%2B28/src/java.base/windows/native/include/jni_md.h

dockcross: target
	@test -d target/dockcross || mkdir target/dockcross

# This target does not generate the same image digest that the one uploaded
#crossbuild: target
#	test -d target/crossbuild || git clone https://github.com/multiarch/crossbuild.git target/crossbuild
#	git -C target/crossbuild reset --hard d06cdc31fce0c85ad78408b44794366dafd59554
#	docker build target/crossbuild -t multiarch/crossbuild

ducible: target
	test -d target/ducible || git clone --branch v1.2.2 https://github.com/jasonwhite/ducible.git target/ducible
	make --directory=target/ducible ducible CROSS_PREFIX= CXX=g++ CC=gcc

clean-native:
	rm -rf $(JANSI_OUT)

$(JANSI_OUT)/%.o: src/main/native/%.c
	@mkdir -p $(@D)
	$(info running: $(CC) $(CCFLAGS) -c $< -o $@)
	$(CC) $(CCFLAGS) -c $< -o $@

ifeq ($(OS_NAME), Windows)
$(JANSI_OUT)/$(LIBNAME): ducible
endif
$(JANSI_OUT)/$(LIBNAME): $(JANSI_OUT)/jansi.o $(JANSI_OUT)/jansi_isatty.o $(JANSI_OUT)/jansi_structs.o $(JANSI_OUT)/jansi_ttyname.o
	@mkdir -p $(@D)
	$(CC) $(CCFLAGS) -o $@ $(JANSI_OUT)/jansi.o $(JANSI_OUT)/jansi_isatty.o $(JANSI_OUT)/jansi_structs.o $(JANSI_OUT)/jansi_ttyname.o $(LINKFLAGS)
ifeq ($(OS_NAME), Windows)
	target/ducible/ducible $(JANSI_OUT)/$(LIBNAME)
endif

NATIVE_DIR=src/main/resources/org/fusesource/jansi/internal/native/$(OS_NAME)/$(OS_ARCH)
NATIVE_TARGET_DIR:=target/classes/org/fusesource/jansi/internal/native/$(OS_NAME)/$(OS_ARCH)
NATIVE_DLL:=$(NATIVE_DIR)/$(LIBNAME)

# For cross-compilation, install docker. See also https://github.com/dockcross/dockcross
# Disabled linux-armv6 build because of this issue; https://github.com/dockcross/dockcross/issues/190
native-all: linux-x86 linux-x86_64 linux-arm linux-armv6 linux-armv7 \
	linux-arm64 linux-ppc64 win-x86 win-x86_64 win-arm64 mac-x86 mac-x86_64 mac-arm64 freebsd-x86 freebsd-x86_64

native: $(NATIVE_DLL)

$(NATIVE_DLL): $(JANSI_OUT)/$(LIBNAME)
	@mkdir -p $(@D)
	cp $< $@
	@mkdir -p $(NATIVE_TARGET_DIR)
	cp $< $(NATIVE_TARGET_DIR)/$(LIBNAME)

target/dockcross/dockcross-linux-x86: dockcross
	docker run --rm dockcross/linux-x86$(linux-x86-digest) > target/dockcross/dockcross-linux-x86
	chmod +x target/dockcross/dockcross-linux-x86
linux-x86: download-includes target/dockcross/dockcross-linux-x86
	target/dockcross/dockcross-linux-x86 bash -c 'make clean-native native OS_NAME=Linux OS_ARCH=x86'

linux-x86_64: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=x86_64-linux-gnu multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Linux OS_ARCH=x86_64

linux-arm: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=arm-linux-gnueabi multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Linux OS_ARCH=arm

target/dockcross/dockcross-linux-armv6: dockcross
	docker run --rm dockcross/linux-armv6$(linux-armv6-digest) > target/dockcross/dockcross-linux-armv6
	chmod +x target/dockcross/dockcross-linux-armv6
linux-armv6: download-includes target/dockcross/dockcross-linux-armv6
	target/dockcross/dockcross-linux-armv6 bash -c 'make clean-native native CROSS_PREFIX=armv6-unknown-linux-gnueabihf- OS_NAME=Linux OS_ARCH=armv6'

linux-armv7: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=arm-linux-gnueabihf multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Linux OS_ARCH=armv7

linux-arm64: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=aarch64-linux-gnu multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Linux OS_ARCH=arm64

linux-ppc64: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=powerpc64le-linux-gnu multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Linux OS_ARCH=ppc64

target/dockcross/dockcross-windows-static-x86: dockcross
	docker run --rm dockcross/windows-static-x86$(windows-static-x86-digest) > target/dockcross/dockcross-windows-static-x86
	chmod +x target/dockcross/dockcross-windows-static-x86
win-x86: download-includes target/dockcross/dockcross-windows-static-x86
	target/dockcross/dockcross-windows-static-x86 bash -c 'make clean-native native CROSS_PREFIX=i686-w64-mingw32.static- OS_NAME=Windows OS_ARCH=x86'

target/dockcross/dockcross-windows-static-x64: dockcross
	docker run --rm dockcross/windows-static-x64$(windows-static-x64-digest) > target/dockcross/dockcross-windows-static-x64
	chmod +x target/dockcross/dockcross-windows-static-x64
win-x86_64: download-includes target/dockcross/dockcross-windows-static-x64
	target/dockcross/dockcross-windows-static-x64 bash -c 'make clean-native native CROSS_PREFIX=x86_64-w64-mingw32.static- OS_NAME=Windows OS_ARCH=x86_64'

target/dockcross/dockcross-windows-arm64: dockcross
	docker run --rm dockcross/windows-arm64$(windows-arm64-digest) > target/dockcross/dockcross-windows-arm64
	chmod +x target/dockcross/dockcross-windows-arm64
win-arm64: download-includes target/dockcross/dockcross-windows-arm64
	target/dockcross/dockcross-windows-arm64 bash -c 'make clean-native native CROSS_PREFIX=aarch64-w64-mingw32- OS_NAME=Windows OS_ARCH=arm64'

mac-x86: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=i386-apple-darwin multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Mac OS_ARCH=x86

mac-x86_64: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		-e CROSS_TRIPLE=x86_64-apple-darwin multiarch/crossbuild$(cross-build-digest) make clean-native native OS_NAME=Mac OS_ARCH=x86_64

mac-arm64: download-includes
	docker run -it --rm -v $$PWD:/src --user $$(id -u):$$(id -g) \
		-e TARGET=arm64-apple-darwin mcandre/snek$(mcandre-snek-digest) sh -c "make clean-native native CROSS_PREFIX=arm64-apple-darwin20.4- OS_NAME=Mac OS_ARCH=arm64"

freebsd-x86: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		empterdose/freebsd-cross-build$(freebsd-crossbuild-digest) make clean-native native CROSS_PREFIX=i386-freebsd9- OS_NAME=FreeBSD OS_ARCH=x86

freebsd-x86_64: download-includes
	docker run -it --rm -v $$PWD:/workdir --user $$(id -u):$$(id -g) \
		empterdose/freebsd-cross-build$(freebsd-crossbuild-digest) make clean-native native CROSS_PREFIX=x86_64-freebsd9- OS_NAME=FreeBSD OS_ARCH=x86_64

#sparcv9:
#	$(MAKE) native OS_NAME=SunOS OS_ARCH=sparcv9


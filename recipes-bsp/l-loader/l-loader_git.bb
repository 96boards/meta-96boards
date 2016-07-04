SUMMARY = "Loader to switch from aarch32 to aarch64 and boot"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=00ef5534e9238b3296c56a2caa13630c"

DEPENDS += " gptfdisk-native edk2-hikey"

inherit deploy pythonnative

SRCREV = "cddd213c1b820d5f224d20b38581c504552dd59e"
PV = "0.2.2"

### DISCLAIMER ###
# l-loader should be built with an aarch32 toolchain but we target an
# ARMv8 machine. OE cross-toolchain is aarch64 in this case.
# We decided to use an external pre-built toolchain in order to build
# l-loader.
# knowledgeably, it is a hack...
###
SRC_URI = "git://github.com/96boards-hikey/l-loader.git;branch=master \
           http://releases.linaro.org/components/toolchain/binaries/5.3-2016.02/arm-linux-gnueabihf/gcc-linaro-5.3-2016.02-x86_64_arm-linux-gnueabihf.tar.xz;name=tc \
"
SRC_URI[tc.md5sum] = "01d8860d62807b676762c9c2576dfb22"
SRC_URI[tc.sha256sum] = "dd66f07662e1f3b555eaa0d076f133b6db702ab0b9ab18f7dfc91a23eab653c5"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"

do_compile() {
    # Use pre-built aarch32 toolchain
    export PATH=${WORKDIR}/gcc-linaro-5.3-2016.02-x86_64_arm-linux-gnueabihf/bin:$PATH

    # bl1 from edk2 is required
    rm -f bl1.bin
    ln -sf ${STAGING_LIBDIR}/edk2/bl1.bin

    make
}

do_install[noexec] = "1"

do_deploy() {
    install -D -p -m0644 l-loader.bin ${DEPLOY_DIR_IMAGE}/l-loader.bin
    cp -a ptable-linux-*.img ${DEPLOY_DIR_IMAGE}
}

addtask deploy before do_build after do_compile

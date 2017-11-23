SUMMARY = "Loader to switch from aarch32 to aarch64 and boot"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e8c1458438ead3c34974bc0be3a03ed6"

COMPATIBLE_MACHINE = "poplar"
DEPENDS += " atf-poplar coreutils-native util-linux-native"

inherit deploy pythonnative

SRCREV = "d7b3ac7748d6607c2dd82cf37326b80a2ede6e5b"

### DISCLAIMER ###
# l-loader should be built with an aarch32 toolchain but we target an
# ARMv8 machine. OE cross-toolchain is aarch64 in this case.
# We decided to use an external pre-built toolchain in order to build
# l-loader.
# knowledgeably, it is a hack...
###
SRC_URI = "git://github.com/Linaro/poplar-l-loader.git;branch=latest \
           http://releases.linaro.org/components/toolchain/binaries/5.3-2016.02/arm-linux-gnueabihf/gcc-linaro-5.3-2016.02-x86_64_arm-linux-gnueabihf.tar.xz;name=tc \
"
SRC_URI[tc.md5sum] = "01d8860d62807b676762c9c2576dfb22"
SRC_URI[tc.sha256sum] = "dd66f07662e1f3b555eaa0d076f133b6db702ab0b9ab18f7dfc91a23eab653c5"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"

do_compile() {
    # Use pre-built aarch32 toolchain
    export PATH=${WORKDIR}/gcc-linaro-5.3-2016.02-x86_64_arm-linux-gnueabihf/bin:$PATH

    # bl1 from fip.bin are required from ATF
    rm -f atf/bl1.bin
    rm -f arf/fip.bin
    ln -sf ${STAGING_LIBDIR}/atf/bl1.bin ${S}/atf/bl1.bin
    ln -sf ${STAGING_LIBDIR}/atf/fip.bin ${S}/atf/fip.bin
    export ARM_TF_INCLUDE=${STAGING_INCDIR}
    make
}

do_install() {
    install -D -p -m0644 fastboot.bin ${D}${libdir}/l-loader/fastboot.bin
}

do_deploy() {
    install -D -p -m0644 fastboot.bin ${DEPLOYDIR}/fastboot.bin
}

FILES_${PN} += "${libdir}/l-loader"

addtask deploy before do_build after do_compile

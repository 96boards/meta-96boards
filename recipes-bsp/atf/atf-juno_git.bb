require atf.inc

DESCRIPTION = "ARM Trusted Firmware Juno"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://license.rst;md5=33065335ea03d977d0569f270b39603e"

COMPATIBLE_MACHINE = "juno"

DEPENDS += "u-boot-juno"

SRCREV = "b762fc7481c66b64eb98b6ff694d569e66253973"

SRC_URI = "git://github.com/ARM-software/arm-trusted-firmware.git;protocol=https;name=atf;branch=master \
           http://releases.linaro.org/members/arm/platforms/17.04/juno-latest-oe-uboot.zip;name=junofip;subdir=juno-oe-uboot \
"
SRC_URI[junofip.md5sum] = "12fc772de457930fc60e42bdde97eb0a"
SRC_URI[junofip.sha256sum] = "be1a3f8b72a0dd98ba1bf9f4fd5415d3adca052c60b090c5dccc178588ec43bc"

S = "${WORKDIR}/git"

# Building for Juno and FVP requires a special SCP firmware to be packed with
# the FIP. You can Refer to the documentation here:
# https://github.com/ARM-software/arm-trusted-firmware/blob/master/docs/user-guide.rst#id19
# This must be obtained from the Arm deliverables as released by Linaro:
# https://community.arm.com/dev-platforms/b/documents/posts/linaro-release-notes-deprecated
do_compile() {
    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} all fip PLAT=${COMPATIBLE_MACHINE} SPD=none \
        SCP_BL2=${WORKDIR}/juno-oe-uboot/SOFTWARE/scp_bl2.bin BL33=${DEPLOY_DIR_IMAGE}/u-boot.bin
}

require linux.inc
require linux-96boards-bootimg.inc

DESCRIPTION = "Generic 96boards kernel"

PV = "4.4.11+git"
SRCREV_kernel = "dc832c605586ad8757c3d85f523605240024390a"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://github.com/96boards/linux.git;protocol=https;branch=96b/releases/2016.06;name=kernel \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "96boards-64|96boards-32|juno|hikey"
KERNEL_IMAGETYPE ?= "Image"
# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    # Make sure to disable debug info and enable ext4fs built-in
    sed -e '/CONFIG_EXT4_FS=/d' \
        -e '/CONFIG_DEBUG_INFO=/d' \
        < ${S}/arch/arm64/configs/distro.config \
        > ${B}/.config

    echo 'CONFIG_EXT4_FS=y' >> ${B}/.config
    echo '# CONFIG_DEBUG_INFO is not set' >> ${B}/.config

    yes '' | oe_runmake -C ${S} O=${B} oldconfig
}

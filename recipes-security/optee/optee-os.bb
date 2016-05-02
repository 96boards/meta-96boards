SUMMARY = "OPTEE OS"
DESCRIPTION = "OPTEE OS"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=69663ab153298557a59c67a60a743e5b"
PR="r0"
PV="1.1.0+git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

SRC_URI = "git://github.com/OP-TEE/optee_os.git"


SRC_URI += " file://0001-allow-setting-sysroot-for-libgcc-lookup.patch "
S = "${WORKDIR}/git"

SRCREV = "c042fbefb52822b8f1c681d49e272092edd10b0d"

EXTRA_OEMAKE = "PLATFORM=hikey CFG_ARM64_core=y \
        CROSS_COMPILE_core=${HOST_PREFIX}  \
        CROSS_COMPILE_ta_arm64=${HOST_PREFIX}  \
        ta-targets=ta_arm64 \
        LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST} \
        "
OPTEE_ARCH_armv7a = "arm32"
OPTEE_ARCH_aarch64 = "arm64"

do_compile() {
    unset LDFLAGS
    oe_runmake all CFG_TEE_TA_LOG_LEVEL=0
}

do_install() {
    bbwarn "Optee installation"
    #install core on boot directory
    install -d ${D}/lib/firmware/

    install -m 644 ${B}/out/arm-plat-${MACHINE}/core/*.bin ${D}/lib/firmware/
    #install TA devkit
    install -d ${D}/usr/include/optee/export-user_ta/


    for f in  ${B}/out/arm-plat-${MACHINE}/export-ta_${OPTEE_ARCH}/* ; do
        cp -aR  $f  ${D}/usr/include/optee/export-user_ta/
    done
}


do_deploy() {
    long_srvrev=${SRCREV}
    short_srvrev=$(echo $long_srvrev | awk  '{ string=substr($0, 1, 8); print string; }' )
    OPTEE_CORE_SUFFIX="${MACHINE}-$short_srvrev-${DATETIME}"
    install -d ${DEPLOYDIR}/optee
    if [ "${is_armv7}" = "1" ];
    then
        for f in ${D}/lib/firmware/*; do
            filename=$(basename "$f")
            extension="${filename##*.}"
            sfilename="${filename%.*}"
            bbnote "Deploy $sfilename-${OPTEE_CORE_SUFFIX}.$extension"
            install -m 644 $f ${DEPLOYDIR}/optee/$sfilename-${OPTEE_CORE_SUFFIX}.$extension
        done
    fi
}
addtask deploy before do_build after do_install
FILES_${PN} = "/lib/firmware/"
FILES_${PN}-dev = "/usr/include/optee"
INSANE_SKIP_${PN}-dev = "staticdev"

INHIBIT_PACKAGE_STRIP = "1"


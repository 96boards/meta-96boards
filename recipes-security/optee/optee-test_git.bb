SUMMARY = "OP-TEE sanity testsuite"
HOMEPAGE = "https://github.com/OP-TEE/optee_test"

LICENSE = "BSD & GPLv2"
LIC_FILES_CHKSUM = "file://${S}/LICENSE.md;md5=daa2bcccc666345ab8940aab1315a4fa"

DEPENDS = "optee-client optee-os python-pycrypto-native"

inherit pythonnative

PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/OP-TEE/optee_test.git"
S = "${WORKDIR}/git"

SRCREV = "5bf536242b29bf5f1b578d79da076db96c46d0b7"

OPTEE_CLIENT_EXPORT = "${STAGING_DIR_TARGET}${prefix}"
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

CFLAGS += "-I${STAGING_INCDIR}"

EXTRA_OEMAKE = " TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 OPTEE_CLIENT_EXPORT=${OPTEE_CLIENT_EXPORT} \
                 CC='${CC}' LD='${LD}' AR='${AR}' \
                 CROSS_COMPILE=${TARGET_PREFIX} \
                 CROSS_COMPILE_ta_arm64=${TARGET_PREFIX} \
                 CFLAFS='${CFLAGS}' \
                 V=1 \
               "

do_compile() {
    # *sigh* don't enable -Werror if your code is dodgy and triggers a ton of gcc warnings.
    sed -i -e 's:-Werror : :g' ${S}/host/xtest/Makefile

    # Top level makefile doesn't seem to handle parallel make gracefully
    oe_runmake xtest
    oe_runmake ta
}

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"

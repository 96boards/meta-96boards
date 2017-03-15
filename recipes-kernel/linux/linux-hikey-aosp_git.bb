require linux.inc

DESCRIPTION = "AOSP kernel for HiKey"

PV = "4.9+git${SRCPV}"
SRCREV_kernel = "cb3d81d81d7ae4dff97a7cd44a756e5f158d649b"
SRCREV_FORMAT = "kernel"

SRC_URI = "\
    git://android.googlesource.com/kernel/hikey-linaro;protocol=https;branch=android-hikey-linaro-4.9;name=kernel \
    file://kselftests-extras.config;subdir=git/kernel/configs \
    file://oe-overrides.config;subdir=git/kernel/configs \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_CONFIG_FRAGMENTS += "\
    ${S}/arch/arm64/configs/hikey_defconfig \
    ${S}/kernel/configs/oe-overrides.config \
    ${S}/kernel/configs/kselftests.config \
    ${S}/kernel/configs/kselftests-extras.config \
"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    touch ${B}/.config

    # Generate a config fragment from the individual selftests config files
    # included in the kernel source tree
    for f in $(find ${S}/tools/testing/selftests -type f -name config); do
        cat ${f} >> ${S}/kernel/configs/kselftests.config
    done

    # Remove duplicated lines and sort the content
    LANG=C sort \
        -u ${S}/kernel/configs/kselftests.config \
        -o ${S}/kernel/configs/kselftests.config

    # Check for kernel config fragments. The assumption is that the config
    # fragment will be specified with the absolute path. For example:
    #   * ${WORKDIR}/config1.cfg
    #   * ${S}/config2.cfg
    # Iterate through the list of configs and make sure that you can find
    # each one. If not then error out.
    # NOTE: If you want to override a configuration that is kept in the kernel
    #       with one from the OE meta data then you should make sure that the
    #       OE meta data version (i.e. ${WORKDIR}/config1.cfg) is listed
    #       after the in-kernel configuration fragment.
    # Check if any config fragments are specified.
    if [ ! -z "${KERNEL_CONFIG_FRAGMENTS}" ]; then
        for f in ${KERNEL_CONFIG_FRAGMENTS}; do
            # Check if the config fragment was copied into the WORKDIR from
            # the OE meta data
            if [ ! -e "$f" ]; then
                echo "Could not find kernel config fragment $f"
                exit 1
            fi
        done

        # Now that all the fragments are located merge them.
        ( cd ${WORKDIR} && ${S}/scripts/kconfig/merge_config.sh -m -r -O ${B} ${B}/.config ${KERNEL_CONFIG_FRAGMENTS} 1>&2 )
    fi

    yes '' | oe_runmake -C ${S} O=${B} oldconfig
}

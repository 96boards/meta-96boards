# Include file to build the OP-TEE kernel support when 'optee' is present in MACHINE_FEATURES


SRCREV_linuxoptee = "5fcce5d5800a60957141f1d963edfd199480bfcb"
SRC_URI_append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee', ' git://github.com/OP-TEE/optee_linuxdriver;protocol=https;branch=master;destsuffix=${S}/optee_linuxdriver;name=linuxoptee', '', d)}"


do_compile_append() {
    if [ -n "${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee', '', d)}" ]; then
        oe_runmake O=${B} M=${S}/optee_linuxdriver -C ${B} ${PARALLEL_MAKE} modules CC="${KERNEL_CC}" LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS}
    fi
}

kernel_do_install_append() {
    if [ -n "${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee', '', d)}" ]; then
        oe_runmake M=${S}/optee_linuxdriver INSTALL_MOD_PATH="${D}" modules_install
    fi
}

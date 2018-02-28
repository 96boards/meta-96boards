require linux.inc
require kselftests.inc

DESCRIPTION = "Generic Linux Stable RC 4.4 kernel"

PV = "4.4+git${SRCPV}"
SRCREV_kernel = "425fdd287e9b41a20bc8b47a00064da3fcd8cae4"
SRCREV_FORMAT = "kernel"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable-rc.git;protocol=https;branch=linux-4.4.y;name=kernel \
    file://distro-overrides.config;subdir=git/kernel/configs \
    file://systemd.config;subdir=git/kernel/configs \
    file://0001-selftests-create-test-specific-kconfig-fragments.patch \
    file://0001-selftests-lib-add-config-fragment-for-bitmap-printf-.patch \
    file://0001-selftests-ftrace-add-CONFIG_KPROBES-y-to-the-config-.patch \
    file://0001-selftests-vm-add-CONFIG_SYSVIPC-y-to-the-config-frag.patch \
    file://0001-selftests-create-cpufreq-kconfig-fragments.patch \
    file://0001-selftests-sync-add-config-fragment-for-testing-sync-.patch \
    file://0001-selftests-ftrace-add-more-config-fragments.patch \
"

# apply=yes indicates non-essential patches for STiH410-b2260 support
SRC_URI_append_stih410-b2260 = "\
    file://0001-clk-add-flag-for-clocks-that-need-to-be-enabled-on-r.patch \
    file://0002-clk-Use-static-inline-functions-instead-of-macros-fo.patch \
    file://0003-clk-Allow-clocks-to-be-marked-as-CRITICAL.patch \
    file://0004-clk-WARN_ON-about-to-disable-a-critical-clock.patch \
    file://0005-clk-Provide-OF-helper-to-mark-clocks-as-CRITICAL.patch \
    file://0006-clk-st-clk-flexgen-Detect-critical-clocks.patch \
    file://0007-clk-st-clkgen-fsyn-Detect-critical-clocks.patch \
    file://0008-clk-st-clkgen-pll-Detect-critical-clocks.patch \
    file://0009-ARM-sti-stih407-family-Supply-defines-for-CLOCKGEN-A.patch \
    file://0010-ARM-sti-stih410-clocks-Identify-critical-clocks.patch \
    file://0011-spi-st-ssc4-Fix-missing-spi_master_put-in-spi_st_pro.patch \
    file://0012-spi-st-ssc4-Remove-no-clocking-hack.patch \
    \
    file://0001-ARM-dts-STiH407-Move-pio20-node-to-fix-kernel-warnin.patch;apply=yes \
    file://0002-ARM-dts-STiH410-Add-thermal-node.patch;apply=yes \
    file://0003-ARM-dts-STiH407-pinctrl-Add-i2c2_alt2_1-node.patch \
    file://0004-ARM-dts-STiH407-Move-non-removable-property-to-board.patch;apply=yes \
    file://0005-ARM-dts-STiH407-pinctrl-Add-pinctrl_rgmii1_mdio_1-no.patch \
    file://0006-ARM-dts-STiH407-family-Add-ports-implemented-propert.patch;apply=yes \
    file://0007-ARM-dts-STi-Introduce-B2260-board.patch \
    file://0008-ARM-dts-STiH407-pinctrl-Update-gpio-cells-to-2.patch;apply=yes \
    file://0009-ARM-dts-STiH407-Supply-PWM-Capture-IRQ.patch;apply=yes \
    file://0010-ARM-dts-STiH407-Declare-PWM-Capture-data-lines-via-P.patch;apply=yes \
    file://0011-ARM-dts-STiH410-b2260-add-USB3-node.patch \
    file://0012-ARM-dts-STiH410-b2260-add-clk_ignore_unused-in-boota.patch \
    file://0013-ARM-dts-STiH410-Add-hva-dt-nodes.patch;apply=yes \
    file://0014-ARM-DT-STiH407-Add-i2s_out-pinctrl-configuration.patch;apply=yes \
    file://0015-ARM-DT-STiH407-Add-i2s_in-pinctrl-configuration.patch;apply=yes \
    file://0016-ARM-DT-STiH407-Add-spdif_out-pinctrl-config.patch;apply=yes \
    file://0017-ARM-dts-STiH410-clock-configuration-to-address-720p-.patch;apply=yes \
    file://0018-ARM-dts-STiH410-b2260-Fix-typo-in-spi0-chipselect-de.patch \
    file://0019-ARM-dts-STiH407-family-fix-i2c-nodes.patch;apply=yes \
    file://0020-ARM-dts-STiH410-Add-label-for-sti-hdmi-node.patch \
    file://0021-ARM-dts-STiH410-B2260-clean-unnecessary-hdmi-node-ov.patch \
    file://0022-ARM-dts-STiH407-DT-fix-s-interrupts-names-interrupt-.patch;apply=yes \
    file://0023-ARM-dts-STiH410-B2260-enable-sound-card.patch \
    file://0024-ARM-dts-STiH407-family-set-snps-dis_u3_susphy_quirk.patch;apply=yes \
    file://0025-ARM-dts-STiH410-b2260-Identify-the-UART-RTS-line.patch \
    file://0026-ARM-dts-STiH407-pinctrl-Add-Pinctrl-group-for-HW-flo.patch \
    file://0027-ARM-dts-STiH407-family-Use-new-Pinctrl-groups.patch \
    file://0028-ARM-dts-STiH410-b2260-Enable-HW-flow-control.patch \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "am57xx-evm|intel-core2-32|juno|stih410-b2260"
KERNEL_DEVICETREE_remove_juno = "arm/juno-r2.dtb"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_CONFIG_FRAGMENTS += "\
    ${S}/kernel/configs/distro-overrides.config \
    ${S}/kernel/configs/systemd.config \
"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    touch ${B}/.scmversion ${S}/.scmversion

    # While kernel.bbclass has an architecture mapping, we can't use it because
    # the kernel config file has a different name.
    case "${HOST_ARCH}" in
      aarch64)
        cp ${S}/arch/arm64/configs/defconfig ${B}/.config
        echo 'CONFIG_RTC_DRV_PL031=y' >> ${B}/.config
        echo 'CONFIG_STUB_CLK_HI6220=y' >> ${B}/.config
      ;;
      arm)
        cp ${S}/arch/arm/configs/multi_v7_defconfig ${B}/.config
        echo 'CONFIG_SERIAL_8250_OMAP=y' >> ${B}/.config
        echo 'CONFIG_POSIX_MQUEUE=y' >> ${B}/.config
      ;;
      x86_64)
        cp ${S}/arch/x86/configs/x86_64_defconfig ${B}/.config
        echo 'CONFIG_IGB=y' >> ${B}/.config
      ;;
    esac

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

    # Since kselftest-merge target isn't available, merge the individual
    # selftests config fragments included in the kernel source tree
    ( cd ${WORKDIR} && ${S}/scripts/kconfig/merge_config.sh -m -r -O ${B} ${B}/.config ${S}/tools/testing/selftests/*/config 1>&2 )

    oe_runmake -C ${S} O=${B} olddefconfig

    bbplain "Saving defconfig to:\n${B}/defconfig"
    oe_runmake -C ${B} savedefconfig
}

do_deploy_append() {
    cp -a ${B}/defconfig ${DEPLOYDIR}
    cp -a ${B}/.config ${DEPLOYDIR}/config
    cp -a ${B}/vmlinux ${DEPLOYDIR}
    cp ${T}/log.do_compile ${T}/log.do_compile_kernelmodules ${DEPLOYDIR}
}

require machine-specific-hooks.inc

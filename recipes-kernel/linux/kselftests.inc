PACKAGE_ARCH = "${MACHINE_ARCH}"

# kernel selftests dependencies
DEPENDS += "libcap libcap-ng pkgconfig-native popt rsync-native util-linux \
    ${@bb.utils.contains("TARGET_ARCH", "arm", "", "numactl", d)} \
"

inherit kernel-arch

KSELFTESTS_ARGS = "-C ${S}/tools/testing/selftests INSTALL_PATH=${D}/opt/kselftests CC="${CC}" LD="${LD}" ARCH=${ARCH}"

do_compile_append() {
    # Make sure to install the user space API used by some tests
    # but not properly declared as a build dependency
    ${MAKE} -C ${S} ARCH=${ARCH} headers_install
    ${MAKE} ${KSELFTESTS_ARGS}
}

do_install_append() {
    ${MAKE} ${KSELFTESTS_ARGS} install
    chown -R root:root ${D}
    # fixup run_kselftest.sh due to spurious lines starting by "make[1]:"
    sed -i '/^make/d' ${D}/opt/kselftests/run_kselftest.sh
}

PACKAGES =+ "kernel-selftests"
FILES_kernel-selftests = "/opt/kselftests"

PACKAGES =+ "kernel-selftests-dbg"
FILES_kernel-selftests-dbg = "/opt/kselftests/*/.debug"

RDEPENDS_kernel-selftests = "bash bc ncurses sudo"

INSANE_SKIP_kernel-selftests = "already-stripped"

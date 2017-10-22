DEPENDS = "openssl-native zip-native"

export LDFLAGS=""

do_install() {
    install -D -p -m0644 ${S}/build/${COMPATIBLE_MACHINE}/release/bl1.bin ${D}${libdir}/atf/bl1.bin
    install -D -p -m0644 ${S}/build/${COMPATIBLE_MACHINE}/release/fip.bin ${D}${libdir}/atf/fip.bin
}

FILES_${PN} += "${libdir}/atf/*.bin"

# /usr/lib/atf/bl1.bin not shipped files. [installed-vs-shipped]
INSANE_SKIP_${PN} += "installed-vs-shipped"

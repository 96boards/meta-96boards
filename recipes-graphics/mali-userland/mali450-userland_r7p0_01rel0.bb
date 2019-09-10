SUMMARY = "ARM Mali Utgard GPU User Space driver for HiKey (drm backend)"

VER ?= "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '64', 'hf', d)}"
MALI_TARNAME = "${@bb.utils.contains('VER', '64', 'mali-450_r7p0-01rel0_linux_1arm64.tar.gz', 'mali-450_r7p0-01rel0_linux-armhf_1.tar.gz', d)}"
MALI_DIRNAME = "${@bb.utils.contains('VER', '64', 'mali-450_r7p0-01rel0_linux_1+arm64', 'mali-450_r7p0-01rel0_linux-armhf_1', d)}"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${WORKDIR}/${MALI_DIRNAME}/END_USER_LICENCE_AGREEMENT.txt;md5=3918cc9836ad038c5a090a0280233eea"

# Disable for non-MALI machines
python __anonymous() {
    features = d.getVar("MACHINE_FEATURES", True)
    if not features:
        return
    if "mali450" not in features:
        pkgn = d.getVar("PN", True)
        pkgv = d.getVar("PV", True)
        raise bb.parse.SkipPackage("%s-%s ONLY supports machines with a MALI iGPU" % (pkgn, pkgv))
}

SRC_URI[md5sum] = "118b0307e087345fe7efdf3fe7a69e86"
SRC_URI[sha256sum] = "34d3b15f0f81487a6b4e3680a79b22afaa2ea221eabe9e559523b48a073afee5"
SRC_URI[arm64.md5sum] = "118b0307e087345fe7efdf3fe7a69e86"
SRC_URI[arm64.sha256sum] = "34d3b15f0f81487a6b4e3680a79b22afaa2ea221eabe9e559523b48a073afee5"
SRC_URI[armhf.md5sum] = "9fd39f6d4a9fa2734dbe1201c54fc421"
SRC_URI[armhf.sha256sum] = "48c1b3c9225597626af5c0d32f5584a3e2e283e108eb4715b5479e93adf15c2f"

PROVIDES += "virtual/egl virtual/libgles1 virtual/libgles2"

DEPENDS = "libdrm wayland mesa patchelf-native libffi"
RDEPENDS_${PN} += "libffi"

SRC_URI = " https://developer.arm.com/-/media/Files/downloads/mali-drivers/user-space/hikey/${MALI_TARNAME};name=arm${VER};downloadfilename=${MALI_TARNAME} \
            file://50-mali.rules \
"

S = "${WORKDIR}/${MALI_DIRNAME}/wayland-drm"

# The driver is a set of binary libraries to install
# there's nothing to configure or compile
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -m 755 -d ${D}/${libdir}
    install ${S}/libMali.so ${D}/${libdir}
    patchelf --set-soname libMali.so ${D}${libdir}/libMali.so
    (cd ${D}/${libdir} && ln -sf libMali.so libEGL.so.1.4 \
    && ln -sf libEGL.so.1.4 libEGL.so.1 \
    && ln -sf libEGL.so.1 libEGL.so)
    (cd ${D}/${libdir} && ln -sf libMali.so libGLESv1_CM.so.1.1 \
    && ln -sf libGLESv1_CM.so.1.1 libGLESv1_CM.so.1 \
    && ln -sf libGLESv1_CM.so.1 libGLESv1_CM.so)
    (cd ${D}/${libdir} && ln -sf libMali.so libGLESv2.so.2.0 \
    && ln -sf libGLESv2.so.2.0 libGLESv2.so.2 \
    && ln -sf libGLESv2.so.2 libGLESv2.so)
    (cd ${D}/${libdir} && ln -sf libMali.so libgbm.so.1 \
    && ln -sf libgbm.so.1 libgbm.so)

    install -D -m0644 ${WORKDIR}/50-mali.rules \
        ${D}/${base_prefix}/lib/udev/rules.d/50-mali.rules
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

# The driver tarball has only shared libraries
FILES_${PN} += "${libdir}/*.so* "
# All the packages except ${PN} are empty, including the development package.
# Set the development package files empty to avoid the QA issue error
# ERROR: QA Issue: mali450-userland rdepends on mali450-userland-dev [dev-deps]
FILES_${PN}-dev = ""

INSANE_SKIP_${PN} = "ldflags dev-so file-rdeps"

# The driver is missing EGL/GLES headers and pkgconfig files. Handle
# the conflicts as mesa and the driver are both providing the same shared libraries.
RREPLACES_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm libgles2-mesa libgles1-mesa libgles2-mesa"
RPROVIDES_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm libgles2-mesa libgles1-mesa libgles2-mesa"
RCONFLICTS_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm libgles2-mesa libgles1-mesa libgles2-mesa"

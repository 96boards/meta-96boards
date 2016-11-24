SUMMARY = "ARM Mali Utgard GPU User Space driver for HiKey (drm backend)"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${WORKDIR}/END_USER_LICENCE_AGREEMENT.txt;md5=3918cc9836ad038c5a090a0280233eea"

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

SRC_URI[md5sum] = "36f39e86ccfe5a6a4cb2090865c339ba"
SRC_URI[sha256sum] = "dd136931cdbb309c0ce30297c06f7c6b0a48450f51acbbbc10529d341977f728"
SRC_URI[arm64.md5sum] = "36f39e86ccfe5a6a4cb2090865c339ba"
SRC_URI[arm64.sha256sum] = "dd136931cdbb309c0ce30297c06f7c6b0a48450f51acbbbc10529d341977f728"
SRC_URI[armhf.md5sum] = "50a5585525660ff8e71c8478e88571c3"
SRC_URI[armhf.sha256sum] = "3785359486e52cbad19cd221555452de768e5deae879b24ae9c7ef0f3f3d65ae"

PROVIDES += "virtual/egl virtual/libgles1 virtual/libgles2"

DEPENDS = "libdrm wayland mesa"

VER ?= "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '64', 'hf', d)}"

SRC_URI = " http://malideveloper.arm.com/downloads/drivers/binary/utgard/r6p0-01rel0/mali-450_r6p0-01rel0_linux_1+arm${VER}.tar.gz;destsuffix=mali;name=arm${VER}"

S = "${WORKDIR}/wayland-drm"

# The driver is a set of binary libraries to install
# there's nothing to configure or compile
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -m 755 -d ${D}/${libdir}
    install ${S}/libMali.so ${D}/${libdir}
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
    (cd ${D}/${libdir} && ln -sf libMali.so libwayland-egl.so)
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

# The driver tarball has only shared libraries
FILES_${PN} += "${libdir}/*.so* "
# All the packages except ${PN} are empty, including the development package.
# Set the development package files empty to avoid the QA issue error
# ERROR: QA Issue: mali450-userland rdepends on mali450-userland-dev [dev-deps]
FILES_${PN}-dev = ""

INSANE_SKIP_${PN} = "ldflags dev-so"

# The driver is missing EGL/GLES headers and pkgconfig files. Handle
# the conflicts as mesa and the driver are both providing the same shared libraries.
RREPLACES_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"
RPROVIDES_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"
RCONFLICTS_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"

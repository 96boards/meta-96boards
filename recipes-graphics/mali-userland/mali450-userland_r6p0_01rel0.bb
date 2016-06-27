SUMMARY = "Mali450 libraries (drm backend)"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${WORKDIR}/END_USER_LICENCE_AGREEMENT.txt;md5=3918cc9836ad038c5a090a0280233eea"

COMPATIBLE_MACHINE = "hikey"

SRC_URI[md5sum] = "36f39e86ccfe5a6a4cb2090865c339ba"
SRC_URI[sha256sum] = "dd136931cdbb309c0ce30297c06f7c6b0a48450f51acbbbc10529d341977f728"

PROVIDES += "virtual/egl virtual/libgles1 virtual/libgles2"

DEPENDS = "libdrm wayland mesa"

SRC_URI = " http://malideveloper.arm.com/downloads/drivers/binary/utgard/r6p0-01rel0/mali-450_${PV}-${PR}_linux_1+arm64.tar.gz;destsuffix=mali"

S = "${WORKDIR}/wayland-drm"

# The SRC is just a set of binaries to install - nothing to configure and to
# compile
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

# The mali-450 driver tarball has only *.so files, so all the packages
# except the ${PN} one would be empty
FILES_${PN} += "${libdir}/*.so* "
FILES_${PN}-dev = ""

INSANE_SKIP_${PN} = "ldflags dev-so"

# To get the egl/gles headers and the packageconfig files (missing from this
# mali-450 driver tarball) we have to build mesa, and to handle the conflicts
# due to both mali450-userland, and mesa providing the same libraries.
RREPLACES_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"
RPROVIDES_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"
RCONFLICTS_${PN} = "libegl libegl1 libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"

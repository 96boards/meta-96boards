require recipes-bsp/grub/grub2.inc

DEPENDS += "autogen-native"
DEPENDS_class-target = "grub-native"

DEFAULT_PREFERENCE = "-1"
DEFAULT_PREFERENCE_arm = "1"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/grub-git:"

PV = "2.01+${SRCPV}"
SRCREV = "b524fa27f56381bb0efa4944e36f50265113aee5"
SRC_URI = "git://git.savannah.gnu.org/grub.git \
           file://autogen.sh-exclude-pc.patch \
           file://0001-grub.d-10_linux.in-add-oe-s-kernel-name.patch \
           file://cfg \
          "

S = "${WORKDIR}/git"

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST_armv7a = 'null'

inherit autotools gettext texinfo deploy

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM_arm = "uboot"
GRUBPLATFORM_aarch64 = "efi"
GRUBPLATFORM ??= "pc"

# Determine the target arch for the grub modules
python __anonymous () {
    import re
    target = d.getVar('TARGET_ARCH', True)
    if target == "x86_64":
        grubtarget = 'x86_64'
        grubimage = "bootx64.efi"
    elif re.match('i.86', target):
        grubtarget = 'i386'
        grubimage = "bootia32.efi"
    elif re.match('aarch64', target):
        grubtarget = 'arm64'
        grubimage = "bootaa64.efi"
    else:
        raise bb.parse.SkipPackage("grub-efi is incompatible with target %s" % target)
    d.setVar("GRUB_TARGET", grubtarget)
    d.setVar("GRUB_IMAGE", grubimage)
}

EXTRA_OECONF = "--with-platform=${GRUBPLATFORM} --disable-grub-mkfont --program-prefix="" \
                --enable-liblzma=no --enable-device-mapper=no --enable-libzfs=no"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '--enable-largefile', '--disable-largefile', d)}"

do_configure_prepend() {
    ( cd ${S}
      ${S}/autogen.sh )
}

do_install_append () {
    install -d ${D}${sysconfdir}/grub.d
    rm -rf ${D}${libdir}/charset.alias
}

GRUB_BUILDIN ?= "boot chain configfile echo efinet eval ext2 fat font gettext gfxterm gzio help linux loadenv lsefi normal part_gpt part_msdos read regexp search search_fs_file search_fs_uuid search_label terminal terminfo test tftp time"

do_deploy() {
	# Search for the grub.cfg on the local boot media by using the
	# built in cfg file provided via this recipe
	if [ "${GRUBPLATFORM}" = "efi" ] ; then
		grub-mkimage -c ../cfg -p /EFI/BOOT -d ./grub-core/ \
		               -O ${GRUB_TARGET}-efi -o ./${GRUB_IMAGE} \
		               ${GRUB_BUILDIN}
		install -m 644 ${B}/${GRUB_IMAGE} ${DEPLOYDIR}
	fi
}

do_deploy_class-native() {
	:
}

addtask deploy after do_install before do_build

# debugedit chokes on bare metal binaries
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

RDEPENDS_${PN}_class-target = "diffutils freetype"

INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"

BBCLASSEXTEND = "native"

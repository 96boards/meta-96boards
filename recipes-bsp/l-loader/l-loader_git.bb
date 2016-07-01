SUMMARY = "Loader to switch from aarch32 to aarch64 and boot"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=00ef5534e9238b3296c56a2caa13630c"

do_configure[depends] += "edk2-hikey:do_deploy"

inherit deploy pythonnative

PV = "0.2.2"

SRC_URI = "git://github.com/96boards-hikey/l-loader.git;branch=master \
           file://temp.bin \
          "
SRCREV = "cddd213c1b820d5f224d20b38581c504552dd59e"
S = "${WORKDIR}/git"

do_configure() {
	cp ${WORKDIR}/temp.bin ${S}/temp
	cp ${DEPLOY_DIR_IMAGE}/bl1.bin ${S}
}

do_compile() {
	python gen_loader.py -o l-loader.bin --img_loader=temp --img_bl1=bl1.bin
}

do_install() {
    install -d ${D}/boot
    install -m 0644 l-loader.bin ${D}/boot/
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 l-loader.bin ${DEPLOYDIR}/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
FILES_${PN} += "/boot"

addtask deploy before do_build after do_compile


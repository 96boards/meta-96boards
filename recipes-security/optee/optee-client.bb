SUMMARY = "OPTEE Client"
DESCRIPTION = "OPTEE Client"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=69663ab153298557a59c67a60a743e5b"
PR="r0"
PV="1.0+git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

#inherit autotools pkgconfig

SRC_URI = "git://github.com/OP-TEE/optee_client.git"
S = "${WORKDIR}/git"

SRCREV = "0b0d237779de6982451f6f5971b7106e459a2958"

do_compile() {
    mkdir -p ${D}/usr
    oe_runmake EXPORT_DIR=${D}/usr/
}
do_install() {
    mkdir -p ${D}/usr
    oe_runmake install EXPORT_DIR=${D}/usr

    cd ${D}/usr/lib
    rm libteec.so libteec.so.1
    ln -s libteec.so.1.0 libteec.so.1
    ln -s libteec.so.1.0 libteec.so
    cd -
}


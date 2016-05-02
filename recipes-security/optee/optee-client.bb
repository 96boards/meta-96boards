SUMMARY = "OPTEE Client"
HOMEPAGE = "https://github.com/OP-TEE/optee_client"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=69663ab153298557a59c67a60a743e5b"

PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/OP-TEE/optee_client.git"
S = "${WORKDIR}/git"

SRCREV = "0b0d237779de6982451f6f5971b7106e459a2958"

do_compile() {
    install -d ${D}${prefix}
    oe_runmake EXPORT_DIR=${D}${prefix}/
}

do_install() {
    install -d ${D}${prefix}
    oe_runmake install EXPORT_DIR=${D}${prefix}

    ( cd ${D}${prefix}/lib
      rm libteec.so libteec.so.1
      ln -s libteec.so.1.0 libteec.so.1
      ln -s libteec.so.1.0 libteec.so
    )
}


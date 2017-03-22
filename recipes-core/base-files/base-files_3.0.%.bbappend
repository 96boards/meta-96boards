SRC_URI += "file://profile.serialtty"

do_install_append() {
	install -m 0644 ${WORKDIR}/profile.serialtty ${D}${sysconfdir}/profile
	sed -i 's#ROOTHOME#${ROOT_HOME}#' ${D}${sysconfdir}/profile
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

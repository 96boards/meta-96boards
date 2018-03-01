FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "file://gdb-Fix-ia64-defining-TRAP_HWBKPT-before-including-g.patch"

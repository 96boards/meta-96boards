FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_hikey = "file://bits-siginfo.h-enum-definition-for-TRAP_HWBKPT-is-mi.patch"

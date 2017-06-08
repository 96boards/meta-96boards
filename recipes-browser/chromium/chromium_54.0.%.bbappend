do_compile_append() {
	ninja -C ${S}/out/${CHROMIUM_BUILD_TYPE} ${PARALLEL_MAKE} chromedriver
}

do_install_append() {
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chromedriver" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chromedriver ${D}${bindir}/${BPN}/chromedriver
	fi
}

PACKAGES =+ "${PN}-chromedriver"
FILES_${PN}-chromedriver = "${bindir}/${BPN}/chromedriver"

DESCRIPTION = "A simple hello world application built with a Makefile."
SECTION = "Custom"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6" 

SRC_URI="https://www.blaess.fr/christophe/yocto-lab/files/${BP}.tar.bz2"
SRC_URI[md5sum] = "3bd7b83d110df1517f0e7fa8f0dbea80"

S = "${WORKDIR}/${PN}"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake DESTDIR="${D}${bindir}" install
}


DESCRIPTION = "A simple hello world application built without Makefile."
SECTION = "Custom"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI="https://www.blaess.fr/christophe/yocto-lab/files/${BPN}.c"
SRC_URI[md5sum] = "40ae84f9ff78004561227a42f27c8131"

S = "${WORKDIR}/"

TARGET_CFLAGS += "-DNDEBUG -DMY_OPTION"

do_compile() {
        ${CC} ${CFLAGS} ${LDFLAGS} ${S}/${BPN}.c -o ${WORKDIR}/${BPN}
}

do_install() {
        install -m 0755 -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/${BPN} ${D}${bindir}
}

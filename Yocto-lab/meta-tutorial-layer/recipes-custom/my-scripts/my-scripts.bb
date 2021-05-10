SUMMARY = "Custom scripts for Yocto training"
SECTION = "custom"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI  = "file://createdir"

do_install() {
     install -d ${D}${sbindir}
     install -m 0755 ${WORKDIR}/createdir ${D}${sbindir}
}

FILES_${PN} += "${sbindir}/createdir"


DESCRIPTION = "A simple hello world application built with CMake."
SECTION = "Custom"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6" 

SRC_URI="https://www.blaess.fr/christophe/yocto-lab/files/${BP}.tar.bz2"
SRC_URI[md5sum] = "88c263b9a5d53f3cbfdb24a7f7f4b583"

S = "${WORKDIR}/${PN}"

inherit cmake


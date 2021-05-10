DESCRIPTION = "A simple hello world application built with autotools."
SECTION = "Custom"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6" 

SRC_URI="https://www.blaess.fr/christophe/yocto-lab/files/${BP}.tar.bz2"
SRC_URI[md5sum] = "572c660dcbf1cbd8ac1018baba26f3ea"

S = "${WORKDIR}/${PN}"

inherit autotools

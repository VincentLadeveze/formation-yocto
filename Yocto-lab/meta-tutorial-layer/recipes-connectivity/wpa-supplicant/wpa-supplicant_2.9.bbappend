FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += " file://home-wifi.conf"

do_install_append () {
    cat ${WORKDIR}/home-wifi.conf >> ${D}${sysconfdir}/wpa_supplicant.conf
}




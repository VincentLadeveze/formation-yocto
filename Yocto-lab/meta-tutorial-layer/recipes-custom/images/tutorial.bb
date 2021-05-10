SUMMARY = "A customized image for development purposes."

LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "splash"
IMAGE_FEATURES += "tools-debug"
IMAGE_FEATURES += "tools-profile"
IMAGE_FEATURES += "tools-sdk"
IMAGE_FEATURES += "ssh-server-dropbear"


IMAGE_INSTALL_append = " mc"
IMAGE_INSTALL_append = " nano"

# Plantage § 03.1
# IMAGE_FEATURES += "read-only-rootfs"

# Ajout de scripts
IMAGE_INSTALL_append = " my-scripts"

# Ajout de Python et des modules Python
# Python3:
IMAGE_INSTALL_append = " python3-pip"
IMAGE_INSTALL_append = " python3-modules"
# Python2:
IMAGE_INSTALL_append = " python-py"
IMAGE_INSTALL_append = " python-modules"

IMAGE_INSTALL_append = " python-hello"

IMAGE_INSTALL_append = " hello-autotools"
IMAGE_INSTALL_append = " hello-cmake"
IMAGE_INSTALL_append = " hello-makefile"
IMAGE_INSTALL_append = " hello-simple"

# Réseau
IMAGE_INSTALL_append = " resolvconf"

# Time zone
IMAGE_INSTALL_append = " tzdata"
DEFAULT_TIMEZONE = "Europe/Budapest"

# Launch ntpd on boot 
IMAGE_INSTALL_append = " ntpd-start"

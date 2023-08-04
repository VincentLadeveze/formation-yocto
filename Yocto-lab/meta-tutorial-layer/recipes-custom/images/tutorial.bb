SUMMARY = "A customized image for development purposes."

LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "splash"
IMAGE_FEATURES += "tools-debug"
IMAGE_FEATURES += "tools-profile"
IMAGE_FEATURES += "tools-sdk"
IMAGE_FEATURES += "ssh-server-dropbear"


IMAGE_INSTALL:append = " mc"
IMAGE_INSTALL:append = " nano"

# Plantage § 03.1
# IMAGE_FEATURES += "read-only-rootfs"

# Ajout de scripts
IMAGE_INSTALL:append = " my-scripts"

# Ajout de Python et des modules Python
# Python3:
IMAGE_INSTALL:append = " python3-pip"
IMAGE_INSTALL:append = " python3-modules"
# Python2:
IMAGE_INSTALL:append = " python-py"
IMAGE_INSTALL:append = " python-modules"

IMAGE_INSTALL:append = " python-hello"

IMAGE_INSTALL:append = " hello-autotools"
IMAGE_INSTALL:append = " hello-cmake"
IMAGE_INSTALL:append = " hello-makefile"
IMAGE_INSTALL:append = " hello-simple"

# Réseau
IMAGE_INSTALL:append = " resolvconf"

# Time zone
IMAGE_INSTALL:append = " tzdata"
DEFAULT_TIMEZONE = "Europe/Budapest"

# Launch ntpd on boot 
IMAGE_INSTALL:append = " ntpd-start"

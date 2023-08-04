
## GIT Yocto

BRANCHE:        `gatesgarth`,
TAG:            `yocto-3.2.2 (Poky)`.

## Liste des répertoires:
le 10/05/21

| NOM |  DESCRIPTION REPERTOIRES | DEPOT |
|:---:|:------------------------:|:-----:|
| build-bbb-all@ | Build BeagleBoneBlack (lien symbolique vers disque Storage). | - |
| build-bbb/ | Build BeagleBoneBlack (sauvegarde pour GitHub). | - |
| build-qemu-all@ | Build QEMU (lien symbolique vers disque Storage). | - |
| build-qemu/ | Build QEMU (sauvegarde pour GitHub). | - |
| build-rpi/ | Build Raspberry PI 4 complet. | - |
| downloads/ | Build configuration direcories:   - téléchargement de paquets. | - |
sstate-cache/ | - stockage temporaire au build. | - |
| poky/                   | Kernel + BusyBox minimale du projet Yocto. | git://git.yoctoproject.org/poky |
| meta-openembedded/ | Applications de Open Embedded particulièrement utiles. | git://git.openembedded.org/meta-openembedded |
| meta-python2/ | Layer de Python2 sortis de 'meta-openembedded/' depuis 2020. | git://git.openembedded.org/meta-python2 |
| meta-raspberrypi/ | Layer de Raspberry PI, contient le BSP des principales versions de carte. | git://git.yoctoproject.org/meta-raspberrypi |
| meta-tutorial-layer/ | Layer créé pour ce tutoriel. | - |
| openembedded-core/ | Coeur de OE: non-utilisé ici car je 'build' poky comme base. |   git://git.openembedded.org/openembedded-core/ |

</br>

=> Le stockage des répertoires de `build` est le suivant:

+ soit dans `~/` pour bosser sur `yocto`
+ soit sur le SSD interne `Storage` pour stockage. Dans ce cas, ces répertoires sont des liens symboliques dans `$YOCTO/`

## tree

```
   {*-*} $ cd $YOCTO/..
   {*-*} $ tree -L 2
.
├── cross-toolchain
│   ├── poky-glibc-x86_64-tutorial-armv7vet2hf-neon-qemuarm-toolchain-3.2.2.sh
│   ├── sdk
│   ├── test-cc
│   └── test-cross-cc
├── password-encryption
│   ├── crypt
│   ├── crypt.c
│   ├── crypt.o
│   ├── Makefile
│   └── README.md
└── Yocto-lab
    ├── build-bbb
    ├── build-qemu
    ├── build-rpi
    ├── Documentation
    ├── downloads
    ├── INFO.md
    ├── meta-openembedded
    ├── meta-python2
    ├── meta-raspberrypi
    ├── meta-tutorial-layer
    ├── openembedded-core
    ├── poky
    └── sstate-cache
```

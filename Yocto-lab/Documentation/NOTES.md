Notes
=====

### INTRODUCTION
Notes sur le tutorial de YOCTO par Christophe Blaess, janvier 2020.
L'objectif est de tester l'ensemble des commandes et d'effecuer le tuto sur ma R.PI4 ainsi que ma BeagleBone Black.


### PRE-REQUISITES

#### Hardware config. minimale

Quatre cœurs, 8 Go de RAM et une 100aine de Go de disque.

#### Installs

<https://www.yoctoproject.org/docs/1.8/yocto-project-qs/yocto-project-qs.html>
``` $ apt install gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath socat libsdl1.2-dev xterm ```
> NB:  BitBake requires Python 2.7. For more information on this requirement, see the "Required Git, tar, and Python" section in the Yocto Project Reference Manual.

Perso
``` $ apt install htop tree locate vim```

#### Locales configuration
see <https://wiki.yoctoproject.org/wiki/TipsAndTricks/ResolvingLocaleIssues>

What to do when bitbake says " Sad Locale, Need en_US.UTF-8"
```
$ sudo apt-get install locales
$ sudo dpkg-reconfigure locales 
$ sudo locale-gen en_US.UTF-8
$ sudo update-locale LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8
$ export LANG=en_US.UTF-8
```

> RQ : I used only "export LC_ALL=en_US.UTF-8" and it worked.
        => Now I use both variables.
    

### Chapter 1

#### 01.1 Vocabulaire et concepts
Ok!

#### 01.2 Production d-une image standard
Etape tjs intéressante pour vérifier l'environnement de dev.
```
$ mkdir Yocto-lab && cd Yocto-lab
$ git clone git://git.yoctoproject.org/poky -b gatesgarth
$ cd poky && git tag
# Récupération du dernier tag yocto
$ git checkout yocto-3.2.1
```

```
# Set Environnement: executables et variables d'env. (bitbake sinon introuvable)
$ cd $ROOT_YOCTOLAB
$ source poky/oe-init-build-env build           # crée le répertoire 'build/'
$ pwd
$ROOT_YOCTOLAB/build
```

```
# build depuis le répertoire créée ($ROOT_YOCTOLAB/build)
$ bitbake core-image-minimal
  # OU pour avoir les heures de début et de fin
$ date +%T && bitbake core-image-minimal && date +%T
# Machine ACER NITRO 5: 1h10
```

**Test du build de Poky :**
> ATTENTION: La connection réseau doit-être établis au moment de lancer 'bitbake', il est possible de 'sourcer' le fichier 'oe-init-build-env' avant d'avoir la connection (évident!...)

Résultats:
```
    # Images
    $ ls tmp/deploy/images/qemux86-64/
```

test de l'image produite :
```
  # Install de QEMU pour processeur intel 32 bits
    $ sudo apt install qemu-system-x86
  # Run image
    $ cd $ROOT_YOCTOLAB/build
    $ runqemu qemux86-64
  # Run image dans le même shell (garde le même clavier AZERTY, la précédente cmd garde le clavier par défaut de l'image qui est en QWERTY)
    $ runqemu nographic qemux86-64

  # Capture de la souris dans QEMU : presser simultanément les touches «CTRL», «ALT» et «G». 

  # cmd de base
    #login en 'root' sans mdp
    $ uname -a
    $ cat /proc/cpuinfo
```


#### 01.3 Production d'image pour des cibles spécifiques

**Variable d'environnement 'MACHINE'**
La liste des architecture est disponible dans 
            ```poky/meta/conf/machine/```
et dans 
            ```poky/meta-yocto-bsp/conf/machine/```
également dans
            ```$BUILD_DIR/conf/local.conf```
par la variable ```MACHINE```
> NB:    '??='  => affectation si pas de valeur préalable (surcharge autorisée). Ex: MACHINE ??= "qemux86"   => surchargeable par une ligne de cmd

**Image pour émulateur ARM**
```
    $ cd $ROOT_YOCTOLAB
    $ source poky/oe-init-build-env build
    $ pwd
    $ROOT_YOCTOLAB/build
    $ MACHINE=qemuarm bitbake core-image-minimal

    # répertoire des images :
    $ ls  tmp/deploy/images/

    # installation de QEMU pour ARM
    $ sudo apt install qemu-system-arm

    # Launch QEMU
    $ cd $ROOT_YOCTOLAB/build
    $ runqemu qemuarm
OU
    $ runqemu nographic qemuarm
```


**Image pour BeagleBone Black**
```
    $ cd $ROOT_YOCTOLAB
    $ source poky/oe-init-build-env build-bbb
    $ pwd
    $ROOT_YOCTOLAB/build-bbb
    $ MACHINE=beaglebone-yocto bitbake core-image-minimal

    # ls  tmp/deploy/images/beaglebone-yocto/
                    core-image-minimal-beaglebone-yocto.wic

    # Copier sur la carte SD l'image yocto de la BBB (voir RPI4)
    $ cd tmp/deploy/images/beaglebone-yocto/
    # Où est la SD Card ?
    $ lsblk

    # Copiez avec 'bmaptool' en vérifiant que /dev/mmcblk0 n'est pas occupée par ailleurs
    $ sudo bmaptool copy --bmap core-image-minimal-beaglebone-yocto.wic.bmap core-image-minimal-beaglebone-yocto.wic /dev/mmcblk0

                bmaptool: info: block map format version 2.0
                bmaptool: info: 13827 blocks of size 4096 (54.0 MiB), mapped 5614 blocks (21.9 MiB or 40.6%)
                bmaptool: info: copying image 'core-image-minimal-beaglebone-yocto.wic' to block device '/dev/mmcblk0' using bmap file 'core-image-minimal-beaglebone-yocto.wic.bmap'
                bmaptool: info: 100% copied
                bmaptool: info: synchronizing '/dev/mmcblk0'
                bmaptool: info: copying time: 5.0s, copying speed 4.4 MiB/sec
```

Branchement du câble __*Sertronics*__ *usb-to-ttl* converter :

| Name  |   Board   |  CABLE |
|:-----:|:---------:|:------:|
| GND   |  PIN 1    |   Noir |
|  RX   |  PIN 4    |  Blanc |
|  TX   |  PIN 5    |   Vert |

```
    # Connection à la BBB via le câble usb-to-ttl
    $ picocom -b 115200 /dev/ttyUSB0
            On voit le bootloader démarrer puis le kernel.
            Demande de login apparait (root sans mdp)

    # Connection à la BBB via le câble usb d'alim.
    $ picocom -b 115200 /dev/ttyACM0
            Seule apparait la demande de login (root sans mdp)
```
> NB: Le nom des ports USB est détecté en enlevant le câble usb-to-ttl de l'ordi.


**Image pour R.PI4**
```
    $ cd $ROOT_YOCTOLAB
    # Clone du layer rpi au même niveau que Poky
    $ git clone git://git.yoctoproject.org/meta-raspberrypi -b gatesgarth
    $ source poky/oe-init-build-env build-rpi
    $ pwd
        $ROOT_YOCTOLAB/build-rpi
```
```
    $ bitbake-layers show-layers

    # Ajout du nouveau layer rpi
    $ bitbake-layers add-layer meta-raspberrypi
    # Vérif:
    $ bitbake-layers show-layers
```
```
    # Verification double de l'ajout du layer rpi
    $ bitbake-layers show-layers
    $ vim conf/bblayers.conf
```
```
    # Machine disponible dans Yocto pour RPI4
    $ ls ../meta-raspberrypi/conf/machine/
    # 1iere impression d'après la doc Raspberry PI, pour ma RPI4: MACHINE = raspberrypi4-64
    # Christophe fait le build en 32 bits et possède exactement la même R.PI4 que moi. Donc:
    #    MACHINE = raspberrypi4
```
```
    # Build yocto
    $ date +%T && MACHINE=raspberrypi4-64 bitbake core-image-minimal && date +%T

    # Emplacement binaires
    $ ls tmp/deploy/images/raspberrypi4/
```
```
    # Conseil de $ROOT_YOCTOLAB/meta-raspberrypi/README.md
    #        ==> copier l'image avec bmaptool (tourne en Python3):  bien plus rapide que 'dd'.
    #                   https://wayneoutthere.com/2020/06/19/how-to-bmap-tool-thing-ubuntu/
    $ sudo apt install bmap-tools
    # A l'emplacement des binaires se trouve un fichier .bmap donc inutile d'effectuer un 'bmaptool create'
    #                       NB: Christophe Blaess donne un nom d'image différent mais la bonne image est celle correspondant au fichier BMAP soit 'core-image-minimal-raspberrypi4-64.wic.bz2'
    
    # Copiez avec 'bmaptool' en vérifiant que /dev/mmcblk0 n'est pas occupée par ailleurs
    $ cd $ROOT_YOCTOLAB/tmp/deploy/images/raspberrypi4/
    $ sudo bmaptool copy --bmap core-image-minimal-raspberrypi4.wic.bmap core-image-minimal-raspberrypi4.wic.bz2 /dev/mmcblk0

                bmaptool: info: block map format version 2.0
                bmaptool: info: 23390 blocks of size 4096 (91.4 MiB), mapped 11673 blocks (45.6 MiB or 49.9%)
                bmaptool: info: copying image 'core-image-minimal-raspberrypi4.wic.bz2' to block device '/dev/mmcblk0' using bmap file 'core-image-minimal-raspberrypi4.wic.bmap'
                bmaptool: info: 100% copied
                bmaptool: info: synchronizing '/dev/mmcblk0'
                bmaptool: info: copying time: 4.1s, copying speed 11.1 MiB/sec

    # Insertion de la SD Card dans la RPI4
    # Lancement sur ma RPI4 réussit !!!     :)))
```

### Chapter 2
Début de la compilation avec ```core-image-base```

##### 02.1 Personnalisation de la configuration
```
$ cd $ROOT_YOCTOLAB
$ source poky/oe-init-build-env build
$ pwd
    $ROOT_YOCTOLAB/build
```
``` 
# Build config
  # Fichier local.conf => ajouter en fin ou début
        # <LOGILIN>

        MACHINE = "qemuarm"
        DL_DIR = "${TOPDIR}/../downloads"
        SSTATE_DIR = "${TOPDIR}/../sstate-cache"

        # </LOGILIN>
```
```
  # Build core-image-base (plus riche que 'core-image-minimal')
    $ bitbake core-image-base

  # Run
    $ runqemu qemuarm
    # log as 'root' sans mdp
    $ ps
        => Le nombre de services démarrés au boot semble également un peu plus important qu’auparavant (core-image-minimal)
```

**Utilisateurs et mots de passe**
Par défaut, l'unique utilisateur est 'root' sans mdp.
    
Operations : 
+ fixer un mot de passe pour l’administrateur root (par exemple «linux»)
+ créer un compte utilisateur standard, disons «guest», avec le mot de passe «welcome». 
```
    # Ajouter les lignes ci-dessous dans $ROOT_BUILD/conf/local.conf
        INHERIT += "extrausers"

        EXTRA_USERS_PARAMS += "usermod  -P linux  root;"
        EXTRA_USERS_PARAMS += "useradd  -P welcome  guest;"
```
> NB: Si l'on oublie la ligne 'INHERIT +=...', pas de rebuild effectué par 'bitbake' car le tuto annonce:
  >+ ... notre configuration va pouvoir utiliser les services d’une classe définie dans Poky permettant la personnalisation des utilisateurs. 
  > + On notera la syntaxe «+=» pour indiquer que la chaîne de caractères «extrausers» est ajoutée à la liste des classes contenues dans «INHERIT»." 

```
    # Rebuild - run - check login/mdp
    $ date +%T ; bitbake core-image-base; date +%T
    $ runqemu qemuarm
    # entrer login/mdp pour utilisateur 'root' puis :
        $ exit
    # entrer login/mdp pour utilisateur 'guest' puis :
        $ exit
```
> Pb: on voit le mot de passe en clair dans yocto. 

Comment faire pour crypter le mdp dans '$ROOT_BUILD/conf/local.conf' ?
```
  # Descendre le projet 'password-encryption' en licence GPL de Christophe
    $ git clone https://github.com/cpb-/password-encryption
    $ cd password-encryption/
    $ make
    $ ./crypt linux
            $5$qxxmE6/X$kG0X7YdB25HwJ.wHp.xxsl3Ax2KwmIy0SXVpYtg7d93
    $ ./crypt linux
            $5$FslQUNu6$gLsIwObGULGmsBjmtkcBhxv0XZW0LjumSGUpdA7nOJ5
```
On note que l'encryptage n'est pas le même entre les excutions de './crypt'
```
  # Utiliser l'option '-e' de './crypt' pour permettre de protéger à l’affichage les caractères «$» en les précédant par des backslashes «\».
    $ ./crypt -e linux
        \$5\$eSI9gWpl\$TOzctMrkdSrgysLeE8LrSNnlDW.EzNCH7dPzM02.XUD
    $ ./crypt -e welcome
        \$5\$9eQHTeNL\$3GQbd.J41LQCnQzuIWCn9IWOPLFtwKbtqVPP7IaXA14
```
```
  # Modifier 'EXTRA_USERS_PARAMS' dans 'build/conf/local.conf' en remplaçant l'option '-P' par l'option '-p' et les mots de passe encryptés:
                EXTRA_USERS_PARAMS += "usermod  -p '\$5\$eSI9gWpl\$TOzctMrkdSrgysLeE8LrSNnlDW.EzNCH7dPzM02.XUD' root;"
                EXTRA_USERS_PARAMS += "useradd  -p '\$5\$9eQHTeNL\$3GQbd.J41LQCnQzuIWCn9IWOPLFtwKbtqVPP7IaXA14' guest;"
```
>  Ne pas oublier de mettre entre 2 ' le mot de passe crypté.
```
    # Re-build - Re-run - Re-test login/mdp
    $ date +%T ; bitbake core-image-base; date +%T
            #   => vérifier que 'bitbake' effectue un re-build !!
    $ runqemu qemuarm
```
_Ca marche !!!_


**Hostname**
```
  # Par défaut, le hostname est celui du 'nom' de 'runqemu':
            Poky (Yocto Project Reference Distro) 3.2.1 qemuarm /dev/ttyAMA0

            qemuarm login: root
            Password: 
            root@qemuarm:~# hostname
            qemuarm

  # Changement de HOSTNAME
    # Ajouter la ligne ci-dessous dans $ROOT_BUILD/conf/local.conf
        hostname_pn-base-files = "mybox"    
```

#### 02.2 Ajout d'applications dans l'image

**Utilitaires présents dans Poky**
```
  # Liste des recettes de Poky
    $ ls  ../poky/meta/recipes*
                ../poky/meta/recipes-bsp:
                ../poky/meta/recipes-connectivity:
                ../poky/meta/recipes-core:
                ../poky/meta/recipes-devtools:
                ../poky/meta/recipes-extended:
                ../poky/meta/recipes-gnome:
                ../poky/meta/recipes-graphics:
                ../poky/meta/recipes-kernel:
                ../poky/meta/recipes-multimedia:
                ../poky/meta/recipes-rt:
                ../poky/meta/recipes-sato:
                ../poky/meta/recipes-support:
```
```
  # Nombre de recette de Poky
    $ find ../poky/meta/recipes* -name '*bb'| wc -l
782
```
```
  # Ajout de Midnight Commander
    $ vim $ROOT_BUILD/conf/local.conf
            IMAGE_INSTALL_append = " mc"

        # Possibilité de ajouter en front:
        IMAGE_INSTALL_prepend = "mc "

  # Build - run
    $ date +%T ; bitbake core-image-base; date +%T
    $ runqemu qemuarm
  # Jouer avec 'mc' après login
    > mc
```
```
  # Ajout d'autres 'apply'
    $ vim $ROOT_BUILD/conf/local.conf
            IMAGE_INSTALL_append = " strace"
            IMAGE_INSTALL_append = " gdbserver powertop"
```
> NB: 'valgrind' ne fonctionne pas sous 'qemuarm' mais marche très bien sous RPI.

```
  # Build - run
    $ date +%T ; bitbake core-image-base; date +%T
    $ runqemu qemuarm

    # Jouer avec  après login
    > strace -V
                strace -- version 5.8
                Copyright (c) 1991-2020 The strace developers <https://strace.io>.
                This is free software; see the source for copying conditions.  There is NO
                warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

                Optional features enabled: (none)
```

**Ajout de packages hors Poky: install de l'éditeur nano**
```
  # Récupération de meta-openembedded
    $ cd $ROOT_YOCTOLAB/
    $ git clone git://git.openembedded.org/meta-openembedded -b gatesgarth
```
> NB: Il est à noter que poky possède déjà open-embedded: $ ls poky/meta-openembedded/
          ==> Cloner n'est pass forcément utile ?...
```
  # Ajout du layer meta-oe
    $ bitbake-layers add-layer ../meta-openembedded/meta-oe/

  # build yocto
    $ cd $ROOT_YOCTOLAB/
    $ source poky/oe-init-build-env build
    $ date +%T ; bitbake core-image-base; date +%T
```

*Plantage BRANCHE "yocto-3.2.1":*
```
18:17:29
Loading cache: 100% |###############################################################################################################################################################################| Time: 0:00:00
Loaded 2217 entries from dependency cache.
ERROR: ParseError at /home/ladeveze/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/meta-openembedded/meta-oe/recipes-dbs/postgresql/postgresql.inc:39: Could not inherit file classes/python3targetconfig.bbclass

Summary: There was 1 ERROR message shown, returning a non-zero exit code.
18:17:31

Tentatives ko:
    - mettre 'meta-openembedded/' dans 'poky/' et re-build => même erreur
    - re-build avec 'core-image-sato' (possède python3) mais pb identique.
    - descendre 'openembedded-core/' (possède classe/python3targetconfig.bbclass), l'ajouter avec 'meta-openembedded/' et re-build => plante à cause de 'core' dans le nom du répertoire (n'accepte pas 2 cores).
```

_Résolution plantage :_
```
BRANCHE yocto-3.2.2 et build avant il'ajout d'application dans l'image.
    NB:         ./meta/classes/python3targetconfig.bbclass  est maintenant dans le tag 'yocto-3.2.2 présente dans Poky et son absence dans le tag 'yocto-3.2.1' provoquait le plantage

   {*-*} $ git branch
* (HEAD detached at yocto-3.2.2)
  gatesgarth
ladeveze@pc-vincent:~/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/poky/
   {*-*} $ find . -iname "*.bbclass" |grep python
./meta-openembedded/meta-python/classes/bandit.bbclass
./meta/classes/python3targetconfig.bbclass
./meta/classes/python3-dir.bbclass
./meta/classes/python3native.bbclass
ladeveze@pc-vincent:~/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/poky/
   {*-*} $ git checkout yocto-3.2.1
Previous HEAD position was d5d6286a66 build-appliance-image: Update to gatesgarth head revision
HEAD is now at 943ef2fad8 build-appliance-image: Update to gatesgarth head revision
ladeveze@pc-vincent:~/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/poky/
   {*-*} $ find . -iname "*.bbclass" |grep python
./meta-openembedded/meta-python/classes/bandit.bbclass
./meta/classes/python3-dir.bbclass
./meta/classes/python3native.bbclass

    Déroulé:    - add layer meta-oe + build
                - add mc + build
                - add strace, gdbserver, powertop + build
                - add tools-sdk + build
                - add nano + build

# Utilisation des fonctionnalités d’image:      les outils de compilation

  # Fichier de description du build 'core-image-base'
    $ find ../poky/ -name core-image-base.bb
                ../poky/meta/recipes-core/images/core-image-base.bb
    $ cat ../poky/meta/recipes-core/images/core-image-base.bb
                        «inherit core-image» : la recette hérite d’une classe prédéfinie qui décrit le contenu d’une image en proposant des fonctionnalités optionnelles.
                        «IMAGE_FEATURES += "splash"»  ajoute la fonctionnalité splashscreen

  # Les fonctionnalités proposées par la classe «core-image» est implémentée dans le fichier poky/meta/classes/core-image.bbclass
                        x11, x11-base, x11-sato, tools-debug, eclipse-debug, tools-profile, tools-testapps, tools-sdk, [...]

  # Ajout dans 'conf/local.conf ' des outils de compil.
    IMAGE_FEATURES += "tools-sdk"
    
  # Build - run
    $ date +%T ; bitbake core-image-base; date +%T
    $ runqemu qemuarm

                    root@enzo:~# gcc --version
                    gcc (GCC) 10.2.0
                    Copyright (C) 2020 Free Software Foundation, Inc.
                    This is free software; see the source for copying conditions.  There is NO
                    warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

                    root@enzo:~# vi my-hello.c
                    root@enzo:~# gcc my-hello.c -o my-hello -Wall 
                       #include <stdio.h>
                       #include <unistd.h>

                       int main(void)
                       {
                         char host[512];  
                         gethostname(host, 512);
                         printf("Hello from %s\n", host);
                         return 0;
                       }
                    root@enzo:~# ./my-hello 
                    Hello from enzo
                    root@enzo:~# g++ --version
                    g++ (GCC) 10.2.0
                    Copyright (C) 2020 Free Software Foundation, Inc.
                    This is free software; see the source for copying conditions.  There is NO
                    warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
```

**Création d’une image spécifique**
Le but est de remplacer les 'IMAGE_INSTALL_append' et les 'IMAGE_FEATURES' de conf/local.conf et de les placer dans notre propre layer.
```
  # Création du layer customisé
    $ cd $BUILD_ROOT/
    $ bitbake-layers create-layer ../meta-tutorial-layer
    $ ls ../
            # Création du répertoire 'meta-tutorial-layer/'
    $ bitbake-layers show-layers
            # Ce nouveau layer n'apparait pas encore dans 'conf/bblayers.conf'

  # Ajout de ce layer
    $ bitbake-layers add-layer ../meta-tutorial-layer
    $ bitbake-layers show-layers
            # Nouveau layer inclu dans 'conf/bblayers.conf'


  # Création du répertoire des recettes customisées et des recettes de notre image
    $ mkdir ../meta-tutorial-layer/recipes-custom/
    $ mkdir ../meta-tutorial-layer/recipes-custom/images/

  # Copie de la recette de 'core-image-base.bb' dans la recette de custom de l'image 'tutorial.bb'
    $ cp ../poky/meta/recipes-core/images/core-image-base.bb ../meta-tutorial-layer/recipes-custom/images/tutorial.bb

  # supprimer toutes les lignes «IMAGE_INSTALL_append» et «IMAGE_FEATURES» dans 'conf/local.conf'
  # Remplacer les lignes de 'tutorial.bb' par :

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


  # Build + exec
    $ date +%T ; bitbake tutorial; date +%T
    $ runqemu qemuarm

            root@enzo:~# strace -V
            strace -- version 5.8
            Copyright (c) 1991-2020 The strace developers <https://strace.io>.
            This is free software; see the source for copying conditions.  There is NO
            warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
```
NB:
```
    $ runqemu qemuarm core-image-base       # Lance zImage avec rootfs et conf de 'core-image-base'
    $ runqemu qemuarm tutorial              # Ca plante
    $ runqemu tutorial qemuarm              # Ca plante
    $ runqemu qemuarm                       # Lance zImage avec rootfs et conf de 'tutorial' (dernier compilé)
```

#### 02.3 Personnalisation des recettes

*Adaptation d’une recette de Poky*
    RAS
*Ajout d'un patch dans une recette (patch d'un fichier présent dans une recette : pas de compilation mais build avec 'bitbake')*
    RAS
*Patch sur un fichier source de package (patch s’appliquant sur un fichier source d’un package téléchargé et compilé par une recette). Utilisation de ```recipetool```*.
    RAS

### Chapter 3

#### 03.1 Ajouter des scripts ou des fichiers de données

```
# Système de fichier en lecture seule
    $ vim ../meta-tutorial-layer/recipes-custom/images/tutorial.bb
                [...]
                IMAGE_FEATURES += "read-only-rootfs"

    $ date +%T; bitbake tutorial; date +%T
```
_**KO  !!!**_

Voir log dans ```tmp/work/qemuarm-poky-linux-gnueabi/tutorial/1.0-r0/temp/log.```

```do_rootfs.31401
ERROR: The following packages could not be configured offline and rootfs is read-only: ['100-sysvinit-inittab']
```

> Le package '100-sysvinit-inittab' a certainement des trucs à installer en post install. Il setrouve maintenant que le rootfs est en read-only ce qui le bloque.
        1) Vérifier si le package en 'pkg_postinst'.
        2) Ré-écritre la recette pour voir si la package peut-être exécuté à la création de rootfs.


**Intégration de scripts shells**
PROLEME = Ch. Blaess propose d'intégrer les lignes de commandes permettant un accès  R/W de rootfs après l'avoir positionné en ready-only dans yocto. Mais impossible de mettre rootfs en ready-only dans yocto
WORKAROUND = Créer un script qui fait: ```$ mkdir -p /home/root/workaround/de/mon/plantage```

```
    $ mkdir -p ../meta-tutorial-layer/recipes-custom/my-scripts/files
    $ vim ../meta-tutorial-layer/recipes-custom/my-scripts/files/createdir
                            #!/bin/sh
                            mkdir -p /home/root/workaround/de/mon/plantage

    $ vim ../meta-tutorial-layer/recipes-custom/my-scripts/my-scripts.bb
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

    $ vim ../meta-tutorial-layer/recipes-custom/images/tutorial.bb
                        [...]
                        IMAGE_INSTALL_append = " my-scripts"

    # Build & run
    $ date +%T; bitbake tutorial; date +%T
    $ runqemu nographic qemuarm

    # Après login en root:
            # pwd
            /home/root/
            # ls -al
            drwx------ 2 root root 1024 Mar  9 15:34 .
            drwxr-xr-x 4 root root 1024 Mar  9  2018 ..
            -rw------- 1 root root    0 Mar  9 15:16 .bash_history

            # ls -al /usr/sbin/createdir 
            -rwxr-xr-x 1 root root 58 Mar  9  2018 /usr/sbin/createdir
            # createdir
            # cd workaround/de/mon/plantage/
            # pwd
            /home/root/workaround/de/mon/plantage

                    # EVERYTHINGS FINE !!!

# Scripts et modules Python
  # Python3 est par défaut dans 'tools-sdk', fonctionnalité installée précédement
    $ vim ../meta-tutorial-layer/recipes-custom/images/tutorial.bb
                [...]
                IMAGE_INSTALL_append = " python3-modules"       
                IMAGE_INSTALL_append = " python3-pip"


  # ATTENTION:   - Dans la formation, c'est 'IMAGE_INSTALL_append = " python-modules"' et non 'python3-modules'
                 - Python2 est sortis de 'meta-openembedded' et existe comme layer à part entière sous le nom de 'meta-python2'


  # Ajout de Python2
    $ cd $YOCTOLAB
    $ git clone git://git.openembedded.org/meta-python2 -b gatesgarth
    $ bitbake-layers add-layer meta-python2/

    $ vim meta-tutorial-layer/recipes-custom/images/tutorial.bb
                [...]
                IMAGE_INSTALL_append = " python-py"                 # installe Python2
                IMAGE_INSTALL_append = " python-modules"            # installe des modules python

  # Build & run
    $ date +%T; bitbake tutorial; date +%T
    $ runqemu nographic qemuarm

  # log as root
    # python -V
    Python 2.7.18
    # python3 -V
    Python 3.8.5
    
  # ajout d'un script Python
    $ cd ../meta-tutorial-layer/recipes-custom/
    $ mkdir -p python-hello/files/ && cd python-hello/files/
    $ vim python-hello.py

                #! /usr/bin/python
                #
                # Christophe BLAESS 2020.
                #
                # Licence MIT.
                #
                from __future__ import print_function
                import socket
                import sys
                print("Python", sys.version[0:3], "says 'Hello' from", socket.gethostname())

    $ cd ..
    $  vim python-hello_1.0.b

                SUMMARY = "Python Hello World for Yocto tutotial"
                SECTION = "custom"

                LICENSE = "MIT"
                LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

                SRC_URI  = "file://python-hello.py"

                do_install() {
                    install -d ${D}${bindir}
                    install -m 0755 ${WORKDIR}/python-hello.py ${D}${bindir}
                }

                FILES_${PN} += "${bindir}/python-hello.py"

                RDEPENDS_python-hello += "python"
                RDEPENDS_python-hello += "python-modules"

    $ cd $BUILD_DIR
    $ vim ../meta-tutorial-layer/recipes-custom/images/tutorial.bb
            [...]
            IMAGE_INSTALL_append = " python-hello"

  # Build & run
    $ date +%T; bitbake tutorial; date +%T
    $ runqemu nographic qemuarm

  # log as root
    # python-hello.py 
    Python 2.7 says 'Hello' from mybox
    # whereis python-hello.py
    python-hello: /usr/bin/python-hello.py
```


#### 03.2 Compiler une application métier en dehors de Yocto

```
  # Extraction de la toolchain (1H et quelques)
    $ bitbake -c populate_sdk tutorial
    $ ls tmp/deploy/sdk/

  # Extraire le sdk dans un répertoire dédié.
    $ export CROSSCOMPILER_DIRECTORY=~/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/cross-toolchain/

    $ cp $YOCTOLAB/build-qemu/tmp/deploy/sdk/poky-glibc-x86_64-tutorial-armv7vet2hf-neon-qemuarm-toolchain-3.2.2.sh $CROSSCOMPILER_DIRECTORY
    $ cd $CROSSCOMPILER_DIRECTORY
    $ ./poky-glibc-x86_64-tutorial-armv7vet2hf-neon-qemuarm-toolchain-3.2.2.sh

                Poky (Yocto Project Reference Distro) SDK installer version 3.0.1
                Enter target directory for SDK (default: /opt/poky/3.0.1): $CROSSCOMPILER_DIRECTORY/sdk
                You are about to install the SDK to "$CROSSCOMPILER_DIRECTORY/sdk". Proceed [Y/n]? y

  # Verifier les variables ci-dessous avant et après 'sourcing' du fichier 'environment-setup-armv7vet2hf-neon-poky-linux-gnueabi'
    $ echo $PATH
    $ echo $ARCH
    $ echo $CROSS_COMPILE
    $ echo $CC
    $ echo $CFLAGS
    $ echo $CXX
    $ echo $CXXFLAGS
    $ echo $LD
    $ echo $LDFLAGS
    $ echo $AS
    $ echo $GDB

    $ source sdk/environment-setup-armv7vet2hf-neon-poky-linux-gnueabi


  # Test de compilation natif et croisé à l'extérieur de Yocto:
            - Manuel
            - Makefile
            - Autotools
            - CMake
  # respectivement dans les répertoires
                        $CROSSCOMPILER_DIRECTORY/

                                    ├── poky-glibc-x86_64-tutorial-armv7vet2hf-neon-qemuarm-toolchain-3.2.2.sh
                                    ├── sdk
                                    │   ├── environment-setup-armv7vet2hf-neon-poky-linux-gnueabi
                                    │   ├── site-config-armv7vet2hf-neon-poky-linux-gnueabi
                                    │   ├── sysroots
                                    │   └── version-armv7vet2hf-neon-poky-linux-gnueabi
                                    ├── test-cc
                                    │   ├── 01_manuelle
                                    │   ├── 02_Makefile
                                    │   ├── 03_autotools
                                    │   ├── 04_cmake
                                    │   └── README
                                    └── test-cross-cc
                                        ├── 01_manuelle
                                        ├── 02_Makefile
                                        ├── 03_autotools
                                        ├── 04_cmake
                                        └── README
```

> Notes sur Makefile:
> + Plante pour des histoires d'indentation. Erreur = "Makefile:12: *** missing separator.  Stop."
> + Changer ligne de compilation:  "```$(CC) $(CFLAGS) -o $@ -c $^```"
> + commande make:
>    * $ make          # en natif*
>    * $ make -e       # en croisé: prend les varaiables d'environnement et laisse tombé les variables internes au Makefile*
                                    
####  03.3 Intégrer notre application dans la production de Yocto
```
  # Recette utilisant les Autotools
    $ mkdir ../meta-tutorial-layer/recipes-custom/hello-autotools
    $ vim ../meta-tutorial-layer/recipes-custom/hello-autotools/hello-autotools_1.0.bb

                DESCRIPTION = "A simple hello world application built with autotools."
                SECTION = "Custom"

                LICENSE = "GPLv2"
                LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6" 

                SRC_URI="https://www.blaess.fr/christophe/yocto-lab/files/${BP}.tar.bz2"
                SRC_URI[md5sum] = "572c660dcbf1cbd8ac1018baba26f3ea"

                S = "${WORKDIR}/${PN}"

                inherit autotools


    $ vim ../meta-tutorial-layer/recipes-custom/images/tutorial.bb 
                    [...]
                    IMAGE_INSTALL_append = " hello-autotools"

  # Les autres examples de recettes utilisent cmake, make, ligne de commande
        ==> Pas de difficulté particulière.
```

> TODO: RECETTES sont intéressantes à étudier de près !!!

### Chapter 4

Ajuster l'image Yocto pour une cible personnalisée

#### 04.1 Configuration du réseau

```
  # Build
    $ source poky/oe-init-build-env build-rpi
    $ date +%T; nice bitbake tutorial; date +%T

  # Copie sur la SD Card
  # Démonter les partitions 'root' et 'boot' de la SD Card avant de copier
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

                    bmaptool: info: block map format version 2.0
                    bmaptool: info: 206029 blocks of size 4096 (804.8 MiB), mapped 125059 blocks (488.5 MiB or 60.7%)
                    bmaptool: info: copying image 'tutorial-raspberrypi4.wic.bz2' to block device '/dev/mmcblk0' using bmap file 'tutorial-raspberrypi4.wic.bmap'
                    bmaptool: info: 100% copied
                    bmaptool: info: synchronizing '/dev/mmcblk0'
                    bmaptool: info: copying time: 30.6s, copying speed 15.9 MiB/sec
```

La configuration initiale du réseau se fait en Ethernet et est testée sur ma RPI4.

*TROUBLE :*
Ethernet testé (clignotement côté FreeBox) avec mon PC mais PAS AVEC MA RPI4...

*WORKAROUND :*
Utilisation du Wifi (le Wifi est testé après install de Raspberry PI OS avec SSID/Mdp de la Freebox)

##### 04.1.1 WPA Supplicant Nasty Wifi configuration  -     *Static IP Address*

###### a) Test Manuel

```
            # Manuelle
                # booter RPI et connection en 'root'

                # Définir 'SSID/PWD' à la configuration de 'wpa_supplicant'
                $ vi /etc/wpa_supplicant/wpa_supplicant.conf

                                ctrl_interface = /var/run/wpa_supplicant
                                ctrl_interface_group = 0
                                update_config = 1
                                 
                                Network = {
                                               ssid = "SSID"
                                               psk = "PASSWD"
                                }

                # Lancer les commandes suivantes à la main:
                      # Créer l'interface avec 1 @IP statique
                        $ ifconfig wlan0 192.168.1.30

                      # Définir @IP du routeur
                        $ route add default gw 192.168.1.254

                      # Activer l'interface avec la cmd 'wpa_supplicant'
                        $ wpa_supplicant -B -i wlan0 -c /etc/wpa_supplicant.conf

                # Test depuis ma machine
                $ ssh root@192.168.1.30
```

*CA FONCTIONNE !!!!*

###### b) Automatisation

```
            # YOCTO
                # Créer un script exécutant les commandes ci-dessus au démarrage dans 'init.d/' au travers de 'initscripts'
                # Voir :    https://reiwaembedded.com/raspberry-pi-yocto-wifi-configuration-for-automatic-connection-at-boot/
                            https://carmalou.com/how-to/2017/08/16/how-to-generate-passcode-for-raspberry-pi.html
                            http://git.yoctoproject.org/cgit/cgit.cgi/meta-intel/tree/meta-nuc/recipes-core/initscripts/initscripts_1.0.bbappend?h=dylan

                      # Trouver la recette de 'initscripts' dans 'Poky'
                        $ cd $POKY
                        $ find . -iname "*initscript*"
                                [...]
                                ./meta/recipes-core/initscripts/initscripts_1.0.bb

                      # Création des répertoires de 'initscripts' dans notre image
                        $ mkdir -p $YOCTOLAB/meta-tutorial-layer/recipes-core/initscripts/files/ && cd $YOCTOLAB/meta-tutorial-layer/recipes-core/initscripts/files/ 
                      
                      # Ajout le script permettant la config. du Wifi
                        $ vim setup-wifi.sh 

                                    #!/bin/sh

                                    # Set wlan0 interface with static IP address, should be unique
                                    ifconfig wlan0 192.168.1.70

                                    # Change SSID and PASSWD to 
                                    <my-wifi-password> > /etc/wpa_supplicant.conf

                                    # Router IP address
                                    route add default gw .254

                                    # Launch interface wlan0 with config
                                    wpa_supplicant -B -i   wlan0    -c    /etc/wpa_supplicant.conf

                                    echo “nameserver 8.8.8.8” >> /etc/resolv.conf
                                    echo “nameserver 192.168.1.254” >> /etc/resolv.conf

                                    exit 0

                      # Création de la recette de surcharge (.bbappend) de 'initscripts'
                        $ cd $YOCTOLAB/meta-tutorial-layer/recipes-core/initscripts/
                        $ vim initscripts_1.0.bbappend

                                                FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

                                                SRC_URI = "file://setup-wifi.sh"

                                                do_install_append () {
                                                    install -d ${D}${sysconfdir}/init.d
                                                    install -m 0755 ${WORKDIR}/setup-wifi.sh ${D}${sysconfdir}/init.d
                                                    update-rc.d -r ${D} setup-wifi.sh start 99 2 3 4 5 .
                                                }
                                                  
                                                MASKED_SCRIPTS = "setup-wifi"

                      # Build et copie sur la SD Card
                        $ cd $BUILD_RPI
                        $ date +%T; nice bitbake tutorial; date +%T 
                        $ cd ./tmp/deploy/images/raspberrypi4/
                      # Démonter les partitions 'root' et 'boot' de la SD Card avant de copier
                        $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0


                # Test depuis ma machine
                $ ssh root@192.168.1.70
```

*CA FONCTIONNE !!!!*

> NB :
>
> + Le mot de passe est en clair dans le script setup-wifi.sh' et dans '/etc/wpa_supplicant.conf'
>                         ==> C'est une solution non sûre et donc de dépannage pour la R&D...
> + 'ssh' ok mais pas d'accès à internet.
> + adresse IP statique.


##### 04.1.2 Network Interfaces Wifi configuration - _Dynamic IP Address_**


######  a) Test Manuel

Passer ID et PSWD à ```/etc/wpa_supplicant.conf```

```
              # Modifier l'interface
                $ vi /etc/network/interfaces
                        [...]
                        auto wlan0                  # ligne à ajouter
                        iface wlan0 inet dhcp
                        [...]

              # Lancer
                $ ifconfig wlan0 up

              # Test internet
                ping 8.8.8.8

              # Test ssh
                $ ifconfig                      # depuis R.PI4
                    @IP = 192.18.1.49
                $ ssh root@192.168.1.49         # depuis pc-vincent
                $ ping 8.8.8.8
                
              #   TOUT FONCTIONNE !!!!

            # YOCTO
                # Ajout au build yocto:
                       - SSID/PSWD à '/etc/wpa_supplicant.conf'
                       - Activation automatique de 'wlan0' dans '/etc/network/interfaces'
```

###### b) wpa_supplicant dans Yocto

```
                $ cd $YOCTOLAB/
              # Recherche de 'wpa_supplicant' dans Yocto
                $ devtool search wpa_supplicant
                            devtool search wpa_supplicant
                            NOTE: Starting bitbake server...
                            NOTE: Reconnecting to bitbake server...
                            NOTE: Retrying server connection (#1)...
                            Loading cache: 100% |##########################################################################################################################| Time: 0:00:00
                            Loaded 2988 entries from dependency cache.
                            wpa-supplicant        Client for Wi-Fi Protected Access (WPA)

                $ find ./poky/ -iname "*.bb*" |grep -i wpa-supplicant
                                        meta/recipes-connectivity/wpa-supplicant/wpa-supplicant_2.9.bb
    
              # Création du répertoire pour modifier la récette dans mon image
                $ mkdir -p meta-tutorial-layer/recipes-connectivity/wpa-supplicant/files && cd meta-tutorial-layer/recipes-connectivity/wpa-supplicant/files

              # Codage du mot de passe wifi avec 'wpa_supplicant'  (à tester manuellement d'abord)
                $ wpa_passphrase <SSID> <my-wifi-password> > home-wifi.conf
              # Supprimer le mot de passe visible en clair
                $ vim home-wifi.conf
                                network={
                                     ssid="<SSID>"
                                     psk=276150f631deebd0a468bfd43953a6e0b87adc8348c8bfba12345b91c9fb0ab0
                                }

              # Création de la recette "d'overlay" de 'wpa-supplicant'
                $ cd ..
                $ vim wpa-supplicant_2.9.bbappend

                            FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
                            SRC_URI += " file://home-wifi.conf"

                            do_install_append () {
                                cat ${WORKDIR}/home-wifi.conf >> ${D}${sysconfdir}/wpa_supplicant.conf
                            }
```

###### c) Interfaces réseau dans Yocto

```
              # Recherche des interfaces réseau dans Yocto
                $ devtool search /etc/network/interfaces
                                NOTE: Starting bitbake server...
                                NOTE: Reconnecting to bitbake server...
                                NOTE: Retrying server connection (#1)...
                                Loading cache: 100% |######################################################################################################################| Time: 0:00:00
                                Loaded 2988 entries from dependency cache.
                                init-ifupdown         Basic TCP/IP networking init scripts and configuration files

                $ cd $YOCTOLAB/
                $ find ./poky/ -iname "*.bb*" |grep -i init-ifupdown
                                        ./poky/meta/recipes-core/init-ifupdown/init-ifupdown_1.0.bb
```

###### d) Ajout automatique de l'interface Wifi

**METHODE 1**

```
              # Création du répertoire pour modifier la récette dans mon image
                $ mkdir meta-tutorial-layer/recipes-core/init-ifupdown/ && cd meta-tutorial-layer/recipes-core/init-ifupdown/

              # Ajout de 'wlan0' dans les interfaces montées au démarrage:
                $ vim init-ifupdown_1.0.bbappend

                                                # Ensure wlan0 is set to auto
                                                #

                                                do_install_append () {
                                                    echo ' auto wlan0' >> ${D}${sysconfdir}/network/interfaces
                                                }
```

**METHODE 2**

```
                $ tree $YOCTOLAB/poky/meta/recipes-core/init-ifupdown/
                                    ../poky/meta/recipes-core/init-ifupdown/
                                    ├── init-ifupdown-1.0
                                    │   ├── copyright
                                    │   ├── init
                                    │   ├── interfaces
                                    │   ├── nfsroot
                                    │   └── qemuall
                                    │       └── interfaces
                                    └── init-ifupdown_1.0.bb

              # Création dans l'image custom du répertoire contenant la recette et du sous-répertoire contenant le fichier d'interface
                $ mkdir -p $YOCTOLAB/meta-tutorial-layer/recipes-core/init-ifupdown/init-ifupdown-1.0 && cd $YOCTOLAB/meta-tutorial-layer/recipes-core/init-ifupdown/init-ifupdown-1.0/

                $ cp $YOCTOLAB/poky/meta/recipes-core/init-ifupdown/init-ifupdown-1.0/interfaces . 
                $ vim interfaces  

                                                [...]
                                                # Wireless interfaces
                                                auto wlan0
                                                iface wlan0 inet dhcp
                                                [...]


                $ cd ..
                $ vim init-ifupdown_1.0.bbappend

                                FILESEXTRAPATHS_prepend := "${THISDIR}/init-ifupdown-1.0:"                                      # remplacer 'init-ifupdown-1.0' par 'files' fonctionne !
                                SRC_URI  += "file://interfaces"

                                # Ensure wlan0 is set to auto
                                #

                                do_install_append () {
                                    install -m 0644 ${WORKDIR}/interfaces ${D}${sysconfdir}/network/interfaces                  # Pris dans la recette 'Poky de 'init-ifupdown_1.0'
                                }

              # Build et copie sur la SD Card
                $ cd $BUILD_RPI
                $ date +%T; nice bitbake tutorial; date +%T 
                $ cd ./tmp/deploy/images/raspberrypi4/
              # Démonter les partitions 'root' et 'boot' de la SD Card avant de copier
                $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

                # Test depuis ma machine
                $ ssh root@192.168.1.49
                $ ping 8.8.8.8
```

*CA FONCTIONNE AVEC LES 2 METHODES  !!!!*

> NB:     Meilleure solution que le script utilisant 'wpa_supplicant' mais pas encore optimum pour la sécurité.


##### 04.1.3 Accès internet via DNS

```
    $ ping www.kernel.org               # Ko initialement

    $ vim $YOCTOLAB/meta-tutorial-layer/recipes-custom/images/tutorial.bb
                [...]
                IMAGE_INSTALL_append = " resolvconf"

    # Build et copie sur la SD Card
    $ cd $BUILD_RPI
    $ date +%T; nice bitbake tutorial; date +%T 
    $ cd ./tmp/deploy/images/raspberrypi4/
    # Démonter les partitions 'root' et 'boot' de la SD Card avant de copier
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

    # Test depuis ma machine
    $ ssh root@192.168.1.49

    $ ping www.kernel.org               # OK !


  # Recherche de la recette responsable d'un fichier
    $ devtool search /etc/network/interfaces                # Vu pour le Wifi, confuguration "propre"

  # Adresse IP statique utilisant 'interfaces'     ==> identique à n'importe quel hardware: embarqué, desktop, serveur, vm, etc.
    # Mettre en oeuvre la méthode 2 de configuration du Wifi avec 'interfaces'
    $ cd $YOCTOLAB/meta-tutorial-layer/recipes-core/init-ifupdown/init-ifupdown-1.0/
    $ vim interfaces
                [...]
                # Wireless interfaces
                auto wlan0
                #iface wlan0 inet dhcp
                iface wlan0 inet static
                     address 192.168.1.70
                     netmask 255.255.255.0
                     network 192.168.1.0
                     gateway 192.168.1.1

                     wireless_mode managed
                     wireless_essid any
                     wpa-driver wext
                     wpa-conf /etc/wpa_supplicant.conf
                [...]

    # Build et copie sur la SD Card
    $ cd $BUILD_RPI
    $ date +%T; nice bitbake tutorial; date +%T 
    $ cd ./tmp/deploy/images/raspberrypi4/
    # Démonter les partitions 'root' et 'boot' de la SD Card avant de copier
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

    # Test depuis ma machine
    $ ssh root@192.168.1.70
```

> NB:   FONCTIONNE UNIQUEMENT DANS LE WLAN  => pas d'accès à internet

#### 04.2 Configuration des utilitaires système

##### 04.2.1 Gestion de l'heure système

```
  # Vérification sur la cible
    $ ssh root@192.168.1.70
    $ uptime
        # Donne le temps écoulé depuis le démarrage
    $ date
        # heure par défaut (démarre au 9 mars 2018 à 12:34:56, cf. ci-dessous)

    $ reboot
    $ date
        # Ces 2 commandes sont censées redémarrer l'heure à celle par défaut (pas le cas pour ma R.PI)

    $ cat /etc/timestamp
        20180309123456
    
    $ ntpd
        -sh: ntpd: command not found        # 'ntpd' fait partie du package busybox
```

##### 04.2.2 Configuration de BusyBox

Vérification sur la cible

```
    $ ssh root@192.168.1.70
    $ busybox
                BusyBox v1.32.0 () multi-call binary.
                BusyBox is copyrighted by many authors between 1998-2015.
                Licensed under GPLv2. See source distribution for detailed
                copyright notices.

                Usage: busybox [function [arguments]...]
                   or: busybox --list
                   or: function [arguments]...

                     BusyBox is a multi-call binary that combines many common Unix
                     utilities into a single executable.  Most people will create a
                     link to busybox for each function they wish to use and BusyBox
                     will act like whatever it was invoked as.

                Currently defined functions:
                     [, [[, addgroup, adduser, ash, awk, basename, blkid, bunzip2, bzcat, bzip2, cat, chattr, chgrp,
                     chmod, chown, chroot, chvt, clear, cmp, cp, cpio, cut, date, dc, dd, deallocvt, delgroup,
                     deluser, depmod, df, diff, dirname, dmesg, dnsdomainname, du, dumpkmap, dumpleases, echo,
                     egrep, env, expr, false, fbset, fdisk, fgrep, find, flock, free, fsck, fstrim, fuser, getopt,
                     getty, grep, groups, gunzip, gzip, head, hexdump, hostname, hwclock, id, ifconfig, ifdown,
                     ifup, insmod, ip, kill, killall, klogd, less, ln, loadfont, loadkmap, logger, logname, logread,
                     losetup, ls, lsmod, lzcat, md5sum, mesg, microcom, mkdir, mkfifo, mknod, mkswap, mktemp,
                     modprobe, more, mount, mountpoint, mv, nc, netstat, nohup, nproc, nslookup, od, openvt,
                     patch, pgrep, pidof, pivot_root, printf, ps, pwd, rdate, readlink, realpath, reboot, renice,
                     reset, resize, rev, rfkill, rm, rmdir, rmmod, route, run-parts, sed, seq, setconsole, setsid,
                     sh, sha1sum, sha256sum, shuf, sleep, sort, start-stop-daemon, stat, strings, stty, sulogin,
                     swapoff, swapon, switch_root, sync, sysctl, syslogd, tail, tar, tee, telnet, test, tftp, time,
                     top, touch, tr, true, ts, tty, udhcpc, udhcpd, umount, uname, uniq, unlink, unzip, uptime,
                     users, usleep, vi, watch, wc, wget, which, who, whoami, xargs, xzcat, yes, zcat
    
    # 'ntpd' est bien absent de la liste des commandes de 'Busybox'

    # Localisation des commandes de l'image 'core-image-minimal' => montre l'intérêt 'Busybox': y a pô grand monde !
    $ ls -l /bin/

                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 ash -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            14 Dec 26 11:38 busybox -> busybox.nosuid
                -rwxr-xr-x    1 root     root        476920 Dec 26 11:49 busybox.nosuid
                -rwsr-xr-x    1 root     root         42440 Dec 26 11:49 busybox.suid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 cat -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 chattr -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 chgrp -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 chmod -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 chown -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 cp -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 cpio -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 date -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 dd -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 df -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 dmesg -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 dnsdomainname -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 dumpkmap -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 echo -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 egrep -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 false -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 fgrep -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 getopt -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 grep -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 gunzip -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 gzip -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 hostname -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 kill -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 ln -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            17 Dec 26 12:49 login -> /bin/busybox.suid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 ls -> /bin/busybox.nosuid
                lrwxrwxrwx    1 root     root            19 Dec 26 12:49 mkdir -> /bin/busybox.nosuid

  # Localisation des commandes de 'Busybox' (avec d'autres binaires et commandes)
    $ ls -l /bin/
    $ ls -l /usr/bin/
    $ ls -l /usr/sbin/

  # Retour sur le PC
    $ cd $BUILD_RPI
  # Edition de la configuration de 'Busybox'
                Networking Utilities    -->     Sélectionner 'ntpd'

  # Création du fragment de configuration
    $ bitbake -c diffconfig busybox
                        [...]
                        Config fragment has been dumped into:
                        /home/ladeveze/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/meta-tutorial-layer/recipes-core/busybox/busybox/fragment.cfg
    $ vim /home/ladeveze/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/meta-tutorial-layer/recipes-core/busybox/busybox/fragment.cfg

  # Intégration du fragment dans une extension de recette pour 'Busybox'
    $ recipetool appendsrcfile -w ../meta-tutorial-layer/ busybox tmp/work/cortexa7t2hf-neon-vfpv4-poky-linux-gnueabi/busybox/1.32.0-r0/fragment.cfg
NOTE: Starting bitbake server...
Loading cache: 100% |######################################################################| Time: 0:00:00
Loaded 2252 entries from dependency cache.
NOTE: Writing append file  ~/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/meta-tutorial-layer/recipes-core/busybox/busybox_%.bbappend
NOTE: Copying tmp/work/cortexa7t2hf-neon-vfpv4-poky-linux-gnueabi/busybox/1.32.0-r0/fragment.cfg to ~/workspace/morgue2021/embedded_linux/yocto/ChristopheBlaess2020/Yocto-lab/meta-tutorial-layer/recipes-core/busybox/busybox/fragment.cfg

  # 'receipetool' a créé le répertoire 'busybox'
    $ tree ../meta-tutorial-layer/recipes-core/busybox/
                ../meta-tutorial-layer/recipes-core/busybox/
                ├── busybox
                │   └── fragment.cfg
                └── busybox_%.bbappend


  # Effacement de la dernière compilation de 'Busybox' (évite les warnings)
    $ bitbake -c clean busybox

  # Build + copie SD Card
    $ date +%T; nice bitbake tutorial; date +%T
    $ cd tmp/deploy/images/raspberrypi4/
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

  # Test sur cible
    $ ssh root@192.168.1.70

    $ which ntpd
            /usr/sbin/ntpd
    $ ls -l /usr/sbin/ntpd
            lrwxrwxrwx 1 root root 19 Mar  9  2018 /usr/sbin/ntpd -> /bin/busybox.nosuid
    $ date
            Tue Apr 13 15:18:50 UTC 2021

    $ reboot
        [...]

    $ ssh root@192.168.1.70
    $ date
            Tue Apr 13 15:27:19 UTC 2021            # La date évolue normalement. C'était déjà le cas sans 'ntpd'.
```

##### 04.2.3 Démarrage d'un service

```
  # System V Init, default run level
    $ cat /etc/inittab

            # /etc/inittab: init(8) configuration.
            # $Id: inittab,v 1.91 2002/01/25 13:35:21 miquels Exp $

            # The default runlevel.
            id:5:initdefault:
            [...]

    $ ls -l /etc/rc5.d/
            total 0
            lrwxrwxrwx 1 root root 20 Mar  9  2018 S01networking -> ../init.d/networking
            lrwxrwxrwx 1 root root 16 Mar  9  2018 S02dbus-1 -> ../init.d/dbus-1
            lrwxrwxrwx 1 root root 18 Mar  9  2018 S10dropbear -> ../init.d/dropbear
            lrwxrwxrwx 1 root root 17 Mar  9  2018 S12rpcbind -> ../init.d/rpcbind
            lrwxrwxrwx 1 root root 21 Mar  9  2018 S15mountnfs.sh -> ../init.d/mountnfs.sh
            lrwxrwxrwx 1 root root 14 Mar  9  2018 S20apmd -> ../init.d/apmd
            lrwxrwxrwx 1 root root 19 Mar  9  2018 S20bluetooth -> ../init.d/bluetooth
            lrwxrwxrwx 1 root root 14 Mar  9  2018 S20ntpd -> ../init.d/ntpd
            lrwxrwxrwx 1 root root 16 Mar  9  2018 S20syslog -> ../init.d/syslog
            lrwxrwxrwx 1 root root 22 Mar  9  2018 S21avahi-daemon -> ../init.d/avahi-daemon
            lrwxrwxrwx 1 root root 15 Mar  9  2018 S22ofono -> ../init.d/ofono
            lrwxrwxrwx 1 root root 15 Mar  9  2018 S64neard -> ../init.d/neard
            lrwxrwxrwx 1 root root 22 Mar  9  2018 S99rmnologin.sh -> ../init.d/rmnologin.sh
            lrwxrwxrwx 1 root root 23 Mar  9  2018 S99stop-bootlogd -> ../init.d/stop-bootlogd

    $ cd $BUILD_RPI
    $ mkdir -p ../meta-tutorial-layer/recipes-custom/ntpd-start/files/

  # Récupération du script 'ntpd' pour System V Init
    $ mv ~/Téléchargement/ntpd ../meta-tutorial-layer/recipes-custom/ntpd-start/files/
    $ vim ../meta-tutorial-layer/recipes-custom/ntpd-start/files/ntpd

                        #! /bin/sh
                        ### BEGIN INIT INFO
                        # Provides:             ntpd
                        # Required-Start:       $networking
                        # Required-Stop:
                        # Default-Start:        2 3 4 5
                        # Default-Stop:         1
                        # Short-Description:    Network Time Protocol client Daemon
                        ### END INIT INFO
                        #

                        do_start()
                        {
                             start-stop-daemon --start --exec ntpd -- -p pool.ntp.org
                        }


                        do_stop()
                        {
                             start-stop-daemon --stop --name ntpd
                        }


                        case "$1" in
                          start) do_start ;;
                          stop)  do_stop  ;;
                          restart) do_stop; do_start ;;
                          *) echo "Usage: $0 {start|stop|restart}" >&2; exit 1 ;;
                        esac

                        exit 0

  # Récupération de la recette
    $ mv ~/Téléchargement/ntpd-start_1.0.bb ../meta-tutorial-layer/recipes-custom/ntpd-start/
    $ vim ../meta-tutorial-layer/recipes-custom/ntpd-start/ntpd-start_1.0.bb

                SUMMARY = "Script to start ntpd client service"
                SECTION = "custom"

                LICENSE = "MIT"
                LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

                SRC_URI  = "file://ntpd"

                inherit update-rc.d

                INITSCRIPT_NAME = "ntpd"

                do_install() {
                  install -d ${D}${sysconfdir}
                  install -d ${D}${sysconfdir}/init.d
                  install -m 755 ${WORKDIR}/ntpd ${D}${sysconfdir}/init.d/ntpd
                }
  # Build + copie SD Card
    $ date +%T; nice bitbake tutorial; date +%T
    $ cd tmp/deploy/images/raspberrypi4/
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

  # Test sur cible
    $ ssh root@192.168.1.70

    root@vincelade:~# date
        Tue Apr 13 16:06:55 UTC 2021
    root@vincelade:~# ls /var/run
            agetty.reload  ifstate	    lock	 resolvconf    syslogd.pid	 utmp
            avahi-daemon   initctl	    mount	 rpcbind.lock  udev		 wpa_supplicant
            dbus	       klogd.pid    ntpd.pid	 rpcbind.sock  udhcpc.eth0.pid	 wpa_supplicant.wlan0.pid
            dropbear.pid   ld.so.cache  resolv.conf  runlevel      udhcpc.wlan0.pid
    root@vincelade:~# cat /var/run/ntpd.pid 
        430
    root@vincelade:~# ps | grep ntpd
          430 root      2652 S    ntpd -p fr.pool.ntp.org
          685 root      2248 S    grep ntpd
    root@vincelade:~# ls -l /etc/rc5.d/
                total 0
                [...]
                lrwxrwxrwx 1 root root 19 Mar  9  2018 S20bluetooth -> ../init.d/bluetooth
                lrwxrwxrwx 1 root root 14 Mar  9  2018 S20ntpd -> ../init.d/ntpd
                lrwxrwxrwx 1 root root 16 Mar  9  2018 S20syslog -> ../init.d/syslog
                [...]
```

*CA FONCTIONNE !!!*

#### 04.3 Personnaliser le support du matériel

##### 04.3.1 Configuration du noyau

Changements d'options dans le Kernel:
+ Activation de l'option préemptabilité
+ Utilisation de la LED verte avec le pattern 'activity' => clignotement avec une fréquence dépendant de la charge instantanée du système.

```
  # Vérifications initiales: 
   $ ssh root@192.168.1.70
        root@192.168.1.70's password: 
        # Kernel compilé avec les options par défaut
        root@vincelade:~# uname -a
                Linux vincelade 5.4.72-v7l #1 SMP Mon Oct 19 11:12:20 UTC 2020 armv7l armv7l armv7l GNU/Linux
        # Le trigger 'activity' est absent sur la cible.
        root@vincelade:~# cat /sys/class/leds/led0/trigger 
                    none rc-feedback kbd-scrolllock kbd-numlock kbd-capslock kbd-kanalock kbd-shiftlock kbd-altgrlock kbd-ctrllock kbd-altlock kbd-shiftllock kbd-shiftrlock kbd-ctrlllock kbd-ctrlrlock timer
                    oneshot heartbeat backlight gpio cpu cpu0 cpu1 cpu2 cpu3 default-on input panic actpwr mmc1 [mmc0] rfkill-any rfkill-none rfkill0 

  # Besoin d'installer la librairie NCURSES sur 'debian' sinon impossible de compiler 'menuconfig' pour le Kernel:
                                https://www.yoctoproject.org/pipermail/yocto/2014-November/022189.html

    $ apt install libncurses5-dev ncurses-doc libncurses-dev
    # Pb d'affichage: incompatibilité de 'ncurses' et de 'UTF-8'

  # Lancement de 'menuconfig' pour le kernel avec Yocto
    $ cd $BUILD_RPI
    $ bitbake -c menuconfig virtual/kernel
                'General setup'  -->  Preemption Model  -->  sélectionner l'option «Preemptible Kernel (Low-Latency Desktop)»

                'Device Drivers'  -->  LED Support  -->  LED Trigger Support  -->  activer (en pressant «Y») l'option «LED activity Trigger»

  # Préparation des fragments de configuration
    $ bitbake -c diffconfig virtual/kernel

  # Création de l'extension de recette liée à notre fragment
    $ recipetool appendsrcfile -w ../meta-tutorial-layer/ virtual/kernel tmp/work/raspberrypi4-poky-linux-gnueabi/linux-raspberrypi/1_5.4.72+gitAUTOINC+5d52d9eea9_154de7bbd5-r0/fragment.cfg

  # Création du répertoire 'recipes-kernel' dans notre layer
    $ ls ../meta-tutorial-layer/
        conf/        README                 recipes-core/    recipes-example/  recipes-support/
        COPYING.MIT  recipes-connectivity/  recipes-custom/  recipes-kernel/

    $ tree ../meta-tutorial-layer/recipes-kernel/
                    ../meta-tutorial-layer/recipes-kernel/
                    └── linux
                        ├── linux-raspberrypi
                        │   └── fragment.cfg
                        └── linux-raspberrypi_%.bbappend

                    2 directories, 2 files

  # Nettoyage recette virtual/kernel (évite les warnings)
    $ bitbake -c clean virtual/kernel

  # Build + copie SD Card
    $ date +%T; nice bitbake tutorial; date +%T
    $ cd tmp/deploy/images/raspberrypi4/
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

  # Test sur cible
    $ ssh root@192.168.1.70
    root@192.168.1.70's password: 
    root@vincelade:~# cat /sys/class/leds/led0/trigger                          # Verif. presence de 'activity' dans la liste des triggers de la LED verte.
    none rc-feedback kbd-scrolllock kbd-numlock kbd-capslock kbd-kanalock kbd-shiftlock kbd-altgrlock kbd-ctrllock kbd-altlock kbd-shiftllock kbd-shiftrlock kbd-ctrlllock kbd-ctrlrlock timer oneshot heartbeat backlight gpio cpu cpu0 cpu1 cpu2 cpu3 activity default-on input panic actpwr mmc1 [mmc0] rfkill-any rfkill-none rfkill0 
    root@vincelade:~# echo activity > /sys/class/leds/led0/trigger              # Activation de 'activity' comme trigger: à ce stade, la LED verte clignotte à la seconde.
    root@vincelade:~# while  true  ;  do  :  ;  done                            # Cette boucle infinie fait clignotter la LED verte ultra-rapidement.
```

*CA FONCTIONNE !!!*

#####  04.3.2 Modificcation du Device Tree

=> Description du Device Tree, de la mise en place de l'expérience avec le capteur de température MCO9808 et la R.PI4.
Aucune manip. sur 'Yocto'...

> NB: Les sources du kernel sont dans le répertoire '$BUILD_RPI/tmp/work-shared/raspberrypi4-64/kernel-source/'
==> Quelle différence avec les sources clonées dans le paragraphe précédent

##### 04.3.3 Patch sur les sources du noyau

```
  # Tester sur la cible la version de kernel
    root@vincelade:~# uname -r
                                5.4.72-v7l

  # Chouf les fichiers de Device Tree du kernel
    $ cd $YOCTOLAB
    $ cd meta-raspberrypi/recipes-kernel/linux
    $ vim linux-raspberrypi_5.4.bb 

                LINUX_VERSION ?= "5.4.72"
                LINUX_RPI_BRANCH ?= "rpi-5.4.y"                                 # branche

                SRCREV_machine = "154de7bbd5844a824a635d4f9e3f773c15c6ce11"     # numero de commit
                SRCREV_meta = "5d52d9eea95fa09d404053360c2351b2b91b323b"

                require linux-raspberrypi_5.4.inc
                [...]
=> Donne la branche et le numero de commit

    $ vim linux-raspberrypi_5.4.inc
                FILESEXTRAPATHS_prepend := "${THISDIR}/linux-raspberrypi:"

                KMETA = "kernel-meta"

                SRC_URI = " \
                    git://github.com/raspberrypi/linux.git;name=machine;branch=${LINUX_RPI_BRANCH} \
                    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.4;destsuffix=${KMETA} \
                    "
                SRC_URI_remove = "file://rpi-kernel-misc.cfg"
                [...]
=> Donne l'adresse du dépôt git

  # Cloner le kernel et se mettre sur la bonne branche
    $ cd $BUILD_RPI
    $ git clone git://github.com/raspberrypi/linux.git
    $ cd linux/
    $ git checkout 154de7bb             # Les 8 premiers digits du commit id
    $ git branch -a

 # Editer le fichier de device tree de la R.PI4 contenant le bus 'i2c1'
    $ vim arch/arm/boot/dts/bcm2711-rpi-4-b.dts
                    &i2c1 {
                            pinctrl-names = "default";
                            pinctrl-0 = <&i2c1_pins>;
                            clock-frequency = <100000>;
                    +
                    +        status = "okay";
                    +        temp@18 {
                    +                compatible = "jedec,jc-42.4-temp";
                    +                reg = <0x18>;
                    +                #address-cells = <0x1>;
                    +                #size-cells = <0x0>;
                    +        };
                    };
=> Ajouter les lignes précédées d'un '+'

  # Créer un fichier de diff avec 'git'
    $ git diff > $BUILDDIR/001-add-i2c1-device-in-dts.patch

  # Créer le patch dans 'Yocto'
    $ recipetool appendsrcfile $YOCTOLAB/meta-tutorial-layer/ virtual/kernel 001-add-i2c1-device-in-dts.patch

  # Nettoyage recette virtual/kernel (évite les warnings)
    $ bitbake -c clean virtual/kernel

  # Build + copie SD Card
    $ date +%T; nice bitbake tutorial; date +%T
    $ cd tmp/deploy/images/raspberrypi4/
    $ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0

  # Test sur cible
    $ ssh root@192.168.1.70
```

> TODO: 
    - Souder le connecteur à la board du capteur de température et tester sur la PI.
    - Résoudre le warning du fichier $BUILD_RPI/remaining.warning

### CHAPTER 5

=> idée pour prolonger la formation :

+ mettre 'systemd' pour remplacer 'update.rc'
+ voir autres possibilités pour le Wifi:     'systemd' & 'wifi', iw, ...
+ 'tzdata' avec 'ntpd'
+ voir 'cron', 'swupdate', 'chroot', ...
+ installer 'Qt' via yocto, installer des modules python via yocto ('Flask', ...)


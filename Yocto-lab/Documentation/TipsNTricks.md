
# Yocto training Tips & Tricks

## $ runqemu [...]

```
# Après le build '$ bitbake tutorial'
$ runqemu qemuarm core-image-base       # Lance zImage avec rootfs et conf de 'core-image-base'
$ runqemu qemuarm tutorial              # Ca plante
$ runqemu tutorial qemuarm              # Ca plante
$ runqemu qemuarm                       # Lance zImage avec rootfs et conf de 'tutorial' (dernier compilé)
```

## $ runqemu nographic qemuarm [...]

Quit short key (no need to kill Linux process) : _**(ctrl+a) + x**_
> ctrl+a + ctrl+x    ne fonctionne pas!

## Shell Script toolbox

Boucle infinie avec des instructions shell en 1 seule ligne de commande :
```$ while  true  ;  do  :  ;  done```
*arrêt avec ctrl+c* :flushed:

## C language toolbox

Convertir le contenu binaire d'une image en tableau avec ```gdk-pixbuf-csource``` :
```$ gdk-pixbuf-csource --macros my-splash.png > psplash-poky-img.h```

## Crypter n'importe quel mot de passe en clair
Christophe Blaess :
```$ git clone https://github.com/cpb-/password-encryption```

alternative :
> chez STM...

## Linux cmde toolbox

Copie de fichiers de grande taille comme sur Carte SD avec [bmaptool](https://manpages.ubuntu.com/manpages/trusty/man1/bmaptool.1.html) ([block map](https://github.com/intel/bmap-tools)) :
```$ sudo bmaptool copy --bmap <bmap file> <file image to copy> <device>```
> Ex:
> _$ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0_

## [Check layers](https://docs.yoctoproject.org/dev/dev-manual/layers.html#yocto-check-layer-script)

```
$ source oe-init-build-env
$ yocto-check-layer <your_layer_directory>
```


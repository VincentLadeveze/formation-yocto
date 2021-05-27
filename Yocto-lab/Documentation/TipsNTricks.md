#########################
#    $ runqemu [...]    #
#########################

# Après le build '$ bitbake tutorial'
$ runqemu qemuarm core-image-base       # Lance zImage avec rootfs et conf de 'core-image-base'
$ runqemu qemuarm tutorial              # Ca plante
$ runqemu tutorial qemuarm              # Ca plante
$ runqemu qemuarm                       # Lance zImage avec rootfs et conf de 'tutorial' (dernier compilé)


#####################################
#    $ runqemu nographic qemuarm    #
#        [...]                      #
#####################################

# Quit short key (no need to kill Linux process)
(ctrl+a) + x                #  ctrl+a + ctrl+x    ne fonctionne pas!


###################################
#       Shell Script toolbox      #
###################################

# Boucle infinie avec des instructions shell en 1 seule ligne de commande 
$ while  true  ;  do  :  ;  done            # arrêt avec ctrl+c


#################################
#       C language toolbox      #
#################################

# Convertir le contenu binaire d'une image en tableau: 'gdk-pixbuf-csource'
$ gdk-pixbuf-csource --macros my-splash.png > psplash-poky-img.h

# Crypter n'importe quel mot de passe en clair
$ git clone https://github.com/cpb-/password-encryption


#################################
#       Linux cmde toolbox      #
#################################

# Copie de fichiers sur Carte SD avec 'bmaptool' (block map)
$ sudo bmaptool copy --bmap <bmap file> <file image to copy> <device>
Ex:
$ sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0 



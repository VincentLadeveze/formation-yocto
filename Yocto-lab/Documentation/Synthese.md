#########################################
#       Synthèse formation YOCTO        #
#       Ch. Blaess - janvier 2020       #
#########################################


1 - Recettes et applications            <recette>.bb   &   'IMAGE_INSTALL_append'
============================

    1.1 - Poky
    ----------

# Liste des recettes de Poky
$ ls  ../poky/meta/recipes*

# Nombre de recette de Poky
$ find ../poky/meta/recipes* -name '*bb'| wc -l

# Ajout d'une application de Poky ($BUILD_ROOT/conf/local.conf)
 IMAGE_INSTALL_append = " mc"

# Verification de la version présente dans Poky
$ bitbake -s | grep ^mc


    1.2 Ajout de packages hors Poky
    -------------------------------

# Ajout de l'application 'nano':
        # https://layers.openembedded.org   chercher dans 'Receipes' le nom 'nano'
        # Il appartient au Layer 'meta-oe' appartenant à la collection de layers 'meta-openembedded'

$ git clone git://git.openembedded.org/meta-openembedded -b gategarth
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
# Ajout de 'nano' de 'meta-oe' ($BUILD_ROOT/conf/local.conf)
  IMAGE_INSTALL_append = " nano"


2 - Image
=========

    2.1 - Fonctionnalités de l'image          <fonctionnalités>.bbclass   &    'IMAGE_FEATURES'
    --------------------------------

# Les fonctionnalités proposées par la classe «core-image» est implémentée dans le fichier 'poky/meta/classes/core-image.bbclass'
                    x11, x11-base, x11-sato, tools-debug, eclipse-debug, tools-profile, tools-testapps, tools-sdk, [...]

# Fichier de description du build 'poky/meta/recipes-core/images/core-image-base.bb' incluant les fonctionnalités
                    «inherit core-image»            : la recette hérite d’une classe prédéfinie qui décrit le contenu d’une image en proposant des fonctionnalités optionnelles.
                    «IMAGE_FEATURES += "splash"»    : ajoute la fonctionnalité splashscreen

# Build de l'image utilisée initialement: poky ou custom.


    2.2 Création d'une image
    ---------------------------

# Create layer et add layer avec Bitbake
# Creation du fichier de description du build '$YOCTOLAB/meta-my-layer/recipes-custom/images/my-image.bb'
# Ajout des fonctionnalités ('IMAGE_FEATURES') et des applicationsa (IMAGE_INSTALL_append'), peuvent être initialement dans '$BUILD_ROOT/conf/local.conf'
# Build avec le nom 'my-image'


    2.3 - Personnalisation des recettes
    -----------------------------------




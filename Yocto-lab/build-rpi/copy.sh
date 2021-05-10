#!/bin/sh

cd $BUILDDIR
cd tmp/deploy/images/raspberrypi4/
sudo bmaptool copy --bmap tutorial-raspberrypi4.wic.bmap tutorial-raspberrypi4.wic.bz2 /dev/mmcblk0


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

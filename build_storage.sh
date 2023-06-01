#!/bin/bash
#
#   File:   build_storage.sh
#   Author: Matteo Loporchio
#
#   Bash script for constructing the event storage database
#   containing information about all event occurrences of each block.
#
#   NOTICE: the execution of this script may take some time.
#

java -cp "bin:lib/*" skip.ChainStorageBuilder data/events data/storage
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

CLASS="skip.ChainStorageBuilder"
EVENTS_FILE="data/events"
STORAGE_DB="data/storage"

java -cp "bin:lib/*" ${CLASS} ${EVENTS_FILE} ${STORAGE_DB}
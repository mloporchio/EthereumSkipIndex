#!/bin/bash
#
#   File:   build_filters.sh
#   Author: Matteo Loporchio
#
#   This Bash script constructs Bloom filters for each block in the data set.
#   Filters have a size of 8 KiB (i.e., 8192 bytes) each.
#   This script also constructs modified Bloom filters 
#   (i.e., Bloom filters recording also the concatenation between contract address
#   and event signature hash for more accurate searches).
#
#   NOTICE: the execution of this script may take some time.
#

KEYS_FILE="data/keys"
EVENTS_FILE="data/events"
FILTER_SIZE=8192

# Build standard filters.
java -cp "bin:lib/*" skip.BloomFilterBuilder ${KEYS_FILE} data/filters_8K ${FILTER_SIZE}

# Build modified filters.
java -cp "bin:lib/*" skip.BloomFilterBuilderExt ${KEYS_FILE} ${EVENTS_FILE} data/filters_8K_m ${FILTER_SIZE}
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

# Build standard filters.
java -cp "bin:lib/*" skip.BloomFilterBuilder data/filters_8K data/index_8K_7 7

# Build modified filters.
java -cp "bin:lib/*" skip.BloomFilterBuilderExt data/filters_8K_m data/index_8K_7_m 7
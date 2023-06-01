#!/bin/bash
#
#   File:   build_index.sh
#   Author: Matteo Loporchio
#
#   Bash script for constructing the BF skip index for each block.
#   The BF skip indexes of all blocks are stored in a dedicated LevelDB database.
#   This script also constructs BF skip indexes with modified filters 
#   (i.e., Bloom filters recording also the concatenation between contract address
#   and event signature hash for more accurate searches).
#
#   NOTICE: the execution of this script may take some time.
#

# Build index with standard filters.
java -cp "bin:lib/*" skip.ChainIndexBuilder data/filters_8K data/index_8K_7 7

# Build index with modified filters.
java -cp "bin:lib/*" skip.ChainIndexBuilder data/filters_8K_m data/index_8K_7_m 7
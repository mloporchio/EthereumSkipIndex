#!/bin/bash
#
#   File:   test_query.sh
#   Author: Matteo Loporchio
#
#   Bash script for running the query simulation procedure
#   using BF skip indexes and queries from the CryptoKitties Core
#   smart contract on the Ethereum Blockchain.
#
#   NOTICE: the execution of this script may take some time.
#

CLASS="skip.TestFindFirst"
INDEX_PATH="data/index_8K_7"
INDEX_MOD_PATH="data/index_8K_7_m"
STORAGE_PATH="data/storage"
QUERY_BIRTH_PATH="data/queries_birth.csv"
QUERY_TRANSFER_PATH="data/queries_transfer.csv"
ADDRESS="0x06012c8cf97bead5deae237070f9587f8e7a266d"
SIGNATURE_BIRTH="0x0a5311bd2a6608f08a180df2ee7c5946819a649b204b554bb8e39825b2c50ad5"
SIGNATURE_TRANSFER="0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"

# Birth
java -cp "bin:lib/*" ${CLASS} ${INDEX_PATH} ${STORAGE_PATH} ${QUERY_BIRTH_PATH} data/queries_birth_res.csv ${ADDRESS} ${SIGNATURE_BIRTH} default 

# Transfer
java -cp "bin:lib/*" ${CLASS} ${INDEX_PATH} ${STORAGE_PATH} ${QUERY_TRANSFER_PATH} data/queries_transfer_res.csv ${ADDRESS} ${SIGNATURE_TRANSFER} default

# Birth (MOD)
java -cp "bin:lib/*" ${CLASS} ${INDEX_MOD_PATH} ${STORAGE_PATH} ${QUERY_BIRTH_PATH} data/queries_birth_res_m.csv ${ADDRESS} ${SIGNATURE_BIRTH} extended 

# Transfer (MOD)
java -cp "bin:lib/*" ${CLASS} ${INDEX_MOD_PATH} ${STORAGE_PATH} ${QUERY_TRANSFER_PATH} data/queries_transfer_res_m.csv ${ADDRESS} ${SIGNATURE_TRANSFER} extended

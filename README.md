# EthSkip

## Description

This GitHub repository contains the code used to generate the results presented in [1].

The paper presents the *BF skip index*, a data structure based on Bloom filters [2] that can be used for answering inter-block queries on blockchains efficiently. 

## Overview

The BF skip index was implemented taking the Ethereum blockchain as a reference. Indeed, each Ethereum block already contains a Bloom filter within its header, in order to provide a compact representation of the events raised by the transactions within the block. The experiments proposed in the article (and implemented in this repository) serve a dual purpose.

1. First, we conducted an historical analysis of events within the first 15 million blocks of the Ethereum blockchain. The goal was to evaluate the feasibility of using default <code>logsBloom</code> filters for building a BF skip index.
2. Then, we tested our implementation of BF skip indexes and the corresponding algorithm for finding the first occurrence of an event using event data from the <a href="https://www.cryptokitties.co/">CryptoKitties</a> Core smart contract (which can be viewed <a href="https://etherscan.io/address/0x06012c8cf97bead5deae237070f9587f8e7a266d">here</a>).

## Data

Due to space constraints, all data needed to reproduce the results is stored in <a href="https://doi.org/10.5281/zenodo.7957140">this Zenodo repository</a>. Before running the experiments, all downloaded files should be placed in the <code>data</code> folder of this GitHub repository. 

The linked Zenodo repository also includes a detailed description of the data set and its structure.

## Documentation

The entire project was implemented using Java and Python. Specifically, all code for constructing Bloom filters, BF skip indexes, and for the simulation of search algorithms was written in Java. Python was used for query generation and result analysis. In particular, Jupyter Notebooks were used to produce and visualize the results interactively.

The Javadoc for all Java sources is available <a href="https://pages.di.unipi.it/loporchio/doc/EthSkip/">here</a>. The same documentation can also be generated as discussed in the [Javadoc](###Javadoc) section.

## Requirements

The code was tested using the following software and libraries, which are required for reproducing all experiments.

- Oracle Java JDK (v. 19.0.1)
- Python (v. 3.8.9) with the following libraries:
    - Jupyter Notebook (v. 6.5.2)
    - Pandas (v. 1.5.2)
    - NumPy (v. 1.23.5)
    - Matplotlib (v. 3.6.2)
    - SciPy (v. 1.9.3)

The Java implementation depends on the following libraries (already included in the <code>lib</code>).

- Google Guava (v. 31.1)
- LevelDB JNI (v. 1.8)


### Hardware

The experiments have been executed on the following hardware.

- The Java code has been tested on a machine running Ubuntu Linux, with an 8-core Intel Xeon 5218 CPU @ 2.3 GHz and 256 GB of RAM.

- The data analysis with Jupyter Notebooks has been carried out on an Apple MacBook Air with a dual-core Intel i7 CPU @ 1.7 GHz and 8 GB of RAM.

**Important:** to run the Java simulations we recommend at least 150 GB of free disk space.

## How to compile

To build all Java classes with ease, you can use the supplied makefile. Just open a terminal in the current folder and type <code>make</code>. 

If the <code>make</code> utility is not available in your system, typing the following command from the root directory of the repository should be sufficient.

<code>javac -cp ".:./lib/\*" src/skip/\*.java -d bin/</code>

### Javadoc

To generate Javadoc for the source code, you can use the supplied makefile. Type <code>make doc</code> and the documentation will be placed in the <code>doc</code> folder of the repository. If <code>make</code> is not available, type the following command:

<code>javadoc -cp ".:./lib/\*" src/skip/\*.java -d doc/</code>

## How to run

This section contains the instructions needed to reproduce the experiments presented in the paper.
The results of our experiments are reported in Sections 8.1, 8.2 and 8.3 of the paper. The suggested order for running the experiments aligns with the order in which they are presented in the paper.

1. **Important**. First, make sure you have all the downloaded files from the Zenodo repository in the <code>data</code> folder. Also, make sure that you have extracted the required compressed files as described <a href="https://doi.org/10.5281/zenodo.7957140">here</a>.

2. **Filter analysis**. To obtain all results of Section 8.1, open the  <code>filters.ipynb</code> notebook and execute all cells. This should create 4 output plots (i.e., Figure 5 in the paper) that will be placed in the <code>pictures</code> folder. The plots are as follows.

    | File | Description |
    |---|---|
    | <code>pictures/dist_ones.pdf</code> | Plot of Figure 5(a) |
    | <code>pictures/temporal_ones.pdf</code> | Plot of Figure 5(b) |
    | <code>pictures/dist_keys.pdf</code> | Plot of Figure 5(c) |
    | <code>pictures/temporal_keys.pdf</code> | Plot of Figure 5(d) |

3. **Index analysis**. To obtain all results of Section 8.2, open the <code>skip.ipynb</code> notebook and execute all cells. This should create 5 output plots and 2 CSV files containing queries for the simulation. Plots will be placed in the <code>pictures</code>, while CSV files will be saved in the <code>data</code> directory.

    | File | Description |
    |---|---|
    | <code>pictures/bf-sparse.pdf</code> | Plot of Figure 6(a) |
    | <code>pictures/bf-normal.pdf</code> | Plot of Figure 6(b) |
    | <code>pictures/bf-saturated.pdf</code> | Plot of Figure 6(c) |
    | <code>pictures/cryptokitties_frequency_cumul.pdf</code> | Plot of Figure 7(a) |
    | <code>pictures/cryptokitties_frequency_perc.pdf</code> | Plot of Figure 7(b) |
    | <code>data/queries_birth.csv</code> | Data set of queries for the Birth event |
    | <code>data/queries_transfer.csv</code> | Data set of queries for the Transfer event |

4. **Query analysis**. Once the query data sets have been generated, you can execute the following commands starting from the main directory of the repository. Notice that the execution of the Bash scripts may take some time, as they perform intensive computations (e.g., index construction and multiple query simulations) on a data set of 1 million Ethereum blocks.

    1. Run <code>build_filters.sh</code> to build the Bloom filters for the entire data set.
    3. Run <code>build_index.sh</code> to construct the BF skip index.
    3. Run <code>build_storage.sh</code> to construct the event storage database.
    4. Run <code>test_query.sh</code> to launch the query simulation procedure.
    5. Open the <code>query.ipynb</code> notebook and execute all cells. This will analyze the results of the previous steps.

    <br>

    These steps should create the following output files and directories. Note that the BF skip indexes of all blocks are stored in LevelDB key-value databases. The four plots created in the <code>pictures</code> constitute the content of Figure 8.

    | File | Description |
    |---|---|
    | <code>data/filters_8K</code> | Binary file containing all Bloom filters |
    | <code>data/filters_8K_m</code> | Binary file containing all modified Bloom filters |
    | <code>data/index_8K_7</code> | Directory of the BF skip index LevelDB database |
    | <code>data/index_8K_7_m</code> | Directory of the BF skip index LevelDB database (with modified filters) |
    | <code>data/storage</code> | Directory of the event storage LevelDB database |
    | <code>data/queries_birth_res.csv</code> | Results of queries for the Birth event |
    | <code>data/queries_birth_res_m.csv</code> | Results of queries for the Birth event (with modified filters) |
    | <code>data/queries_transfer_res.csv</code> | Results of queries for the Transfer event |
    | <code>data/queries_transfer_res_m.csv</code> | Results of queries for the Transfer event (with modified filters) |
    | <code>pictures/query_birth.pdf</code> | Plot of Figure 8(a) |
    | <code>pictures/query_transfer.pdf</code> | Plot of Figure 8(b) |
    | <code>pictures/query_birth_m.pdf</code> | Plot of Figure 8(c) |
    | <code>pictures/query_transfer_m.pdf</code> | Plot of Figure 8(d) |

5. **Inter-block time**. The <code>time.ipynb</code> notebook contains the calculations for deriving the average inter-block time discussed in Section 8.2.

## References

1. Loporchio, Matteo, et al. "Skip index: supporting efficient inter-block queries and query authentication on the blockchain." (2023).
2. Bloom, Burton H. "Space/time trade-offs in hash coding with allowable errors." Communications of the ACM 13.7 (1970): 422-426.
3. Wood, Gavin. "Ethereum: A secure decentralised generalised transaction ledger." Ethereum project yellow paper 151.2014 (2014): 1-32.

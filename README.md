# EthSkip

## Description

This GitHub repository contains the code used to generate the results presented in [1].

The paper presents the *BF skip index*, a data structure based on Bloom filters [2] that can be used for answering inter-block queries on blockchains efficiently. 

## Overview

The BF skip index was implemented taking the Ethereum blockchain as a reference. Indeed, each Ethereum block already contains a Bloom filter within its header, in order to provide a compact representation of the events raised by the transactions within the block. The experiments proposed in the article (and implemented in this repository) serve a dual purpose.

1. First, we conducted an historical analysis of events within the first 15 million blocks of the Ethereum blockchain. The goal was to evaluate the feasibility of using default <code>logsBloom</code> filters for building a BF skip index.
2. Then, we tested our implementation of BF skip indexes and the corresponding algorithm for finding the first occurrence of an event using event data from the <a href="https://www.cryptokitties.co/">CryptoKitties</a> Core smart contract (which can be viewed <a href="https://etherscan.io/address/0x06012c8cf97bead5deae237070f9587f8e7a266d">here</a>).

## Data

Due to space constraints, all data needed to reproduce the results is stored in <a href="#">this</a> Zenodo repository. Before running the experiments, all downloaded files should be placed in the <code>data</code> folder of this GitHub repository. 

The linked Zenodo repository also includes a detailed description of the data set and its structure.

## Requirements

The entire project was implemented using Java and Python. Specifically, all code for constructing Bloom filters, BF skip indexes, and for the simulation of  search algorithms was written in Java. Python was used for query generation and result analysis. In particular, Jupyter Notebooks were used to produce and visualize the results interactively.

The code was tested using the following software and libraries, which are required for reproducing all experiments.

- Oracle Java JDK (v. 19.0.1)
- Python (v. 3.8.9) with the following libraries:
    - Jupyter Notebook (v. 6.5.2)
    - Pandas (v. 1.5.2)
    - NumPy (v. 1.23.5)
    - Matplotlib (v. 3.6.2)
    - SciPy (v. 1.9.3)

## How to compile

To build all Java classes with ease, you can use the supplied makefile. Just open a terminal in the current folder and type <code>make</code>.
<!-- If the <code>make</code> utility is not available in your system, it is sufficient to type the following command:

<code>javac -cp ".:lib/*" *.java</code> -->

## How to run

This section contains the instructions needed to reproduce the experiments presented in the paper.

TODO

## References

1. Anonymous authors. "Paper title."
2. Bloom, Burton H. "Space/time trade-offs in hash coding with allowable errors." Communications of the ACM 13.7 (1970): 422-426.
3. Wood, Gavin. "Ethereum: A secure decentralised generalised transaction ledger." Ethereum project yellow paper 151.2014 (2014): 1-32.

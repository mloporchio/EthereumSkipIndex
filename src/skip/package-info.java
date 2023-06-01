/**
 * <p>This package contains the implementation of the <em>BF skip index</em> [1], 
 * an indexing data structure based on Bloom filters [2] that can be used for answering 
 * inter-block queries on blockchains efficiently.<br>
 * The data structure supports membership queries, which consist in verifying whether
 * a given element (e.g., a transaction or a value of one of its attributes) 
 * belongs to a certain block.</p>
 * 
 * <p>The package also includes classes for testing the implementation on real data
 * from the Ethereum blockchain [3]. In particular, the reference use case proposed in [1]
 * involves the events triggered by <a href="https://www.cryptokitties.co/">CryptoKitties</a>, 
 * a popular decentralized application based on Ethereum.</p>
 * 
 * <strong>References</strong>
 * <ol>
 *      <li>Loporchio, Matteo, et al. "Skip index: supporting efficient inter-block queries and query authentication on the blockchain". (2023).</li>
 *      <li>Bloom, Burton H. "Space/time trade-offs in hash coding with allowable errors." Communications of the ACM 13.7 (1970): 422-426.</li>
 *      <li>Wood, Gavin. "Ethereum: A secure decentralised generalised transaction ledger." Ethereum project yellow paper 151.2014 (2014): 1-32.</li>
 * </ol>
 * 
 * @author Matteo Loporchio
 * @version 1.0
 */
package skip;
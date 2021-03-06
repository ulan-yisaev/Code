package blockchain.block;

import blockchain.util.StringUtil;

import java.io.Serializable;
import java.util.Random;

public class Block implements Serializable {
    static final long serialVersionUID = 1L;
    private int id;
    private long timestamp;
    private int nonce;      //an arbitrary number used in cryptography
    private String data;
    private String hash;
    private String prevHash;
    private long timeToCreateBlock;
    private int minerId;
    private int zerosCnt;
    private String zerosCntMessage;

    private int attempts;

    public Block(int id, String prevHash, String data, int minerId, int zerosCnt) {
        this.id = id;
        this.prevHash = prevHash;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.zerosCnt = zerosCnt;
        this.hash = /*(zerosCnt == 0) ? calculateHash() :*/ mineBlock();
        this.minerId = minerId;
    }
    // We represent a block by a hash value. Generating the hash value of a block is called “mining” the block.
// Mining a block is typically computationally expensive to do as it serves as the “proof of work”.

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private String mineBlock() {

        String nZeros = new String(new char[zerosCnt]).replace('\0', '0');

        do {
            nonce = new Random().nextInt(Integer.MAX_VALUE);
            hash = calculateHash();
            attempts++;
//            if (attempts % 1000000 == 0) System.out.printf("attempts# in the mineBlock(%s): %d | zerosCnt: %d | nZeros: %s | | hash: %s %n", Thread.currentThread().getName(), attempts, zerosCnt, nZeros, hash);
            if (Thread.currentThread().isInterrupted()) break;  //https://dzone.com/articles/interrupting-executor-tasks
        } while (!hash.substring(0, zerosCnt).equals(nZeros));

        timeToCreateBlock = (System.currentTimeMillis() - timestamp) / 100;

        if (timeToCreateBlock < 1 /*&& zerosCnt < 4*/) {
            zerosCntMessage = "N was increased to " + ++zerosCnt;
        } else if (timeToCreateBlock > 1) {
            zerosCntMessage = "N was decreased to " + --zerosCnt;
        } else {
            zerosCntMessage = "N stays the same";
        }
        return hash;
    }

    public String calculateHash() {

        return StringUtil.applySha256(id
                + timestamp
                + prevHash
                + nonce);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public long getTimeToCreateBlock() {
        return timeToCreateBlock;
    }

    public int getMinerId() {
        return minerId;
    }

    public void setMinerId(int minerId) {
        this.minerId = minerId;
    }

    public int getZerosCnt() {
        return zerosCnt;
    }

    public void setZerosCnt(int zerosCnt) {
        this.zerosCnt = zerosCnt;
    }

    public String getZerosCntMessage() {
        return zerosCntMessage;
    }

    public void setZerosCntMessage(String zerosCntMessage) {
        this.zerosCntMessage = zerosCntMessage;
    }

    @Override
    public String toString() {
        return String.format("Block:%n"
                        + "Created by miner # %d%n"
                        + "Id: %d%n"
                        + "Timestamp: %d%n"
                        + "Magic number: %d%n"
                        + "Hash of the previous block:%n"
                        + "%s%n"
                        + "Hash of the block:%n"
                        + "%s%n"
                        + "Block was generating for %d seconds%n"
                        + zerosCntMessage
                ,
                this.minerId,
                this.id,
                this.timestamp,
                this.nonce,
                this.prevHash,
                this.hash,
                this.timeToCreateBlock
        );
    }
}

/*
Nonce stands for a Number Used Once in a cryptographic communication such that the block's hash meets a certain criterion. This criterion could be generated a hash that must have its leading four digits to be zero. Thus, the generated hash would look like 00001acbm010gfh1010xxx.

A nonce is basically a random number which figures out how you can actually make this specific block provide you with a valid hash. The way you can do this is by changing the nonce manually. Generally, the miner starts with a Nonce value of 1 and keeps on incrementing it until the generated hash meets the specified criterion. Thus, it may take several iterations until the desired hash with four leading zeros is generated. The expected time for generating a block in the bitcoin system is 10 minutes. Once the miner successfully mines the block, he releases that block in the system and making it the last block in the chain.
 */
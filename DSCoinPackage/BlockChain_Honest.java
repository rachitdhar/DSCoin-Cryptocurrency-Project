package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

    public int tr_count;
    public static final String start_string = "DSCoin";
    public TransactionBlock lastBlock;

    public void InsertBlock_Honest (TransactionBlock newBlock) {
        CRF newCRF = new CRF(64);
        Long nonce = (Long)(long)1000000001;
        int MIN_INITIAL_ZEROES = 4;

        while (true) {
            String testDgst = newCRF.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + nonce.toString());
            boolean check = true;
            
            for (int i = 0; i < MIN_INITIAL_ZEROES; i++)
                if (testDgst.charAt(i) != '0') check = false;

            if (check) {
                newBlock.dgst = testDgst;
                newBlock.nonce = nonce.toString();
                break;
            }
            nonce++;
        }
        
        newBlock.previous = lastBlock;
        lastBlock = newBlock;
    }
}

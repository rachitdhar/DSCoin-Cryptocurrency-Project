package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    return false;
  }

  public TransactionBlock FindLongestValidChain () {
    return null;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {

  }
}

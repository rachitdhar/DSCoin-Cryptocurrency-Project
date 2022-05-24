package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {

  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    return null;
  }

  public void MineCoin(DSCoin_Honest DSObj) {

  }  

  public void MineCoin(DSCoin_Malicious DSObj) {

  }  
}

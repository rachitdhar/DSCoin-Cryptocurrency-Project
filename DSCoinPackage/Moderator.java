package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) throws EmptyQueueException {
    Members Mod=new Members();
    Mod.UID="Moderator";
    int i=0;
    int j=0;
    int coin_id=100000;
    while(coinCount!=0)
    {
    String CID = Integer.toString(coin_id);
    Transaction T=new Transaction();
    T.coinID=CID;
    T.Source=Mod;
    T.Destination=DSObj.memberlist[j];
    T.coinsrc_block=null;
    T.transaction_next_in_queue=null;
    DSObj.pendingTransactions.AddTransactions(T);
    if(i==DSObj.bChain.tr_count-1)
    {
      i=0;
      Transaction[]t=new Transaction[DSObj.bChain.tr_count];

      for(int k=0;k<DSObj.bChain.tr_count;k++)
      {
        try {
          t[k]=DSObj.pendingTransactions.RemoveTransaction();
          
        } catch (EmptyQueueException e) {
          throw new EmptyQueueException();
        }
        
      }
      TransactionBlock Tb=new TransactionBlock(t);
      for(int k=0;k<t.length;k++)
      {
        Pair<String,TransactionBlock> P=new Pair<String,TransactionBlock>(t[k].coinID,Tb);
        t[k].Destination.mycoins.add(P);
      }
      DSObj.bChain.InsertBlock_Honest(Tb);
    }
      i++;
      if(j==DSObj.memberlist.length-1) j=0;
      else j++;
      coin_id++;
      coinCount--;
    }
    coin_id--;
    String CID = Integer.toString(coin_id);
    DSObj.latestCoinID=CID;
}   
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) throws EmptyQueueException {

    Members Mod=new Members();
    Mod.UID="Moderator";
    int i=0;
    int j=0;
    int coin_id=100000;
    while(coinCount!=0)
    {
    String CID = Integer.toString(coin_id);
    Transaction T=new Transaction();
    T.coinID=CID;
    T.Source=Mod;
    T.Destination=DSObj.memberlist[j];
    T.coinsrc_block=null;
    T.transaction_next_in_queue=null;
    DSObj.pendingTransactions.AddTransactions(T);
    if(i==DSObj.bChain.tr_count-1)
    {
      i=0;
      Transaction[]t=new Transaction[DSObj.bChain.tr_count];

      for(int k=0;k<DSObj.bChain.tr_count;k++)
      {
        try {
          t[k]=DSObj.pendingTransactions.RemoveTransaction();
          
        } catch (EmptyQueueException E) {
          throw new EmptyQueueException() ;
        }
        
      }
      TransactionBlock Tb=new TransactionBlock(t);
      for(int k=0;k<t.length;k++)
      {
        Pair<String,TransactionBlock> P=new Pair<String,TransactionBlock>(t[k].coinID,Tb);
        t[k].Destination.mycoins.add(P);
      }
      DSObj.bChain.InsertBlock_Malicious(Tb);
    }
      i++;
      if(j==DSObj.memberlist.length-1) j=0;
      else j++;
      coin_id++;
      coinCount--;
    }
    coin_id--;
    String CID = Integer.toString(coin_id);
    DSObj.latestCoinID=CID;


  }
}

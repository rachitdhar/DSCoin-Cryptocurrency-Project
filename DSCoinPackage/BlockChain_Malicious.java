package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;
  

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    if(!(tB.dgst.substring(0,3)=="0000"))
    {
      return false;
    }
    CRF To_check=new CRF(64);
    if(tB.previous!=null)
    {
      if(!tB.dgst.equals( To_check.Fn(tB.previous.dgst+"#"+tB.trsummary+"#"+tB.nonce)))
      {
        return false;
      }
    }
    else
    {
      if(!tB.dgst.equals( To_check.Fn(start_string+"#"+tB.trsummary+"#"+tB.nonce)))
      {
        return false;
      }
    }
    MerkleTree Tree=new MerkleTree();
    if(!tB.trsummary.equals((Tree.Build(tB.trarray))))
    {
      return false;
    }
    for(int i=0;i<tB.trarray.length;i++)
    {
      if(!tB.checkTransaction(tB.trarray[i]))
      {
        return false;
      }
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int MaximumLength=0;
    TransactionBlock MaxLastTB=null;
    for(int i=0;i<lastBlocksList.length;i++)
    {
      TransactionBlock lastValidBlock=lastBlocksList[i];
      int count=0;
      TransactionBlock iterator=lastBlocksList[i];
      while(iterator!=null)
      {
        if(!checkTransactionBlock(iterator))
        {
          lastValidBlock=iterator.previous;
          count=0;
          iterator=iterator.previous;
          continue;
        }
        count++;
        iterator=iterator.previous;
      }
      if(count>MaximumLength)
      {
        MaximumLength=count;
        MaxLastTB=lastValidBlock;
      }
    }
    return MaxLastTB;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    TransactionBlock lTB=FindLongestValidChain();
    int i=1000000001;
    CRF To_check=new CRF(64);
    while(true)
    {
      String s=Integer.toString(i);
      String crf= To_check.Fn(lTB.dgst+"#"+newBlock.trsummary+"#"+s);
      if(crf.substring(0,4).equals("0000"))
      {
        newBlock.nonce=s;
        newBlock.dgst=crf;
        break;
      }
      i++;
    }
    newBlock.previous=lTB;
    int j;
    for(j=0;lastBlocksList[i]!=null;i++)
    {
      if(lastBlocksList[i]==lTB)
      {
        lastBlocksList[i]=newBlock;
        break;
      }
    }
    lastBlocksList[j]=lTB;
  }
}

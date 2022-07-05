package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;



  public void AddTransactions (Transaction transaction) {
        if(numTransactions==0)
        {
          firstTransaction=transaction;
          lastTransaction=transaction;
          numTransactions++;
        }
        else if(numTransactions==1)
        {
          firstTransaction.transaction_next_in_queue=transaction;
          lastTransaction=transaction;
          numTransactions++;
        }
        else{
          lastTransaction.transaction_next_in_queue=transaction;
          lastTransaction=transaction;
          numTransactions++;
        }
        return;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(numTransactions==0)
    {
      throw new EmptyQueueException("Error in RemoveTransaction");
    }
    else if(numTransactions==1)
    {
      Transaction temp=firstTransaction;
      firstTransaction=null;
      lastTransaction=null;
      numTransactions--;
      return temp;
    }
    else{
      Transaction temp=firstTransaction;
      firstTransaction=firstTransaction.transaction_next_in_queue;
      numTransactions--;
      return temp;
    }
  }

  public int size() {
    return numTransactions;
  }
}

package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {

  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    return null;
  }

  public int size() {
    return 0;
  }
}

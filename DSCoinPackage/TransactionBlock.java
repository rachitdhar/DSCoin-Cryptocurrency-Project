package DSCoinPackage;

import HelperClasses.MerkleTree;
// import HelperClasses.CRF;

public class TransactionBlock {
	
	public Transaction[] trarray;
	public TransactionBlock previous;
	public MerkleTree Tree;
	public String trsummary;
	public String nonce;
	public String dgst;

	public TransactionBlock(Transaction[] t) {
        int tr_count = t.length;
        trarray = new Transaction[tr_count];

        for (int i = 0; i < tr_count; i++) {
            trarray[i] = t[i];
        }

		previous = null;
        trsummary = Tree.Build(trarray);
        dgst = null;
	}

	public boolean checkTransaction(Transaction t) {
        boolean check1 = false, check2 = true;
        TransactionBlock t_coinsrc_block = t.coinsrc_block;
        Transaction t_arr[] = t_coinsrc_block.trarray;
        
        for (int i = 0; i < t_arr.length; i++) {
            if ((t_arr[i].coinID == t.coinID) && (t_arr[i].Destination.UID == t.Source.UID)) {
                check1 = true;
            }
        }

        // checking for double spending
        TransactionBlock tb = this;
        boolean foundCoin = false;

        while (true) {
            Transaction tb_arr[] = tb.trarray;
            for (int i = 0; i < tb_arr.length; i++) {
                if (tb_arr[i].coinID == t.coinID) {
                    if (tb_arr[i].Source.UID == t.Source.UID) check2 = false;
                    foundCoin = true;
                    break;
                }
            }

            if (tb.previous == null) break;
            else tb = tb.previous;

            if (foundCoin == true) break;
        }

		return (check1 && check2);
	}
}

package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.TreeNode;

public class Members {

    public String UID;
    public List<Pair<String, TransactionBlock>> mycoins;
    public Transaction[] in_process_trans;

    public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
        Pair<String, TransactionBlock> coin = mycoins.remove(0);
        Transaction tobj = new Transaction();
        tobj.coinID = coin.get_first();
        tobj.Source = this;

        // search through memberslist and find destUID member
        Members membersList[] = DSobj.memberlist;
        for (int i = 0; i < membersList.length; i++) {
            if (membersList[i].UID == destUID) {
                tobj.Destination = membersList[i];
                break;
            }
        }

        // for time and space efficient adding of elements, we use List
        // List<Transaction> temp_arr = new ArrayList<Transaction>(Arrays.asList(in_process_trans));
        // temp_arr.add(tobj);
        // Transaction new_in_process_trans[] = (Transaction[]) (new Object()); 
        // new_in_process_trans = (Transaction[]) temp_arr.toArray();
        // in_process_trans = new_in_process_trans;

        for (int i = 0; i < in_process_trans.length; i++) {
            if (in_process_trans[i] == null) {
                in_process_trans[i] = tobj;
                break;
            }
        }

        DSobj.pendingTransactions.AddTransactions(tobj);
    }

    private void getTransactionNode(TreeNode n, String s, TreeNode tn) {
        if (n.val == s) tn = n;
        else if (n.left != null) {
            getTransactionNode(n.left, s, tn);
            getTransactionNode(n.right, s, tn);
        }
    }

    public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
        TransactionBlock tB = DSObj.bChain.lastBlock;
        boolean found = false;

        while (true) {
            Transaction t_arr[] = tB.trarray;
            for (int i = 0; i < t_arr.length; i++) {
                if (t_arr[i] == tobj) {
                    found = true;
                    break;
                }
            }

            if (found || (tB.previous == null)) break;
            else tB = tB.previous;
        }
        if (!found) throw new MissingTransactionException();

        // computing sibling-coupled-path-to-root to prove membership of tobj in merkle tree of tB
        List<Pair<String,String>> siblingCoupledPathToRoot = new ArrayList<Pair<String,String>>();
        
        String transactionHash = tB.Tree.get_str(tobj);
        TreeNode root = tB.Tree.rootnode;
        TreeNode tobj_node = null;
        
        getTransactionNode(root, transactionHash, tobj_node); // traverse through merkle tree and find node with hash same as tobj hash

        TreeNode currentNode = tobj_node;
        while (true) {
            if (currentNode.parent == null) {
                Pair<String,String> p = new Pair<String,String>(currentNode.val, null);
                siblingCoupledPathToRoot.add(p);
                break;
            } else {
                TreeNode sibling = null;
                if (currentNode.parent.left == currentNode) {
                    sibling = currentNode.parent.right;
                    Pair<String,String> p = new Pair<String,String>(currentNode.val, sibling.val);
                    siblingCoupledPathToRoot.add(p);
                }
                else {
                    sibling = currentNode.parent.left;
                    Pair<String,String> p = new Pair<String,String>(sibling.val, currentNode.val);
                    siblingCoupledPathToRoot.add(p);
                }
                currentNode = currentNode.parent;
            }
        }

        // creating another list of pairs of strings
        List<Pair<String, String>> listOfPairs = new ArrayList<Pair<String, String>>();
        Pair<String, String> firstPair = new Pair<String,String>(tB.previous.dgst, null);
        listOfPairs.add(firstPair);
        TransactionBlock t_i = tB;

        while (true) {
            Pair<String, String> newPair = new Pair<String,String>(
                t_i.dgst, t_i.previous.dgst + "#" + t_i.trsummary + "#" + t_i.nonce
            );
            listOfPairs.add(newPair);

            if (t_i.previous == null) break;
            else t_i = t_i.previous;
        }

        // deleting tobj from in_process_trans
        for (int i = 0; i < in_process_trans.length; i++) {
            if (in_process_trans[i] == tobj) {
                in_process_trans[i] = null;
                break;
            }
        }
        tobj.coinsrc_block = tB;

        // adding sent coin to destination's mycoins list
        Pair<String, TransactionBlock> newCoin = new Pair<String,TransactionBlock>(tobj.coinID, tobj.coinsrc_block);
        tobj.Destination.mycoins.add(newCoin);

        // returning both lists
        Pair<List<Pair<String,String>>, List<Pair<String,String>>> res =
        new Pair<List<Pair<String,String>>,List<Pair<String,String>>>(siblingCoupledPathToRoot, listOfPairs);
        
        return res;
    }

    public void MineCoin(DSCoin_Honest DSObj) {
        int numToRemove = DSObj.bChain.tr_count - 1;
        Transaction t_arr[] = new Transaction[numToRemove + 1];
        int removed = 0;

        while (removed < numToRemove) {
            Transaction temp;
            boolean valid = true;

            try {
                temp = DSObj.pendingTransactions.RemoveTransaction();
            } catch (Exception e) {
                break;
            }

            // checking validity
            if (!DSObj.bChain.lastBlock.checkTransaction(temp)) {
                valid = false;
                continue;
            }

            for (int i = 0; i < removed; i++) {
                if (t_arr[i].coinID == temp.coinID) {
                    valid = false;
                    break;
                }
            }

            if (!valid) continue;

            t_arr[removed] = temp;
            removed++;
        }

        Transaction minerRewardTransaction = new Transaction();
        String newCoinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
        DSObj.latestCoinID = newCoinID; // incrementing latestCoinID

        minerRewardTransaction.coinID = newCoinID;
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this;
        minerRewardTransaction.coinsrc_block = null;

        t_arr[removed] = minerRewardTransaction;
        TransactionBlock tB = new TransactionBlock(t_arr);
        TransactionBlock lastB = DSObj.bChain.lastBlock;
        tB.previous = lastB;
        DSObj.bChain.lastBlock = tB;

        Pair<String, TransactionBlock> newCoin = new Pair<String,TransactionBlock>(newCoinID, tB);
        mycoins.add(newCoin);
    }

    public void MineCoin(DSCoin_Malicious DSObj) {
        int numToRemove = DSObj.bChain.tr_count - 1;
        Transaction t_arr[] = new Transaction[numToRemove + 1];
        int removed = 0;

        while (removed < numToRemove) {
            Transaction temp;
            boolean valid = true;

            try {
                temp = DSObj.pendingTransactions.RemoveTransaction();
            } catch (Exception e) {
                break;
            }

            // checking validity
            if (!DSObj.bChain.FindLongestValidChain().checkTransaction(temp)) {
                valid = false;
                continue;
            }

            for (int i = 0; i < removed; i++) {
                if (t_arr[i].coinID == temp.coinID) {
                    valid = false;
                    break;
                }
            }

            if (!valid) continue;

            t_arr[removed] = temp;
            removed++;
        }

        Transaction minerRewardTransaction = new Transaction();
        String newCoinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
        DSObj.latestCoinID = newCoinID; // incrementing latestCoinID

        minerRewardTransaction.coinID = newCoinID;
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this;
        minerRewardTransaction.coinsrc_block = null;

        t_arr[removed] = minerRewardTransaction;
        TransactionBlock tB = new TransactionBlock(t_arr);

        // inserting block at appropriate position
        TransactionBlock longestValidChain = DSObj.bChain.FindLongestValidChain();
        tB.previous = longestValidChain;

        TransactionBlock lastBlocks_list[] = DSObj.bChain.lastBlocksList;
        for (int i = 0; i < lastBlocks_list.length; i++) {
            if (lastBlocks_list[i] == null) {
                lastBlocks_list[i] = tB;
                break;
            }
        }

        Pair<String, TransactionBlock> newCoin = new Pair<String,TransactionBlock>(newCoinID, tB);
        mycoins.add(newCoin);
    }
}
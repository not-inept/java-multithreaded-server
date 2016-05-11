package hw09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private String transaction;

    private	HashMap<String, Integer> accountsCache;
    private HashMap<String, Integer> acc_writ_val_cache;
    private HashMap<String, Integer> acc_read_val_cache;
    

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        transaction = trans;
    }
    
 	/**
	 * @param:  name   =: name of account as String
	 * 	 		cached =: whether to look at cached or global
	 * @throws: InvalidTransactionError on improper name
	 * @return: Account associated with name
	 **/
    private Account lookupAccount(String name, Boolean init) throws InvalidTransactionError {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        int orig = accountNum;
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        if (name.length() > 1 && init) {
     	
        }
        return accounts[orig % 26];
    }
    
  	/**
 	 * @param:  error  =: case if closing due to error
 	 * @effects: This when closed by error restarts
 	 **/
    private void closeAccounts(Boolean error) {
    	Account act;
    	
    	// Close all write accounts after update
    	for (String key : acc_writ_val_cache.keySet()) {
    		act = lookupAccount(key, false);
    		if (!error) {
    			act.update(acc_writ_val_cache.get(key));
    		}
    		/* shared mutable state */
    		       act.close();
    		/* shared mutable state */
    	}
    	
    	// Close all read accounts
    	for (String key : acc_read_val_cache.keySet()) {
    		act = lookupAccount(key, false);
    		/* shared mutable state */
	             act.close();
	      /* shared mutable state */
    	}
    	
    	if (error) {
//    		try {
//				Thread.sleep((int)(Math.random() * 100) + 1);
//			} catch (InterruptedException e) {}
    		restart();
    	}
    }
    
  	/**
 	 * @param:  name   =: name of account as String
 	 * 	 		cached =: whether to look at cached or global
 	 * @throws: InvalidTransactionError on improper name
 	 * @return: Account associated with name
 	 **/
    private void openAccount(Account act, String w, String w0) throws TransactionAbortException {
   		
        	if (w.equals(w0)) {	                        // case this is the write account        
        		if (!acc_writ_val_cache.containsKey(w)) { // Avoid errors
     			
        			/* shared immutable state */
        			      act.open(true);
        			/* shared immutable state */
        			      
    				acc_writ_val_cache.put(w, accountsCache.get(w));
        			acc_read_val_cache.remove(w);
        		}
        	} else {
        		if (!acc_writ_val_cache.containsKey(w) && // case read
        		    !acc_read_val_cache.containsKey(w)) { // Avoid errors
        			
        			/* shared immutable state */
        			      act.open(false);
        			/* shared immutable state */
        			      
        			acc_read_val_cache.put(w, accountsCache.get(w));
        		}
        	}
    }
    
  
    private class ParseResult {
    	// public Account account;
    	public String name;
    	public ParseResult(Account a, String s) {
    		// account = a;parseAccount
    		name = s;
    	}
    }
    
   /**
  	 * @param:  name =: name of account as String
  	 * 	 		w0   =: name of account to write
  	 * @throws: TransactionAbortException on improper transaction
  	 * @return: ParseResult containing account name
  	 **/
    private ParseResult parseAccount(String name, String w0) throws TransactionAbortException {
      String lookedat = new String(name);
      int accountNum = (int) (name.charAt(0)) - (int) 'A';
      if (accountNum < A || accountNum > Z)
          throw new InvalidTransactionError();
      
      Account a = accounts[accountNum];
      openAccount(accounts[accountNum], String.valueOf((char)(accountNum%numLetters + 65)), w0);
      int accountValue = 0;
      if (acc_read_val_cache.containsKey(name)) {
    	  accountValue = acc_read_val_cache.get(name);
      }
      if (acc_writ_val_cache.containsKey(name)) {
    	  accountValue = acc_writ_val_cache.get(name);
      }
      Integer accountValue2 = new Integer(accountValue);
      for (int i = 1; i < name.length(); i++) {
    	  String curAcc = String.valueOf((char)(accountValue2%numLetters + 65));
          if (name.charAt(i) != '*')
              throw new InvalidTransactionError();
          if (!accountsCache.containsKey(curAcc)) {
        	  accountValue2 =  accounts[accountNum].peek();
              accountsCache.put(curAcc, accountValue2);       	  
          } else {
        	  accountValue2 = accountsCache.get(curAcc);
          }
          accountNum = (accountValue2 % numLetters);
      }   
      accountNum = (int) (name.charAt(0)) - (int) 'A';

      for (int i = 1; i < name.length(); i++) {
          if (name.charAt(i) != '*'){
              throw new InvalidTransactionError();       	  
          }
          if (acc_read_val_cache.containsKey(String.valueOf((char)(accountValue%numLetters + 65)))) {
        	  accountValue = acc_read_val_cache.get(String.valueOf((char)(accountValue%numLetters + 65)));
          }
          if (acc_writ_val_cache.containsKey(String.valueOf((char)(accountValue%numLetters + 65)))) {
        	  accountValue = acc_writ_val_cache.get(String.valueOf((char)(accountValue%numLetters + 65)));
          }
          openAccount(accounts[accountNum], String.valueOf((char)(accountNum%numLetters + 65)), w0);
          a = accounts[accountNum];
          accountNum = (accountValue % numLetters);
      }
      return new ParseResult(a,String.valueOf((char)(accountNum + 65)));
	}

    /**
   	 * @param:  name =: name of account as String
   	 * @return: bool if this is an account
   	 **/
    private Boolean isAccount(String name) {
        return !(name.charAt(0) >= '0' && name.charAt(0) <= '9');
    }
    
    /**
   	 * @effect: restarts the thread due to collision
   	 **/
    public void restart() {
    	run();
    	return;
    }
    
    @Override
    public void run() {
        String[] commands = transaction.split(";");

        accountsCache = new HashMap<String, Integer>();
        acc_writ_val_cache = new HashMap<String, Integer>();
        acc_read_val_cache = new HashMap<String, Integer>();
        String[] words;
        int i;
        for (i = 0; i < commands.length; i++) {
        	words = commands[i].trim().split("\\s");
            for (Integer k = 0; k < words.length; k += 2) {
            	if (isAccount(words[k])) {
            		accountsCache.put(words[k], lookupAccount(words[k], true).peek());
            	}
            }        	
        }
        for (i = 0; i < commands.length; i++) {
            words = commands[i].trim().split("\\s");
            if (words.length < 3)
                throw new InvalidTransactionError();
            if (!words[1].equals("="))
                throw new InvalidTransactionError();
            
            // 0 : Determine Accounts Needed
            ArrayList<String> found_accounts = new ArrayList<String>();
            for (Integer k = 0; k < words.length; k += 2) {
            	if (isAccount(words[k])) {
            		found_accounts.add(words[k]);
            	}
            }
            Collections.sort(found_accounts);
            
            // 1 : Open All Accounts Needed
            ParseResult act = null;
            for (String w : found_accounts) {
        		try {
					act = parseAccount(w, words[0]);
				} catch (TransactionAbortException e) {
					closeAccounts(true);
					return;
				}
            }
            
            // 2 : Verify Previously Peeked Values
            
            for (String w : acc_read_val_cache.keySet()) {
            	try {            		
               		lookupAccount(w, false).verify(acc_read_val_cache.get(w));
				} catch (TransactionAbortException e) {
					closeAccounts(true);
					return;
				}
            }
            
            // 2.5 : Log All Updates
            /// Handle 1st term
            Integer delta = 0;
            if (isAccount(words[2])) {
            	try {
					act = parseAccount(words[2], words[0]);
				} catch (TransactionAbortException e) {
					closeAccounts(true);
					return;
				}
            	if (acc_writ_val_cache.containsKey(act.name)) {
            		delta += acc_writ_val_cache.get(act.name);
            	} else {
            		delta += acc_read_val_cache.get(act.name);
            	}
            } else delta += new Integer(words[2]);
            for (int j = 3; j < words.length; j+=2) {
            	Integer value = 0;
            	if (isAccount(words[j+1])) {
                	try {
						act = parseAccount(words[j+1], words[0]);
					} catch (TransactionAbortException e) {
						closeAccounts(true);
						return;
					}  
                	if (acc_writ_val_cache.containsKey(act.name)) {
                		value += acc_writ_val_cache.get(act.name);
                	} else {
                		value += acc_read_val_cache.get(act.name);
                	}                	
            	} else {
            		value = new Integer(words[j+1]).intValue();
            	}
                if (words[j].equals("+"))
                    delta += value;
                else if (words[j].equals("-"))
                    delta -= value;
                else
                    throw new InvalidTransactionError();   
        	
            }
            acc_writ_val_cache.put(words[0], delta);
        }
        closeAccounts(false);
        // System.out.println("commit: " + transaction);
        return;
    }
}

public class MultithreadedServer {
	// requires: accounts != null && accounts[i] != null (i.e., accounts are properly initialized)
	// modifies: accounts
	// effects: accounts change according to transactions in inputFile
    public static void runServer(String inputFile, Account accounts[])
        throws IOException {
        
    	// read transactions from input file
        String line;
        BufferedReader input = new BufferedReader(new FileReader(inputFile));

        // Executor for tasks
        ExecutorService pool = Executors.newCachedThreadPool();
        
        // Execute the tasks
        while ((line = input.readLine()) != null) {
        	pool.execute(new Task(accounts, line));
        }
        
        pool.shutdown();
        try {
			pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

		}
        
        input.close();

    }
}

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

// TO DO: Task is currently an ordinary class.
// You will need to modify it to make it a task,
// so it can be given to an Executor thread pool.

class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private Account[] accountsCache;
    private String transaction;

    private HashMap<String, Integer> acc_writ_val_cache;
    private HashMap<String, Integer> acc_read_val_cache;
    
    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        transaction = trans;
    }
    
    // return account represented by string
    
    private Account lookupAccount(String name, Boolean cached) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        Account a;
        if (cached) a = accountsCache[accountNum];
        else a = accounts[accountNum];
        if (name.length() > 1) System.out.println("It should never print this D:");
        return a;
    }
    
    private void closeAccounts(Boolean error) {
    	Account act;
    	for (String key : acc_writ_val_cache.keySet()) {
    		act = lookupAccount(key,false);
    		if (!error) {
    			act.update(acc_writ_val_cache.get(key));
    		}
    		act.close();
    	}
    	for (String key : acc_read_val_cache.keySet()) {
    		act = lookupAccount(key,false);
    		act.close();
    	}
    	if (error) {
//    		try {
//				Thread.sleep((int)(Math.random() * 100) + 1);
//			} catch (InterruptedException e) {}
    		restart();
    	}
    }
    
    private void openAccount(Account act, String w, String w0) throws TransactionAbortException {
    		Integer pee = 0;
        	if (w.equals(w0)) {	         
        		if (!acc_writ_val_cache.containsKey(w)) {
        			pee = lookupAccount(w,true).peek();
        			act.open(true);
    				acc_writ_val_cache.put(w, pee);
        			acc_read_val_cache.remove(w);
        		}
        	} else {
        		if (!acc_writ_val_cache.containsKey(w) && !acc_read_val_cache.containsKey(w)) {
        			pee = lookupAccount(w,true).peek();
        			act.open(false);
        			acc_read_val_cache.put(w, pee);
        		}
        	}
    }
    
    private class ParseResult {
    	// public Account account;
    	public String name;
    	public ParseResult(Account a, String s) {
    		// account = a;
    		name = s;
    	}
    }
    
    private ParseResult parseAccount(String name, String w0) throws TransactionAbortException {
      int accountNum = (int) (name.charAt(0)) - (int) 'A';
      if (accountNum < A || accountNum > Z)
          throw new InvalidTransactionError();
      Account a = accounts[accountNum];
      openAccount(accounts[accountNum], String.valueOf((char)(accountNum%numLetters + 65)), w0);
      for (int i = 1; i < name.length(); i++) {
          if (name.charAt(i) != '*')
              throw new InvalidTransactionError();
          a = accounts[accountNum];
          openAccount(accounts[accountNum], String.valueOf((char)(accountNum%numLetters + 65)), w0);
          Integer accountValue = 0;
          if (acc_read_val_cache.containsKey(name)) accountValue = acc_read_val_cache.get(name);
          if (acc_writ_val_cache.containsKey(name)) accountValue = acc_writ_val_cache.get(name);
          accountNum = (accountValue % numLetters);
          a = accounts[accountNum];
      }
      return new ParseResult(a,String.valueOf((char)(accountNum + 65)));
	}

    private Boolean isAccount(String name) {
        return !(name.charAt(0) >= '0' && name.charAt(0) <= '9');
    }
    
    public void restart() {
    	run();
    	return;
    }
    
    @Override
    public void run() {
        String[] commands = transaction.split(";");
        
        accountsCache = accounts.clone();
        acc_writ_val_cache = new HashMap<String, Integer>();
        acc_read_val_cache = new HashMap<String, Integer>();

        for (int i = 0; i < commands.length; i++) {
            String[] words = commands[i].trim().split("\\s");
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
    	// 3 : Perform All Updates        
    	// 4 : Close All Open Accounts
        closeAccounts(false);
        System.out.println("commit: " + transaction);
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

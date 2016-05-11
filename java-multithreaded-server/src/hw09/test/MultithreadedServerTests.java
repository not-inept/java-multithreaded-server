package hw09.test;

import hw09.*;

import java.io.*;
import java.util.Vector;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

//class fileCreator {
//	
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 *    		  left   =: how many more lines to generate
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 **/
//
//// input
////    --> transaction  input | epsilon
//
//   private static void input(Writer writer, Random rand, 
//   								  int[] mimic, int left) throws IOException {
//   	
//  	   if (left > 0) {							   // Continue to make more transactions?
//  		   transaction(writer, rand, mimic);   // transaction expansion
//  		   input(writer, rand, mimic, left-1); // input expansion
//  	   }													// If skipped then epsilon expansion
//   }
//   
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 **/
//   
//// transaction
////    --> command  more_commands  \n
//   
//   private static void transaction(Writer writer, Random rand, 
//   										  int[] mimic) throws IOException {
//   	
//  	   command(writer, rand, mimic);                         // command expansion
//  	   more_commands(writer, rand, mimic, rand.nextInt(20)); // more_commands expansion
//  	   writer.write("\n");												// terminal write
//   }
//   
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 *    		  more   =: random number to decide if more transactions occur
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 **/
//   
//// more_commands
////    --> ;  command  more_commands
//   
//   private static void more_commands(Writer writer, Random rand, 
//   											 int[] mimic, int more) throws IOException {
//   	
//  	   if (more > 13) {														 // Continue to make more commands?
//  		   writer.write("; ");												 // Terminal write
//  		   command(writer, rand, mimic);								    // command expansion
//  		   more_commands(writer, rand, mimic, rand.nextInt(20));  // more_commands expansion
//  	   }																		 	 // If skipped then epsilon expansion
//   }
//  
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 **/
//   
//// command
////    --> account  =  val  val_tail
//   
//   private static void command(Writer writer, Random rand, 
//   									 int[] mimic) throws IOException {
//   	
//  	   int a = account(writer, rand, mimic);     // account expansion
//  	   writer.write(" = "); 						   // terminal write
//  	   int b = val(writer, rand, mimic, 1);      // val expansion
//  	   int c = val_tail(writer, rand, mimic, b); // val_tail expansion
//  	 
//  	   mimic[a] = b + c;								   // abstraction evaluation
//   }
//   
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 * @return:   returns account number
//	 **/
//   
//// account
////    --> capital_letter  indirects
//   
//   private static int account(Writer writer, Random rand, 
//   									int[] mimic) throws IOException {
//   	
//  	   int letter = rand.nextInt(26);          // get a random account number
//  	   writer.write((char) (letter+(int)'A')); // convert it to its name
//  	 
//  	   int redirects = indirects(writer, rand, 	           // indirects expansion
//  			                       mimic, rand.nextInt(20));
//
//  	   for (int i = 0; i < redirects; i++)     // recalculate according to redirects
//  		   letter = mimic[letter] % 26;
//  	 
//  	   return letter;
//   }
//   
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 *    		  more   =: random number to decide if more transactions occur
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 * @return:   returns the number of redirects
//	 **/
//   
//// indirects
////    --> *  indirects | epsilon
//   
//   private static int indirects(Writer writer, Random rand, 
//   									  int[] mimic, int more) throws IOException {
//   	
//  	   if (more > 13) {                      // Continue to make more commands?
//  		   writer.write("*");					  // terminal write
//  		   return 1 + indirects(writer, rand, // Recurse down the indirects
//  				 					   mimic, rand.nextInt(20));
//  	   }
//  	   return 0;
//   }
//   
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 *    		  max    =: number denoting the maximum number allowed
//	 *     						to remove from the accout to avoid negatives
//	 *     						negative means subtract; if negative abs(max)
//	 *     						is the maximum number
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 * @return:   returns value of account or income
//	 **/
//   
//// val
////    --> account | integer
//   
//   private static int val(Writer writer, Random rand, 
//   							  int[] mimic, int max) throws IOException {
//  		
//      if (max < 0) {                                  // If subtracting check for negative transactions
//      	Vector<Integer> plausible = new Vector<Integer>(); // Vector containing all of the plausible accounts
//         
//     		for (int i = 0; i < 26; i++) {   // populate the vector
//     		   if (mimic[i] < Math.abs(max)) // only if the account won't
//     			   plausible.add(i);				// cause a negative balance
//     		}
//     		
//         if (rand.nextInt(20) < 10 && plausible.size() > 0) { 				// If account
//         	int letter = plausible.get(rand.nextInt(plausible.size())); // get account
//          	writer.write((char) (letter+(int)'A'));							// terminal write
//          	return mimic[letter];													// return value of account
//         }
//         
//         int randInt = rand.nextInt(Math.abs(max)); 							// else return random int
//         writer.write(Integer.toString(randInt));								// terminal write
//      	return randInt;																
//         
//  	   } else {
//  	      if (rand.nextInt(20) < 10) {												// Case Account
//  	   	   return mimic[account(writer, rand, mimic)]; 						// Account expansion
//  	      } else {
//  	         int randInt = rand.nextInt(100);										// random number
//    		   writer.write(Integer.toString(randInt));							// terminal write
//    		   return randInt;
//  	      }
//  	   }
//   }
//   
//	/**
//	 * @param:    writer =: location to write to
//	 * 	 		  rand   =: random number generator
//	 * 			  mimic  =: int array mimic of the Account array
//	 *    		  max    =: number denoting the maximum number allowed
//	 *     						to remove from the accout to avoid negatives
//	 * @throws:   IOException upon improper file creation or write
//	 * 			     ** Should never happen **
//	 * @requires: writer, rand, mimi != null and mimic is of size 26
//	 * @returns returns value of account or income
//	 **/
//   
//// val_tail
////    --> +  val | -  val
//   
//   private static int val_tail(Writer writer, Random rand, 
//   									 int[] mimic, int max) throws IOException {
//   	
//      int returnInt;
//  	   if (rand.nextInt(20) < 10) { // case addition
//  		   writer.write(" + ");		  // terminal write
//  		   returnInt = 1;
//  	   } else {							  // case subtraction
//  		   writer.write(" - ");      // terminal write
//  		   returnInt = -1;
//  	   }
//  	 
//  	   return returnInt * val(writer, rand, mimic, returnInt*max);
//   }
// 
//	/**
//	 * @param:    location =: String to location
//	 * 			  mimic  =: int array mimic of the Account array
//	 * @requires: location, mimic != null and mimic is of size 26
//	 * 			  and location is a valid file path
//	 **/
//   
//   public static void create(String location, int[] mimic) {
//   	
//  	   Random rand = new Random();  // Random number generator
//  	   File f = new File(location); // create a new file
//  	   try {								  // attempt to create file and generate data
//	      
//  	   	f.createNewFile();						// Generate file		         				 	        
//	      Writer writer = new BufferedWriter( // create writer to the file
//	      					 new OutputStreamWriter(
//      		             new FileOutputStream(location), "utf-8"));
//	      
//         input(writer, rand, mimic, rand.nextInt(50)+25); // perform first expansion
//         
//	      writer.close();											 // close the writer
//	      
//      } catch (Exception e) { 									 // Because Java is needy
//	      // Should not happen if given valid file path
//	      e.printStackTrace();
//      }
//   }
//}
//


public class MultithreadedServerTests extends TestCase {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;
    private static Account[] accounts;
            
    protected static void dumpAccounts() {
	    // output values:
	    for (int i = A; i <= Z; i++) {
	       System.out.print("    ");
	       if (i < 10) System.out.print("0");
	       System.out.print(i + " ");
	       System.out.print(new Character((char) (i + 'A')) + ": ");
	       accounts[i].print();
	       System.out.print(" (");
	       accounts[i].printMod();
	       System.out.print(")\n");
	    }
	 }    
        
     @Test
	 public void testIncrement() throws IOException {
   	  
		// initialize accounts 
		accounts = new Account[numLetters];
		for (int i = A; i <= Z; i++) {
			accounts[i] = new Account(Z-i);
		}			 
		
		MultithreadedServer.runServer("src/hw09/data/increment", accounts);
	
		// assert correct account values
		for (int i = A; i <= Z; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs",Z-i+1,accounts[i].getValue());
		}

	 }
     
     @Test
   public void testRotate() throws IOException {
         System.out.println("STARTING ROTATE\n\n");

   	  // initialize accounts
   	accounts = new Account[numLetters];
   	
  		for (int i = A; i <= Z; i++) {
  			accounts[i] = new Account(Z-i);
  		}			 
  		
  		MultithreadedServer.runServer("src/hw09/data/rotate", accounts);
   
     	// assert correct account values
  		for (int i = A; i <= Z-2; i++) {
  			Character c = new Character((char) (i+'A'));
  			assertEquals("Account "+c+" differs",(47-2*i),accounts[i].getValue());
  		}	
  		assertEquals("Account Y differs",(47),accounts[24].getValue());
  		assertEquals("Account Z differs",(92),accounts[25].getValue());
  		
   }
     
//     @Test
//	 public void testRandom_One() throws IOException {
//   	  
//		// initialize accounts 
//		accounts = new Account[numLetters];
//		int[] mimic = new int[numLetters];
//		
//		for (int i = A; i <= Z; i++) {
//			accounts[i] = new Account(Z-i);
//			mimic[i]    = Z-i;
//		}		
//		
//		fileCreator.create("src/hw09/data/random", mimic); 
//		
//		MultithreadedServer.runServer("src/hw09/data/random", accounts);
// 	   
//     	// assert correct account values
//  		for (int i = A; i <= Z-2; i++) {
//  			Character c = new Character((char) (i+'A'));
//  			assertEquals("Account "+c+" differs",mimic[i],accounts[i].getValue());
//  		}	
//   }
//
//     @Test
//	 public void testRandom_Two() throws IOException {
//   	  
//		// initialize accounts 
//		accounts = new Account[numLetters];
//		int[] mimic = new int[numLetters];
//		
//		for (int i = A; i <= Z; i++) {
//			accounts[i] = new Account(Z-i);
//			mimic[i]    = Z-i;
//		}		
//		
//		fileCreator.create("src/hw09/data/random2", mimic); 
//		
//		MultithreadedServer.runServer("src/hw09/data/random2", accounts);
// 	   
//     	// assert correct account values
//  		for (int i = A; i <= Z-2; i++) {
//  			Character c = new Character((char) (i+'A'));
//  			assertEquals("Account "+c+" differs",mimic[i],accounts[i].getValue());
//  		}	
//   }
//
//     @Test
//	 public void testRandom_Three() throws IOException {
//   	  
//		// initialize accounts 
//		accounts = new Account[numLetters];
//		int[] mimic = new int[numLetters];
//		
//		for (int i = A; i <= Z; i++) {
//			accounts[i] = new Account(Z-i);
//			mimic[i]    = Z-i;
//		}		
//		
//		fileCreator.create("src/hw09/data/random3", mimic); 
//		
//		MultithreadedServer.runServer("src/hw09/data/random3", accounts);
// 	   
//     	// assert correct account values
//  		for (int i = A; i <= Z-2; i++) {
//  			Character c = new Character((char) (i+'A'));
//  			assertEquals("Account "+c+" differs",mimic[i],accounts[i].getValue());
//  		}	
//   }
}

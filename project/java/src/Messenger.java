/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String hostname, String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://" + hostname + ":" + dbport + "/" + dbname 
                      + "?user=" + user + "&password=" + passwd + "&ssl=false";
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url);
        
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
  public int getCurrSeqVal(String sequence) throws SQLException {
	  Statement stmt = this._connection.createStatement ();
	  ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	  if (rs.next())
		  return rs.getInt(1);
	  return -1;
  }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

  public static void ViewChatSubmenu(Messenger esql, String authorisedUser){
      boolean picking = true;
      ListChats(esql, authorisedUser);  
      System.out.println("\t1. View Messeges in chat");
      System.out.println("\t2. Reply to chat");
      System.out.println("\t3. Add member to chat");
      System.out.println("\t4. Delete memeber from chat");
      System.out.println("\t5. Delete chat");
      while(picking){
        switch (readChoice()){
          case 1: AddToContact(esql, authorisedUser); 
                  WaitForKey(); break;
          
           
          default : System.out.println("Unrecognized choice! choose again"); break;
        }
    }
      
            
  }
  
   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
     

 System.out.print ("ARGS= ");
 for (String arg: args)
     System.out.print (arg + " ");
 System.out.println();


      if (args.length != 5 && args.length != 4) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <hostname> <dbname> <port> <user> <password>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String hostname = args[0];
         System.out.println("hostname is " + hostname);
        
         String dbname = args[1];
         System.out.println("dbname is " + dbname);
        
         String dbport = args[2];
         System.out.println("dbport is " + dbport);
        
         String user = args[3];
         System.out.println("user is " + user);
        
         String password = "";
         if(args.length == 5 ){
           password = args[4];
         }
         System.out.println("password is " + password);
        
         esql = new Messenger (hostname, dbname, dbport, user, password);

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.print("\033[H\033[2J");
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            System.out.flush();
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.print("\033[H\033[2J");
                System.out.println("MAIN MENU\tWelcome, "+authorisedUser);
                System.out.println("---------");
                System.out.println("1. Add to contact list");
                System.out.println("2. Browse contact list");
                System.out.println("3. Browse blocked list");
                System.out.println("4. View chats");
                System.out.println("5. Create A Chat");
                System.out.println("6. Delete Account");
                System.out.println(".........................");
                System.out.println("9. Log out");
                System.out.flush();
                switch (readChoice()){
                   case 1: AddToContact(esql, authorisedUser); 
                           WaitForKey(); break;
                   case 2: ListContacts(esql, authorisedUser);  
                           WaitForKey(); break;
                   case 3: ListBlocked(esql, authorisedUser); 
                           WaitForKey(); break;
                   case 4: ViewChatSubmenu(esql, authorisedUser);
                  
                           WaitForKey(); break;
                   case 5: NewChat(esql, authorisedUser); 
                           WaitForKey(); break;
                   case 6: DeleteAccount(esql, authorisedUser); 
                           WaitForKey(); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!");  
                           WaitForKey(); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice
  
   /*
    * Wait for key to continue
    *
    **/
  public static void WaitForKey(){
      // returns only if a correct value is given.
      do {
         System.out.println("Press any key to continue");
         try { // read the integer, parse it and break.
            Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            //System.out.println("Your input is invalid!");
            break;
         }//end try
      }while (true);
  }
  
   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         //Creating empty contact\block lists for a user
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
         int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
         int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         String query = String.format("INSERT INTO USR (phoneNum, " +
                                      "login, password, block_list, " + 
                                      "contact_list) VALUES ('%s','%s','%s',%s,%s)"
                                      , phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'"
                                      , login, password);

         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static void AddToContact(Messenger esql, String authorisedUser){
      try{
      //First, ask user for other user's login and check if they exist
      String login;
      System.out.print("Enter the login name: ");
      login = in.readLine();
      String query1 = String.format("SELECT COUNT(*) FROM Usr WHERE login = '%s'", login);
      int rowCount = esql.executeQuery(query1);
    
      if (rowCount == 1) //requested user exists
      {   
        //Second, get contat list ID
        String query2 = String.format("SELECT contact_list FROM Usr WHERE login = '%s'", authorisedUser);
    
        List<List<String>> result;
        result = esql.executeQueryAndReturnResult(query2);
        int list_id = Integer.parseInt(result.get(0).get(0)); 
    
        //Third, insert contact to contact list
        String query3 = String.format("INSERT INTO USER_LIST_CONTAINS (list_id, list_member) " +
                        "VALUES('%s', '%s');", list_id, login);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully added to contacts!");
      }   
      else //requested user does not exist
        System.out.println ("This user does not exist.");
    
    
      }catch(Exception e){ 
        System.err.println (e.getMessage ());
      }   
   }//end


   public static void ListContacts(Messenger esql, String authorisedUser){
      try{
        // Browsing current user's contact list

        String query = String.format("SELECT ULC.list_member " + 
                       "FROM USR U, USER_LIST UL, USER_LIST_CONTAINS ULC " +
                       "WHERE U.login = '%s' AND UL.list_id = U.contact_list AND ULC.list_id = UL.list_id ", authorisedUser);

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total contacts: " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end

    public static void ListBlocked(Messenger esql, String authorisedUser){
      try{
        // Browsing current user's block list
        String query = String.format("SELECT ULC.list_member " +
                       "FROM USR U, USER_LIST UL, USER_LIST_CONTAINS ULC " +
                       "WHERE U.login = '%s' AND UL.list_id = U.block_list AND ULC.list_id = UL.list_id ", authorisedUser);

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total contacts: " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end

   public static void ListChats(Messenger esql, String authorisedUser){
      try{
        //select chats where user is a member of that chat
        String query = String.format("SELECT CL.chat_id, CL.member " + 
                       "FROM CHAT_LIST CL " + 
                       "WHERE CL.chat_id = ANY (SELECT chat_id " +
                                "FROM CHAT_LIST " +
                                "WHERE member = '%s' )", authorisedUser);

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total chats: " + rowCount);
      }catch(Exception e){ 
         System.err.println (e.getMessage());
      }   
   }//end
  
  private static boolean validUser(Messenger esql, String checkingUser){
    try{
      String query = String.format("SELECT login FROM usr WHERE login='%s'", checkingUser);
      if(esql.executeQueryAndPrintResult(query)>0){
        return true;
      }
      return false;
     } catch(Exception e){
      return false;
     } 
    
  }

  public static void NewChat(Messenger esql, String authorisedUser){
      System.out.println("Here are a list of your contacts, you can also add other users with their username: ");
      ListContacts(esql, authorisedUser);
      boolean picking = true;
      HashSet<String> users = new HashSet<String>();
      users.add(authorisedUser);
      try {
        while(picking){
          System.out.println("Type in the new reciepients name or hit [enter] to continue");
          String contact = in.readLine();
          System.out.println("contact is " + contact);
          if(validUser(esql, contact)){
            System.out.println("Adding " + contact + " to the recipients list");
            users.add(contact);
          } else if (contact.equals("")){
            System.out.println("continuing...");
            picking = false;
          } else {
            System.out.println("Not a valid Username try again!");
          }
        }
      } catch(Exception e){
       
      } 
      
      picking = true;
      String chat_type = "";
      try {
        while(picking){
          System.out.println("Pick the chat type:");
          System.out.println("\t1. group");
          System.out.println("\t2. private");
          String contact = in.readLine();
          switch(contact){
            case "1": chat_type = "group"; 
                    picking = false; break;
            case "2": chat_type = "private"; 
                    picking = false; break;
            default: System.out.println("Unrecognized choice!"); break;
          }
        }
      } catch(Exception e){
         System.err.println (e.getMessage ());
      }
      String query_insert_chat = "";
      try {
        //int seq_val = esql.getCurrSeqVal("chat_chat_id_seq");
        query_insert_chat = String.format(
                                     "INSERT INTO chat (chat_type, init_sender) VALUES ('%s','%s') RETURNING chat_id;"
                                     , chat_type, authorisedUser);
        List< List < String > > result = esql.executeQueryAndReturnResult(query_insert_chat);
        System.out.println("Executed");
        //System.out.println("Result is: " + result.toString());
        int seq_val = Integer.parseInt(result.get(0).get(0));
        System.out.println("chat_id is " + seq_val);
        
        /*
        String query_insert_chat_list = "INSERT INTO chat_list (chat_id, member) VALUES ('%s','%s')";
        
        for(int k = 0; k < users.size(); k++){
          query_insert_chat_list += " ('%s','%s')";
        }
        */
        
        for(String member : users){
          System.out.println("Adding user "+ member);
          String query_insert_chat_list = String.format("INSERT INTO chat_list (chat_id, member) VALUES ('%s','%s');"
                                                        , seq_val, member);
          esql.executeQuery(query_insert_chat_list);
        }
      
        System.out.println("Finished creating a chat!");
      } catch(Exception e){
        //System.out.println("Query: "+ query_insert_chat);
        //System.out.println("Query Error: "+ e.getMessage());
      }
  }//end 
     
   public static void DeleteAccount(Messenger esql, String authorisedUser){
    try{
     
      
    } catch(Exception e) {
      
    }
       
  }

  public static void ViewChat(){
    try{
      String get_chat_query = String.format("SELECT * FROM message WHERE chat_id=5001 ORDER BY msg_timestamp LIMIT 10"); 
      
    } catch(Exception e){
      
    }
    
  }
  
  
   public static void Query6(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end Query6

}//end Messenger

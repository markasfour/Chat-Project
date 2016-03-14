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
                    + "?user=" + user;
         
         if(passwd!="") {
        	url += "&password=" + passwd + "&ssl=false";
         }
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
      while(picking){
      	System.out.print("\033[H\033[2J"); 
        ListChats(esql, authorisedUser);  
      	System.out.println("\t1. Open Chat for viewing, replying, and editing members");
      	System.out.println("\t2. Delete chat");
      	System.out.println("\t9. return to main menu");
      	System.out.flush();
        switch (readChoice()){
          case 1: 
                  ViewMessages(esql, authorisedUser); 
                  break;
          case 2:
                  DeleteChat(esql, authorisedUser);
                  break;
          case 9:
          	      picking = false;
          	      break;
          
          
           
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
               case 1: CreateUser(esql);
                       WaitForKey(); break;
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
                System.out.println("2. Remove from contact list");
                System.out.println("3. Browse contact list");
                System.out.println("4. Add to blocked list");
                System.out.println("5. Remove from blocked list");
                System.out.println("6. Browse blocked list");
                System.out.println("7. View chats");
                System.out.println("8. Create a chat");
		System.out.println("9. Change status");
                System.out.println("10. Delete account");
                System.out.println(".........................");
                System.out.println("11. Log out");
                System.out.flush();
                switch (readChoice()){
                   case 1: AddToContact(esql, authorisedUser); 
                           WaitForKey(); break;
                  
                   case 2: RemoveFromContact(esql, authorisedUser);
                           WaitForKey(); break;
                  
                   case 3: ListContacts(esql, authorisedUser);  
                           WaitForKey(); break;
                  
                   case 4: AddToBlock(esql, authorisedUser);
                           WaitForKey(); break;
                   
                   case 5: RemoveFromBlock(esql, authorisedUser);
						   WaitForKey(); break; 
                  
                   case 6: ListBlocked(esql, authorisedUser); 
                           WaitForKey(); break;
                  
                   case 7: ViewChatSubmenu(esql, authorisedUser);
                            break;
                  
                   case 8: NewChat(esql, authorisedUser); 
                           WaitForKey(); break;
                  
		   case 9: UpdateStatus(esql, authorisedUser);
			   WaitForKey(); break;

                   case 10: boolean logout = DeleteAccount(esql, authorisedUser); 
						   if (logout) {
						   	   usermenu = false;
						   }break;
                  
                   case 11: usermenu = false; break;
                  
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
  
  public static int readChatID(){
    int input;
    do {
      System.out.print("Please select a Chat: ");
      try { // read the integer, parse it and break.
        input = Integer.parseInt(in.readLine());
        break;
      }catch (Exception e) {
        System.out.println("Your input is invalid!");
        continue;
      }//end try
    }while (true);
    return input;
    
    
  }
  
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
      System.out.print("Enter the user name: ");
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
                        "VALUES(%d, '%s');", list_id, login);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully added to contacts!");
      }   
      else //requested user does not exist
        System.out.println ("This user does not exist.");
    
    
      }catch(Exception e){ 
        System.err.println (e.getMessage ());
      }   
   }//end

  public static void AddToBlock(Messenger esql, String authorisedUser){
      try{
      //First, ask user for other user's login and check if they exist
      String login;
      System.out.print("Enter the user name: ");
      login = in.readLine();
      String query1 = String.format("SELECT COUNT(*) FROM Usr WHERE login = '%s'", login);
      int rowCount = esql.executeQuery(query1);
    
      if (rowCount == 1) //requested user exists
      {   
        //Second, get contat list ID
        String query2 = String.format("SELECT block_list FROM Usr WHERE login = '%s'", authorisedUser);
    
        List<List<String>> result;
        result = esql.executeQueryAndReturnResult(query2);
        int list_id = Integer.parseInt(result.get(0).get(0)); 
    
        //Third, insert contact to contact list
        String query3 = String.format("INSERT INTO USER_LIST_CONTAINS (list_id, list_member) " +
                        "VALUES(%d, '%s');", list_id, login);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully added to blocked list");
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
	 String query = String.format("SELECT US.login, US.phoneNum, US.status From USR US WHERE login = Any (SELECT ulc.list_member from usr u, user_list ul, user_list_contains ulc where u.login = '%s' and ul.list_id = u.contact_list and ulc.list_id = ul.list_id);", authorisedUser);
        //String query = String.format("Select US.login, US.phoneNum, US.status FROM USR US WHERE login = ANY (SELECT ULC.list_member, U.status, U.phoneNum " + 
          //             "FROM USR U, USER_LIST UL, USER_LIST_CONTAINS ULC " +
            //           "WHERE U.login = '%s' AND UL.list_id = U.contact_list AND ULC.list_id = UL.list_id) ", authorisedUser);

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
         System.out.println ("total blocked: " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end

   public static void ListChats(Messenger esql, String authorisedUser){
      try{
        //select chats where user is a member of that chat
        //String query = String.format("SELECT CL.chat_id, CL.member " + 
        //               "FROM CHAT_LIST CL " + 
          //             "WHERE CL.chat_id = ANY (SELECT chat_id " +
            //                    "FROM CHAT_LIST " +
              //                  "WHERE member = '%s' )", authorisedUser);
		//String query = String.format("SELECT m.chat_id as chat_id, m.sender_login as Sent_Latest_Message, m.msg_timestamp as Timestamp from message M where chat_id = ANY(select cl.chat_id from chat_list cl where member='%s') AND m.msg_timestamp = ANY (Select max (a.msg_timestamp) from message a where chat_id = any (select cl.chat_id from chat_list cl where member = '%s') Group by chat_id) order by m.msg_timestamp desc", authorisedUser, authorisedUser);
	String query = String.format(
		"SELECT " +
  		"  M.chat_id as Chat_ID, "+
  		"  M.sender_login as Sent_Latest_Message, "+
  		"  M.msg_timestamp as Timestamp " +
		"FROM " +
  		"  message M " +
		"WHERE " +
  		"  chat_id= ANY ( " +
      		"    SELECT " +
      		"      CL.chat_id " +
      		"    FROM " +
        	"      chat_list CL " +
      		"    WHERE member='%s') " +
  		"  AND " +
  		"    M.msg_timestamp = ANY ( " +
    		"      SELECT MAX(A.msg_timestamp) " +
    		"      FROM " +
       		"        Message A " +
    		" WHERE chat_id = ANY ( " +
      		"   SELECT CL.chat_id " +
      		"   FROM " +
        	"     chat_list CL " +
      		"   WHERE member='%s') " +
                " GROUP BY chat_id) " +
                " ORDER BY " +
	 	" M.msg_timestamp desc;"
	 	, authorisedUser, authorisedUser);
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total chats: " + rowCount);
      }catch(Exception e){ 
         System.err.println (e.getMessage());
      }   
   }//end
  
  private static boolean ValidUser(Messenger esql, String authorisedUser, String checkingUser){
    String query = "";
    try{
      //Check if user exists
      query = String.format("SELECT login FROM usr WHERE login='%s'", checkingUser);
      if(esql.executeQuery(query)>0){
        

        //Check if users blocklist contains authorisedUser
        query = String.format(
          "SELECT list_member FROM user_list_contains WHERE "
          + "list_member='%s' AND list_id = (SELECT block_list " 
          + "FROM usr where login ='%s');", authorisedUser, checkingUser);
        
        
        if(esql.executeQuery(query) > 0){
          //System.out.println("FALSE: " + query);
          return false;
        }  else {
          //System.out.println("TRUE: " + query);
          return true;
        }
        
      }
      return false;
     } catch(Exception e){
      System.out.println("QUERY ERROR: " + query);
      System.out.println("Message: " + e.getMessage());
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
          
          if(ValidUser(esql, authorisedUser, contact)){
            System.out.println("Adding " + contact + " to the recipients list");
            users.add(contact);
          } else if (contact.equals("")){
            if(users.size() < 2){
              System.out.println("You must add atleast one other member into the chat!");
            } else {
              System.out.println("Users in chat: ");
              for( String user : users){
                System.out.println("\t"+user); 
              }
              picking = false;
            }
          } else {
            System.out.println("Not a valid Username try again!");
          }
        }
      } catch(Exception e){
       
      } 
    
      String chat_type = "group";
      if( users.size() == 2){
        chat_type = "private";  
      }
      
      String query_insert_chat = "";
      try {
        query_insert_chat = String.format(
                                     "INSERT INTO chat (chat_type, init_sender) VALUES ('%s','%s');"
                                     , chat_type, authorisedUser);
        //List< List < String > > result = esql.executeQueryAndReturnResult(query_insert_chat);
       
        //System.out.println("Result is: " + result.toString());
        //int seq_val = Integer.parseInt(result.get(0).get(0));
        esql.executeUpdate(query_insert_chat);
        int seq_val = esql.getCurrSeqVal("chat_chat_id_seq");
        System.out.println("chat_id is " + seq_val);
        
      
        
        for(String member : users){
          String query_insert_chat_list = String.format("INSERT INTO chat_list (chat_id, member) VALUES ('%d','%s');"
                                                        , seq_val, member);
          esql.executeUpdate(query_insert_chat_list);
        }
     	if (chat_type == "group"){ 
        	System.out.println("Finished creating a group chat!");
	}
	else{
		System.out.println("Finished creating a private chat!");
	}
      } catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());
      }
  }//end 

  private static boolean ValidChat(Messenger esql, String authorisedUser, int chatID){
    try {
       String check_valid_query = String.format(
                                  "SELECT chat_id from chat_list WHERE chat_list.chat_id = %d " +
                                  "AND chat_list.member='%s';", chatID, authorisedUser);
      
      List<List< String> > result = esql.executeQueryAndReturnResult(check_valid_query);
      if(result.size() > 0){
        return true; 
      }
      else
      	  System.out.println("Invalid chat");
       
    } catch(Exception e){
      System.out.println("Query Error: " + e.getMessage());
    }
    return false;
    
  }

  private static boolean IsInitialSender(Messenger esql, String authorisedUser, int chatID){
	try {
		String check_initial_sender = String.format("SELECT init_sender FROM CHAT " +
										"WHERE chat_id = %d AND init_sender = '%s'", chatID, authorisedUser);
		List<List< String> > result = esql.executeQueryAndReturnResult(check_initial_sender);
      	if(result.size() > 0){
			return true; 
      	}
	}
	catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());
	}
	return false;
  }
  
  public static void ViewMessages(Messenger esql, String authorisedUser){
    boolean picking = true;
    int offset = 0;
    try{
      System.out.println("Select a chat to view messages in.");
      int chat = Integer.parseInt(in.readLine());
      if (ValidChat(esql, authorisedUser, chat))
      {
        while(picking){
          System.out.print("\033[H\033[2J");
          System.out.println("Showing messages " + offset + " to " + (offset + 10));
          int limit = 10;
          String get_chat_query = String.format(
                  "SELECT msg_id, msg_timestamp, msg_text FROM message WHERE chat_id= %d ORDER BY msg_timestamp desc LIMIT %d OFFSET %d"
                  , chat, limit, offset);

          esql.executeQueryAndPrintResult(get_chat_query);
          //ask if user wants to view more. increment limit by 10. clear screen and reexecute query. loop until user says no    
          System.out.println("\t1. Reply to chat");
          System.out.println("\t2. See next 10 messages");
          System.out.println("\t3. See previous 10 messages");
		  System.out.println("\t4. Edit message");
		  System.out.println("\t5. Delete message");	
          //these options are given only if the user is the initial sender for these chats
          if (IsInitialSender(esql, authorisedUser, chat))
          {
			System.out.println("\t6. Add members to chat");
			System.out.println("\t7. Remove members from chat");
          }
          System.out.println("\t8. return to main menu");
          switch(readChoice()){  
            case 1: 
                    ReplyChat(esql, authorisedUser, chat);
                    break;
            case 2: 
                    offset += 10;
                    break;
            case 3: 
                    if(offset > 0){
                      offset -= 10;
                    }
                    break;
            case 4: 
					EditMessage(esql, authorisedUser, chat);
					break;
            case 5:
					DeleteMessage(esql, authorisedUser, chat);
					break;
            case 6: 
					if (IsInitialSender(esql, authorisedUser, chat)){
						AddMember(esql, authorisedUser, chat);
					}
					break;
			
			case 7: 
					if (IsInitialSender(esql, authorisedUser, chat)){
						DeleteMember(esql, authorisedUser, chat);
					}
					break;
            case 8:
                    picking = false;
                    break;
            default:
                    break;
          }
        }
      }
      else
      {
      	System.out.println("Invalid chat");
      }
      
    } catch(Exception e){
      System.out.println("Query Error: " + e.getMessage());
    }
    
  }
  
  public static void ReplyChat(Messenger esql, String authorisedUser, int chatID){
    try {
      
      /*
      // Ask user for the chat_id
      System.out.println("Please enter a chat id.");
      int chatID;
      do{
        chatID = readChoice();
        System.out.println("chatID is " + chatID);
      }while(!ValidChat(esql, authorisedUser, chatID));
      */
      
      // Ask user for a message to send
      System.out.println("Enter a message to send:");
      String message = in.readLine();
      
      
      // Send message
      String insert_chat_query = String.format(
                                 "INSERT INTO message (msg_text, sender_login,chat_id) VALUES ('%s','%s','%d')"
                                 , message, authorisedUser, chatID);
      
      esql.executeUpdate(insert_chat_query);
      
      System.out.println("Message sent!");
    } catch(Exception e) {
      System.out.println("Query Error: " + e.getMessage());
    }
    
  }
  
  public static void RemoveFromContact(Messenger esql, String authorisedUser){
	try{	
    //Get contat list ID
    String query1 = String.format("SELECT contact_list FROM Usr WHERE login = '%s'", authorisedUser);

    List<List<String>> result;
    result = esql.executeQueryAndReturnResult(query1);
    int list_id = Integer.parseInt(result.get(0).get(0)); 
	
	//Ask user for other user's login and check if they exist
	String login;
	System.out.print("Enter the user name: ");
	login = in.readLine();
	String query2 = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = %d and list_member= '%s'", list_id, login);
	int rowCount = esql.executeQuery(query2);
      
      if (rowCount > 0) //requested user exists
      {   
        //Delete contact from contact list
        String query3 = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id = %d and list_member = '%s'", list_id, login);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully removed from contacts");
      }   
      else //requested user does not exist
        System.out.println ("This user does not exist in your contacts.");
   }
   catch(Exception e) {
	System.out.println("Query Error: " + e.getMessage());
   }
  }

  public static void RemoveFromBlock(Messenger esql, String authorisedUser){
    try{	
    //Get block list ID
    String query1 = String.format("SELECT block_list FROM Usr WHERE login = '%s'", authorisedUser);

    List<List<String>> result;
    result = esql.executeQueryAndReturnResult(query1);
    int list_id = Integer.parseInt(result.get(0).get(0)); 
	
	//Ask user for other user's login and check if they exist
	String login;
	System.out.print("Enter the user name: ");
	login = in.readLine();
	String query2 = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = %d and list_member= '%s'", list_id, login);
	int rowCount = esql.executeQuery(query2);
      
      if (rowCount > 0) //requested user exists
      {   
        //Delete contact from block list
        String query3 = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id = %d and list_member = '%s'", list_id, login);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully removed from blocked");
      }   
      else //requested user does not exist
        System.out.println ("This user does not exist in your blocked list.");
   }
   catch(Exception e) {
	System.out.println("Query Error: " + e.getMessage());
   }
  }
  
  
  public static void DeleteChat(Messenger esql, String authorisedUser){
	try{
		System.out.println("Select a chat to delete.");
		int chat = Integer.parseInt(in.readLine());
		if(IsInitialSender(esql, authorisedUser, chat))
		{
			System.out.println("Deleting chat...");
			String delete_chat = String.format("DELETE FROM Chat where chat_id = %d", chat);
			esql.executeUpdate(delete_chat);
			
			System.out.println("Successfully deleted chat");
			WaitForKey();
		}
		else
		{
			System.out.println("You are not authorized to delete this chat");
			WaitForKey();
		}
	}
	catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());
	}
  }
  
  public static void DeleteMember(Messenger esql, String authorisedUser, int chatID){
  	  try{
  	  System.out.println("Here is a list of the recipients: ");
      String query = String.format("SELECT member FROM chat_list WHERE chat_id = %d", chatID);
      int number_of_recipients = esql.executeQueryAndPrintResult(query);
      boolean picking = true;
      	if (number_of_recipients >= 3)
      	{
        	while(picking){
          	  System.out.println("Type in the recipient to remove or hit [enter] to continue");
          	  String contact = in.readLine();
          	  
          	  if(ValidUser(esql, authorisedUser, contact)){
            	System.out.println("Removing " + contact + " from the recipients list");
				String query_insert_chat_list = String.format("DELETE FROM chat_list WHERE chat_id = %d AND member = '%s'" , chatID, contact);
				esql.executeUpdate(query_insert_chat_list);
          	  } 
          	  else if (contact.equals("")){
              	  picking = false;
            	}
          	  else {
            	System.out.println("Not a valid Username try again!");
          	  }
        	}
        }
		else{
			System.out.println("Only 2 members in chat. You can only delete entire chat");
			WaitForKey();
		}
      } catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());       
      }    
  }
  
  public static void AddMember(Messenger esql, String authorisedUser, int chatID){
	System.out.println("Here are a list of your contacts, you can also add other users with their username: ");
      ListContacts(esql, authorisedUser);
      boolean picking = true;
      try {
        while(picking){
          System.out.println("Type in the new recipient name or hit [enter] to continue");
          String contact = in.readLine();
          
          if(ValidUser(esql, authorisedUser, contact)){
            System.out.println("Adding " + contact + " to the recipients list");
			String query_insert_chat_list = String.format("INSERT INTO chat_list (chat_id, member) VALUES (%d,'%s')" , chatID, contact);
			esql.executeUpdate(query_insert_chat_list);
          } 
          else if (contact.equals("")){
              picking = false;
            }
          else {
            System.out.println("Not a valid Username try again!");
          }
        }
      } catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());       
      }    
    
  }

  private static boolean IsMessageSender(Messenger esql, String authorisedUser, int chatID, int message){
	try{
		String query = String.format("SELECT * FROM message " +
						"WHERE msg_id = %d and chat_id = %d and sender_login = '%s'",
						message, chatID, authorisedUser);
		List<List< String> > result = esql.executeQueryAndReturnResult(query);
      	if(result.size() > 0){
			return true; 
      	}
	}
	catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());
	}
	return false;
  }

  public static void DeleteMessage(Messenger esql, String authorisedUser, int chatID){
	try{
		System.out.println("Select a message to delete.");
		int message = Integer.parseInt(in.readLine());
		if(IsMessageSender(esql, authorisedUser, chatID, message))
		{
			System.out.println("Deleting message...");
			String delete_message = String.format("DELETE FROM message where msg_id = %d", message);
			esql.executeUpdate(delete_message);
			
			System.out.println("Successfully deleted message");
			WaitForKey();
		}
		else
		{
			System.out.println("You are not authorized to delete this message");
			WaitForKey();
		}
	}
	catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());
	}
  }
	
  public static void EditMessage(Messenger esql, String authorisedUser, int chatID){
	try{
		System.out.println("Select a message to edit.");
		int message = Integer.parseInt(in.readLine());
		if(IsMessageSender(esql, authorisedUser, chatID, message))
		{
			System.out.println("This message said: ");
			String prev_message = String.format("SELECT msg_text FROM message WHERE msg_id = %d", message);
			esql.executeQueryAndPrintResult(prev_message);

      	  	System.out.println("Enter the edited message:");
      	  	String message_text = in.readLine();
      	  	String edit_message = String.format("UPDATE message SET msg_text = '%s' WHERE msg_id = %d" ,message_text, message);
      	  	esql.executeUpdate(edit_message);

			System.out.println("Successfully edited message");
			WaitForKey(); 
		}
		else
		{
			System.out.println("You are not authorized to edit this message");
			WaitForKey();
		}
	}
	catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());
	}
  }

  public static boolean DeleteAccount(Messenger esql, String authorisedUser){
    try{
		System.out.println("Are you sure you would like to delete your account?");
		System.out.println("1 = Yes. 2 = No");
		switch (readChoice()){
          case 1: 
                  System.out.println("Deleting account...");
                  String terminate = String.format("DELETE FROM usr WHERE login = '%s'", authorisedUser);
                  esql.executeUpdate(terminate);
                  
                  System.out.println("Account successfully deleted");
                  WaitForKey();
				  return true; 
          case 2:
                  return false; 
        }

    } catch(Exception e) {
      System.out.println("Query Error: " + e.getMessage());
    }
    return false;   
  }

  public static void UpdateStatus(Messenger esql, String authorisedUser){
	try{
		System.out.println("Your current status is: ");
		String status = String.format("Select status from usr where login = '%s'", authorisedUser);
		esql.executeQueryAndPrintResult(status);
		System.out.println("Would you like to edit your status?");
		System.out.println("1 = Yes. 2 = No");
		switch (readChoice()){
          	case 1:
			System.out.println("Enter your new status: ");
			String new_status = in.readLine();	
			String edit_status = String.format("UPDATE usr SET status = '%s' WHERE login = '%s'", new_status, authorisedUser);
			esql.executeUpdate(edit_status);

			System.out.println("Successfully updated your status");
		case 2:
			return;
		}
	}
	catch(Exception e){
		System.out.println("Query Error: " + e.getMessage());	
	}
  }
  
}//end Messenger

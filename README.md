# Chat-Project


## Installation and Setup

To install make sure a postgres db is running at localhost.

Port and db name must be specified in .bashrc or added to the compile.sh
Enviroment variables names need to be `$PGPORT` and `$DB_NAME` 

Create and populate it the database run create_db.sh in `project/sql/scripts/`
Make sure all your .csv files are located in your `$PGDATA` path.

Start the messaging application by running compile.sh in `project/java/scripts/`

## Special Features

* Customized Welcome screen

Main menu will display users login name.

* Screen clears inbetween selects to improve the User's experience.

After most of the menu options the screen is cleared to keep the UI cleaner than if things kept printing to the console. Some menu options had to have a WaitForKey(); integrated into the menu option so the user can still see the output of his option.

* Option to connect to an Amazon RDS, the compile.sh has a commented out section for this. 

The commented sectoin of the compile.sh will set a different hostname, port, user, password and different JDBC library. The different JDBC driver was required as the server is Postgres version 9.14 which is incompatible with the 8.13 one found on well.

* Listing contacts includes the phone number

Now when you list all of your chats you will also be shown the users phone number for ease of access.

* A user cannot message people who have them on their blocked list

* When creating a chat we list all of your contacts for easier usage.

* Users can edit their status to share with all of their contacts

* Several indexes are present to enhance the speed of the database queries\
  * the chat id is indexed because it is used in multiple joins
  * the login name is also indexed because the database scans through the user table hashes for the unique login name
  * the list id is indexed becuase the list id is constantly being looked for
  * Hashing is used for indexing because there are no inequality searches (there are only equality searches)

## Usage

After running compile.sh you can either create a user or login as an existing user.

Once logged in you are personally welcomed to a variety options:

Menu option 1-3
These menu items can be used to view and modify your contact list.
Your contact list give you an easy way add users when creating a chat

Menu option 4-6
These menu items can be used to view and modify your blocked list.
Users that are blocked can not add you to their contacts and they cannot send you messages

Menu option 7
Will go into a submenu that can be used to view your chats 

Menu option 8
Will ask for users to add to a new chat.

Menu option 9
Will delete all of your accounts information.

## Problems

Using postgers on well is very annoying to set up every time, Solutions to this include setting the `$PG_DATA` path to the directory you have your data in. It is also possible to set the static paths in the `load_data.sql` as seen in `load_data_rds.sql`. We ended up trying to use an Amazon RDS to keep all of the data loaded into the database and allow easier usage of concurrent users. 

The version diffences between the RDS (Postgres 9.14) and Well (Postgre 8.1.23) caused some issues as we developed for RDS and Postgres 9.14, eventually we had to change some of the queries are remove the `RETURN` clause to allow use with Well and the older version of Postgres

The UI menu needs to be addressed, and contact list should have a submenu as well as blocked list. The create chat option should be in the submenu for viewing and managing chats.

## Authors

###Mark Asfour

Queries (view blocked list, add to contacts, delete contact and block, add users to chat, delete user account, delete message, add message), edited create_tables.sql, created indexes

###Mehran Ghamaty 

UI menus (clear screen, personalized welcome, submenus for specific queries), view chat reply to chat, made lsit chats have status + phonenumber, set up database, RDS

## Bugs

Phone number is not validated.

--http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/PostgreSQL.Procedural.Importing.html

\copy USER_LIST FROM '/home/mehran/workspace/Chat-Project/project/data/usr_list.csv' WITH DELIMITER ';';

ALTER SEQUENCE user_list_list_id_seq RESTART 55906;

\copy USR FROM '/home/mehran/workspace/Chat-Project/project/data/usr.csv' WITH DELIMITER ';';

\copy USER_LIST_CONTAINS FROM '/home/mehran/workspace/Chat-Project/project/data/usr_list_contains.csv' WITH DELIMITER ';';

\copy CHAT FROM '/home/mehran/workspace/Chat-Project/project/data/chat.csv' WITH DELIMITER ';';

ALTER SEQUENCE chat_chat_id_seq RESTART 5001;

\copy CHAT_LIST FROM '/home/mehran/workspace/Chat-Project/project/data/chat_list.csv' WITH DELIMITER ';';

\copy MESSAGE (msg_id, msg_text, msg_timestamp, sender_login, chat_id) FROM '/home/mehran/workspace/Chat-Project/project/data/message.csv' WITH DELIMITER ';';

ALTER SEQUENCE message_msg_id_seq RESTART 50000;


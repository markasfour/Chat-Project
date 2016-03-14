CREATE INDEX chat_index ON chat using HASH (chat_id); 

CREATE INDEX user_name_index ON usr using HASH (login);

CREATE INDEX list_id_index ON user_list_contains using HASH (list_id);

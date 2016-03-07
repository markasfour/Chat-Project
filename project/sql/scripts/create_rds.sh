#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "DIR is "  $DIR
echo "creating tables..."
chat < $DIR/../src/create_tables.sql
echo "creating indexes..."
chat < $DIR/../src/create_indexes.sql
echo "loading data"
#user_list_upload="\\\copy user_list from '$DIR/../../data/usr_list.csv' with DELIMITER ';'" 
#chat -c $user_list_upload;
chat  < $DIR/../src/load_data_rds.sql
echo "finished!"

-- Table Creation
create table bankmembers ( 
    member_id number(10) primary key,
    user_name varchar2(255) unique not null,
    first_name varchar2(255) not null,
    last_name varchar2(255) not null,
    user_password varchar2(255) not null,
    pin_number varchar2(255) not null
);

create table bankaccounts (
    account_id number (10) primary key,
    account_type varchar2(255) not null,
    account_no varchar2(255) unique not null,
    member_id number (10) not null,
    balance binary_float not null
);

create table transactions (
    transaction_id number (10) primary key,
    source_account_no varchar2(255) not null,
    end_account_no varchar2(255) not null,
    member_id number (10) not null,
    transaction_type varchar2(255) not null,
    transaction_amount binary_float not null,
    transaction_time timestamp(0) not null
);

alter table bankaccounts add constraint user_foreign_key
    foreign key (member_id) references 
	bankmembers (member_id) on delete cascade;
        
alter table transactions add constraint member_transact_foreign_key
    foreign key (member_id) references
    bankmembers (member_id) on delete cascade;
    
alter table transactions add constraint src_acct_no_foreign_key
    foreign key (source_account_no) references
    bankaccounts (account_no) on delete cascade;
    
alter table transactions add constraint end_acct_no_foreign_key
    foreign key (end_account_no) references
    bankaccounts (account_no) on delete cascade;
    
-- Stored Procedures and Helper Sequences
create sequence acct_id
    start with 1
    minvalue 1
    increment by 1;

--acct_value is a random 10-digit number generated in Java code 
--initial balance is a number specified by the user of the bank application

create or replace procedure add_account(acct_value varchar, member_id number, account_type varchar, 
                                        initial_balance binary_float, account_val out number) as 
begin
    insert into bankaccounts values(acct_id.nextval, account_type, acct_value, member_id, initial_balance);     
    account_val := acct_id.currval;
end;

create sequence user_id
    start with 1
    minvalue 1
    increment by 1;
    
create or replace procedure add_member(member_first_name varchar, member_last_name varchar, member_user_name varchar,
                                        member_password varchar, member_pin number, member_val out number) as

begin
    insert into bankmembers values(user_id.nextval, member_user_name, member_first_name, 
                                    member_last_name, member_password, member_pin);
    member_val := user_id.currval;
end;

create or replace procedure delete_account(acct_number varchar) as

begin
    delete from bankaccounts where account_no = acct_number;
end;

create or replace procedure delete_member(portal_name varchar) as

begin
    delete from bankmembers where user_name = portal_name;
end;

create or replace procedure deposit(account_num varchar, amount binary_float, result out number) as
begin
    update bankaccounts set balance = (balance + amount) where account_no = account_num;
    result := 1;
end;

create or replace procedure withdraw(account_num varchar, amount binary_float, result out number) as
current_balance binary_float;
begin
    select balance into current_balance from bankaccounts where account_no = account_num;
    if (current_balance - amount) >= 0 then
        update bankaccounts set balance = (current_balance - amount) where account_no = account_num;
        result := 1; 
    else
        result := -1;
    end if;
end;

create or replace procedure transfer(source_account_num varchar, end_account_num varchar, 
                                                amount binary_float, result out number) as
source_account_balance binary_float;
end_account_balance binary_float;
begin
    select balance into source_account_balance from bankaccounts where account_no = source_account_num;
    select balance into end_account_balance from bankaccounts where account_no = end_account_num;
    
    if (source_account_balance - amount) >= 0 then
        update bankaccounts set balance = (source_account_balance - amount) where account_no = source_account_num;
        update bankaccounts set balance = (end_account_balance + amount) where account_no = end_account_num;
        result := 1;
    else
        result := -1;
    end if;
end;

create or replace procedure change_user_name(new_user_name varchar, user_id number, result out number) as
begin
    update bankmembers set user_name = new_user_name where member_id = user_id;
    result := 1;
end;

create or replace procedure change_password(new_password varchar, user_id number, result out number) as
begin
    update bankmembers set user_password = new_password where member_id = user_id;
    result := 1;
end;

create or replace procedure change_pin(new_pin varchar, user_id number, result out number) as
begin
    update bankmembers set pin_number = new_pin where member_id = user_id;
    result := 1;
end;

create or replace procedure change_first_name(new_first_name varchar, user_id number, result out number) as
begin
    update bankmembers set first_name = new_first_name where member_id = user_id;
    result := 1;
end;

create or replace procedure change_last_name(new_last_name varchar, user_id number, result out number) as
begin
    update bankmembers set last_name = new_last_name where member_id = user_id;
    result := 1;
end;

create sequence transact_id
    start with 1
    minvalue 1
    increment by 1;
    
create or replace procedure add_new_transaction(source_account_no varchar, end_account_no varchar, user_id number,
                                                transact_type varchar2, amount binary_float, trans_val out number) as
begin
    insert into transactions values(transact_id.nextval, source_account_no, end_account_no, 
                                    user_id, transact_type, amount, CURRENT_TIMESTAMP);                         
    trans_val := transact_id.currval;
end;
    
commit;
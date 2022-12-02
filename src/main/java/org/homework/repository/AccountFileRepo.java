package org.homework.repository;


import org.homework.annotation.Service;
import org.homework.entity.Account;

//@Service
public class AccountFileRepo extends FileRepoAbstract<Account> implements RepositoryAccount{

    public AccountFileRepo(){
        super(Account[].class, "accounts.json");
        System.out.println("Выполнен конструктор AccFileRepo");
    }

}

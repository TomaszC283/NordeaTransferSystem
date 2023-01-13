package com.transfersystem.dto;

public class Storage {

    private static Example accounts;

    public static Example getAccounts() {
        if (accounts == null) {
            accounts = new Example();
        }
        return accounts;
    }
}

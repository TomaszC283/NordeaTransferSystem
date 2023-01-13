package com.transfersystem;

import com.transfersystem.service.AccountsService;

import java.io.FileNotFoundException;

public class TransferSystemApp {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Transfer system App Runned! Initializing storage...");
        AccountsService.initializeStorage();
        System.out.println("Storage initialized!");
    }
}

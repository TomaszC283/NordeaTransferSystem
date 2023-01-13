package com.transfersystem.service;

import com.google.gson.Gson;
import com.transfersystem.dto.Example;
import com.transfersystem.dto.Storage;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class AccountsService {

    public static void initializeStorage() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/example-transfer-system.json"));
        Storage.getAccounts().setAccounts(new Gson().fromJson(br, Example.class).getAccounts());
    }

    public static void saveChangesToJsonStorage() throws IOException {
        System.out.println("Saving changes to JSON");
        FileWriter fw = new FileWriter("src/main/resources/example-transfer-system.json");
        Gson gson = new Gson();
        gson.toJson( Storage.getAccounts(), fw);
        fw.flush();
        fw.close();
    }
}

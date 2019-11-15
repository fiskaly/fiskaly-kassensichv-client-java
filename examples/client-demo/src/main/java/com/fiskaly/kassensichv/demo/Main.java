package com.fiskaly.kassensichv.demo;

import com.fiskaly.kassensichv.client.ClientFactory;
import com.fiskaly.kassensichv.demo.commands.RunCommand;
import com.fiskaly.kassensichv.sma.GeneralSma;
import okhttp3.OkHttpClient;
import picocli.CommandLine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final String API_KEY = System.getenv("API_KEY");
        final String API_SECRET = System.getenv("API_SECRET");

        boolean envCorrect = true;

        if (API_KEY == null) {
            envCorrect = false;

            System.err.println("Please provide a value for the 'API_KEY' environment variable!");
        }

        if (API_SECRET == null) {
            envCorrect = false;

            System.err.println("Please provide a value for the 'API_SECRET' environment variable!");
        }

        if (!envCorrect) {
            System.exit(1);
        }

        OkHttpClient client = ClientFactory.getClient(
                API_KEY,
                API_SECRET,
                new GeneralSma()
        );

        RunCommand runCommand = runCommand = new RunCommand(API_KEY, API_SECRET, client);
        int line = new CommandLine(runCommand)
                .execute(args);

        System.exit(line);
    }
}

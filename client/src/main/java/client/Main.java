package client;

import client.utils.ResponseHandler;

public class Main {
    public static void main(String[] args) {
        Client client = new Client(1337, 500, 100);
        client.run();
    }
}

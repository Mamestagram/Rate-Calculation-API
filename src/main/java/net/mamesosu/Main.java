package net.mamesosu;

import net.mamesosu.object.Server;

// bancho.pyの.data内で実行されているものとする
public class Main {

    public static void main(String[] args) {
        Server server = new Server();

        server.start();
    }
}
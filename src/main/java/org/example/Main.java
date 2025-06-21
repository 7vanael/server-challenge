package org.example;

import Router.Router;
import Router.HomeHandler;
import Router.DirectoryHandler;
import Router.FileHandler;
import Router.HelloHandler;
import Router.FormHandler;
import Router.PingHandler;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static int port = 80;
    public static String root = "testroot";
    public static String name = "Challenge Server";

    private static String usage = "  -p     Specify the port.  Default is 80.\n" +
            "  -r     Specify the root directory.  Default is the current working directory.\n" +
            "  -h     Print this help message\n" +
            "  -x     Print the startup configuration without starting the server\n";

    public static void main(String[] args) {
        if (!parseArgs(args)) {
            return;
        }
        Router router = new Router(name);
        Path rootPath = Paths.get(root);

        router.addRoute("GET", "/", new HomeHandler(rootPath, name));
        router.addRoute("GET", "index.html", new HomeHandler(rootPath, name));
        router.addRoute("GET", "/index.html", new HomeHandler(rootPath, name));
        router.addRoute("GET", "/hello", new HelloHandler(rootPath, name));
        router.addRoute("GET", "/listing", new DirectoryHandler(rootPath, name));
        router.addRoute("GET", "/listing/*", new DirectoryHandler(rootPath, name));
        router.addRoute("GET", "/form", new FormHandler(rootPath, name));
        router.addRoute("POST", "/form", new FormHandler(rootPath, name));
        router.addRoute("GET", "/ping", new PingHandler(rootPath, name));

        router.addRoute("GET", "/*", new FileHandler(rootPath, name));

        Server server = new Server(port, root, router);
        System.out.println("new Server initialized");

        server.startServer();
        System.out.println("Called start server from main");
    }

    private static boolean parseArgs(String[] args) {
        boolean helpFlag = false;
        boolean printFlag = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    int targetP;
                    if (i + 1 >= args.length) {
                        System.out.println(usage);
                        return false;
                    } else {
                        try {
                            targetP = Integer.parseInt(args[i + 1]);
                            i++;
                            if (isValidPort(targetP)) {
                                port = targetP;
                            } else {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid port");
                            System.out.println(usage);
                            return false;
                        }

                    }
                    break;
                case "-r":
                    if (i + 1 >= args.length) {
                        System.out.println(usage);
                        return false;
                    } else {
                        String targetR = args[i + 1];
                        i++;
                        if (isValidRoot(targetR)) {
                            root = targetR;
                        } else {
                            return false;
                        }
                    }
                    break;
                case "-h":
                    System.out.println(usage);
                    helpFlag = true;
                    break;
                case "-x":
                    printFlag = true;
                    break;
                default:
                    System.out.println("Option not known: " + args[i]);
                    System.out.println(usage);
                    return false;
            }
        }
        if (printFlag) {
            String absolutePath = new File(root).getAbsolutePath();
            System.out.println(name);
            System.out.println("Running on port: " + port);
            System.out.println("Serving files from: " + absolutePath);
        }
        return !helpFlag && !printFlag;
    }

    private static boolean isValidRoot(String targetR) {
        if (targetR.isEmpty()) {
            System.out.println("Root path cannot be empty.");
            System.out.println(usage);
            return false;
        }
        File rootDir = new File(targetR);
        if (!rootDir.exists()) {
            System.out.println("Directory does not exist: " + targetR);
            return false;
        } else if (!rootDir.isDirectory()) {
            System.out.println("Not a directory: " + targetR);
            return false;
        }
        return true;
    }

    private static boolean isValidPort(int targetP) {
        if (targetP < 1 || 65535 < targetP) {
            System.out.println("Port must be between 1 and 65535");
            return false;
        }
        try (ServerSocket socket = new ServerSocket(targetP)) {
            return true;
        } catch (IOException e) {
            System.out.println("Port already in use: " + targetP);
            return false;
        }
    }
}
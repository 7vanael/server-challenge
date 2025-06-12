package org.example;

import java.io.File;

public class Main {
    public static int port = 80;
    public static String root = ".";

    private static String usage = "-p     Specify the port.  Default is 80.\n" +
            "-r     Specify the root directory.  Default is the current working directory.\n" +
            "-h     Print this help message\n" +
            "-x     Print the startup configuration without starting the server\n";

    public static void main(String[] args) {
        if (parseArgs(args)) {
//            start Server
        }

    }

    private static boolean parseArgs(String[] args) {
        boolean helpFlag = false;
        boolean blockFlag = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    if (i + 1 >= args.length) {
                        System.out.println(usage);
                        return false;
                    } else {
                        try {
                            port = Integer.parseInt(args[i + 1]);
                            i++;
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
                        root = args[i + 1];
                        i++;
                    }
                    break;
                case "-h":
                    System.out.println(usage);
                    helpFlag = true;
                    break;
                case "-x":
                    String absolutePath = new File(root).getAbsolutePath();
                    System.out.println("Challenge Server");
                    System.out.println("Running on port: " + port);
                    System.out.println("Serving files from: " + absolutePath);
                    blockFlag = true;
                    break;
                default:
                    System.out.println("Option not known: " + args[i]);
                    System.out.println(usage);
                    return false;
            }
        }
        return !helpFlag && !blockFlag;
    }
}
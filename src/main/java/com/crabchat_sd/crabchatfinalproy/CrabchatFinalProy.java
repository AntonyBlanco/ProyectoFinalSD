package com.crabchat_sd.crabchatfinalproy;

import java.util.Scanner;

public class CrabchatFinalProy {

    public static void main(String[] args) {
        
        System.out.println(" === Aplicación CrabChat === \n");
        Scanner scanner = new Scanner(System.in);

        System.out.println(" 1. Instancia de Cliente CrabChat");
        System.out.println(" 2. Instancia de Servidor CrabChat");
        System.out.print("Escoja una opción: ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            ClienteLogin.main(args);
            // Perform operations with the ClienteChat instance
        } else if (choice == 2) {
            ServidorChat.main(args);
            // Perform operations with the ServidorChat instance
        } else {
            System.out.println("Invalid choice. Please try again.");
        }
        scanner.close();
    }
}

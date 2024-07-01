package org.griddynamics.flashcards;

import java.util.Scanner;

public class Main {
   public static void main(String[] args) {

      Scanner sc = new Scanner(System.in);
      FlashcardManagementSystem flashcards = new FlashcardManagementSystem(sc);
      flashcards.run(args);
   }
}

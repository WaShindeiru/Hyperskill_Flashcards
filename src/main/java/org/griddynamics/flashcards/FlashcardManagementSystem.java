package org.griddynamics.flashcards;

import com.beust.jcommander.JCommander;
import com.google.gson.*;
import org.griddynamics.flashcards.args.Args;
import org.griddynamics.flashcards.util.FileAccess;
import org.griddynamics.flashcards.util.SimpleLogger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FlashcardManagementSystem {

   private Scanner scanner;
   private JsonObject cache = new JsonObject();
   private Gson gson = new Gson();
   private FileAccess fileAccess = new FileAccess();

   private boolean isRunning = true;

   private Map<String, Flashcard> cards = new HashMap<>();
   private Map<String, String> cardsInverse = new HashMap<>();

   private int mostErrorCount = 0;
   private List<Flashcard> flashcardErrorList = new ArrayList<>();
   private SimpleLogger logger = new SimpleLogger(fileAccess);
   private Args commandArgs = new Args();

   public FlashcardManagementSystem(Scanner scanner) {
      this.scanner = scanner;
   }

   public void run(String[] args) {
      JCommander parser = JCommander.newBuilder()
            .addObject(commandArgs)
            .build();

      parser.parse(args);

      if (commandArgs.importPath != null)
         importCards(commandArgs.importPath);

      String action;

      while(isRunning) {
         logger.info("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
         action = scanner.nextLine();
         logger.addInput(action);

         switch (action) {
            case "add" -> add();
            case "remove" -> remove();
            case "import" -> import_();
            case "export" -> export();
            case "ask" -> ask();
            case "exit" -> exit();
            case "log" -> log();
            case "hardest card" -> showHardestCard();
            case "reset stats" -> resetStats();
         }
         logger.info("");
      }
   }

   public Map<String, Flashcard> getCards() {
      return cards;
   }

   public void add() {
      logger.info("The card:");
      String term = scanner.nextLine();
      logger.addInput(term);

      if (cards.containsKey(term)) {
         logger.info("The card \"" + term + "\" already exists.");
         return;
      }

      logger.info("The definition of the card:");
      String definition = scanner.nextLine();
      logger.addInput(definition);

      if (cardsInverse.containsKey(definition)) {
         logger.info("The definition \"" + definition + "\" already exists.");
         return;
      }

      cards.put(term, new Flashcard(term, definition));
      cardsInverse.put(definition, term);
      logger.info("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
   }

   public void remove() {
      logger.info("Which card?");
      String term = scanner.nextLine();
      logger.addInput(term);

      if (cards.containsKey(term)) {
         cards.remove(term);
         logger.info("The card has been removed.");
      } else
         logger.info("Can't remove \"" + term + "\": there is no such card.");
   }

   public void import_() {
      logger.info("File name:");
      String fileName = scanner.nextLine();
      logger.addInput(fileName);

      importCards(fileName);
   }

   public void importCards(String fileName) {
      String temp = null;
      try {
         temp = fileAccess.readFromFile(fileName);
      } catch (IOException e) {
         logger.info("File not found.");
         return;
      }

      JsonObject jsonObject = gson.fromJson(temp, JsonObject.class);

      try {
         if (jsonObject.has("cards") && jsonObject.get("cards").isJsonArray()) {
            JsonArray cardsArray = jsonObject.get("cards").getAsJsonArray();
            List<Flashcard> tempArray = new ArrayList<>();
            flashcardErrorList.clear();
            mostErrorCount = 0;

            for (JsonElement element : cardsArray) {
               String term = element.getAsJsonObject().get("term").getAsString();
               String definition = element.getAsJsonObject().get("definition").getAsString();
               int mistakeCount = element.getAsJsonObject().get("mistakeCount").getAsInt();
               Flashcard newFlashcard = new Flashcard(term, definition, mistakeCount);
               cards.put(term, newFlashcard);
               cardsInverse.put(definition, term);
            }

            for (Map.Entry<String, Flashcard> entry : cards.entrySet())
               tempArray.add(entry.getValue());

            Collections.sort(tempArray);
            if (!tempArray.isEmpty()) {
               mostErrorCount = tempArray.get(tempArray.size() - 1).getMistakeCount();

               if (mostErrorCount != 0) {
                  for (int i = tempArray.size() - 1; i >= 0; i--) {
                     Flashcard flashcard = tempArray.get(i);
                     if (flashcard.getMistakeCount() != mostErrorCount)
                        break;

                     flashcardErrorList.add(flashcard);
                  }
               }
            }

            logger.info(cardsArray.size() + " cards have been loaded.");
         } else
            logger.info("0 cards have been loaded.");
      } catch (IllegalStateException e) {
         logger.info("File has incorrect format");
      }
   }

   public void export() {
      logger.info("File name:");
      String fileName = scanner.nextLine();
      logger.addInput(fileName);

      exportCards(fileName);
   }

   public void exportCards(String filename) {
      JsonArray tempArray = new JsonArray();

      for (Map.Entry<String, Flashcard> entry : cards.entrySet()) {
         JsonObject tempObject = new JsonObject();
         tempObject.add("term", new JsonPrimitive(entry.getKey()));
         Flashcard card = cards.get(entry.getKey());
         tempObject.add("definition", new JsonPrimitive(card.getDefinition()));
         tempObject.add("mistakeCount", new JsonPrimitive(card.getMistakeCount()));
         tempArray.add(tempObject);
      }

      cache.add("cards", tempArray);
      String output = gson.toJson(cache);

      try {
         fileAccess.writeToFile(filename, output);
         logger.info(cards.size() + " cards have been saved.");
      } catch (IOException e) {
         System.out.println("Can't access the file");
      }
   }

   public void ask() {
      int numberOfCards = 0;
      String term;
      String definition;
      String userInput;

      logger.info("How many times to ask?");
      numberOfCards = Integer.parseInt(scanner.nextLine());
      logger.addInput(Integer.toString(numberOfCards));

      int i = 0;
      for (Map.Entry<String, Flashcard> entry : cards.entrySet()) {
         if (i >= numberOfCards)
            break;

         term = entry.getKey();
         Flashcard card = entry.getValue();
         definition = card.getDefinition();
         logger.info("Print the definition of \"" + term + "\":");
         userInput = scanner.nextLine();
         logger.addInput(userInput);

         if (definition.equals(userInput))
            logger.info("Correct!");
         else if (cardsInverse.containsKey(userInput)) {
            compareToMostErrorCount(card);
            logger.info("Wrong. The right answer is \"" + definition + "\", but your definiton is correct " +
                  "for \"" + cardsInverse.get(userInput) + "\"");
         } else {
            compareToMostErrorCount(card);
            logger.info("Wrong. The right answer is \"" + definition + "\"");
         }

         i++;
      }
   }

   public void compareToMostErrorCount(Flashcard card) {
      card.addMistake();
      int mistakeCount = card.getMistakeCount();
      if (mistakeCount == this.mostErrorCount) {
         this.flashcardErrorList.add(card);

      } else if (mistakeCount > this.mostErrorCount) {
         this.mostErrorCount = mistakeCount;
         this.flashcardErrorList.clear();
         this.flashcardErrorList.add(card);
      }
   }

   public void showHardestCard() {
      if (mostErrorCount == 0)
         logger.info("There are no cards with errors.");
      else {
         if (flashcardErrorList.size() == 1)
            logger.info("The hardest card is \"" + flashcardErrorList.get(0).getTerm() + "\". You have " +
                  mostErrorCount + " errors answering it.");
         else {
            String output = flashcardErrorList.stream()
                  .map(s -> "\"" + s.getTerm() + "\"")
                  .collect(Collectors.joining(", "));

            logger.info("The hardest cards are " + output + ". You have " + mostErrorCount + " errors answering them.");
         }
      }
   }

   public void resetStats() {
      mostErrorCount = 0;
      for (Flashcard card : cards.values())
         card.resetMistakeCount();

      flashcardErrorList.clear();
      logger.info("Card statistics have been reset.");
   }

   public void log() {
      logger.info("File name:");
      String fileName = scanner.nextLine();
      logger.addInput(fileName);
      try {
         logger.saveLogs(fileName);
         logger.info("The log has been saved.");
      } catch (IOException e) {
         System.out.println("Can't access the file");
      }
   }

   public void exit() {
      if (commandArgs.exportPath != null)
         exportCards(commandArgs.exportPath);

      logger.info("Bye bye!");
      isRunning = false;
   }
}

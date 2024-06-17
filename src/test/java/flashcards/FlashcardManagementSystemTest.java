package flashcards;

import flashcards.utils.FileAccess;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FlashcardManagementSystemTest {
   private static final FileAccess fileAccess = new FileAccess();

   @Test
   public void testAddFlashcardWithUniqueTermAndDefinition() {
      ByteArrayInputStream in = new ByteArrayInputStream("add\nbulbasaur\ngrass\nexit\n".getBytes());
      Scanner scanner = new Scanner(in);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};
      cardSystem.run(args);

      assertTrue(cardSystem.getCards().containsKey("bulbasaur"));
      assertEquals("grass", cardSystem.getCards().get("bulbasaur").getDefinition());
   }

   @Test
   public void testAddFlashcardAndExport() {
      ByteArrayInputStream in = new ByteArrayInputStream("add\nbulbasaur\ngrass\nadd\ncharmander\nfire\nexport\n./src/test/resources/db.json\nexit\n".getBytes());
      Scanner scanner = new Scanner(in);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};
      cardSystem.run(args);

      assertTrue(cardSystem.getCards().containsKey("bulbasaur"));

      in = new ByteArrayInputStream("import\n./src/test/resources/db.json\nexit\n".getBytes());
      scanner = new Scanner(in);
      cardSystem = new FlashcardManagementSystem(scanner);
      cardSystem.run(args);

      assertTrue(cardSystem.getCards().containsKey("bulbasaur"));
      assertEquals("grass", cardSystem.getCards().get("bulbasaur").getDefinition());
      assertTrue(cardSystem.getCards().containsKey("charmander"));
      assertEquals("fire", cardSystem.getCards().get("charmander").getDefinition());
   }

   @Test
   public void testAddExistingTermFlashcard() {
      Scanner scanner = new Scanner("bulbasaur\nfire\nbulbasaur\ngrass\n");
      FlashcardManagementSystem system = new FlashcardManagementSystem(scanner);

      system.add();
      system.add();

      Map<String, Flashcard> cards = system.getCards();
      assertEquals(1, cards.size());
      assertEquals("fire", cards.get("bulbasaur").getDefinition());
   }

   @Test
   public void testAddThenRemove() {
      Scanner scanner = new Scanner("bulbasaur\ngrass\ncharmander\nfire\ncharmander\n");
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);

      cardSystem.add();
      cardSystem.add();
      cardSystem.remove();

      Map<String, Flashcard> cards = cardSystem.getCards();
      assertEquals(1, cards.size());
      assertEquals("grass", cards.get("bulbasaur").getDefinition());
   }

   @Test
   public void testLog() {
      String inputString  = "add\nbulbasaur\ngrass\nlog\n./src/test/resources/test.txt\nexit";
      Scanner scanner = new Scanner(inputString);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};

      cardSystem.run(args);

      String logFile = null;
      try {
         logFile = fileAccess.readFromFile("./src/test/resources/test.txt").trim();
      } catch (IOException e) {
         fail(e.getMessage());
      }

      String correctResult = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "add\n" +
            "The card:\n" +
            "bulbasaur\n" +
            "The definition of the card:\n" +
            "grass\n" +
            "The pair (\"bulbasaur\":\"grass\") has been added.\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "log\n" +
            "File name:\n" +
            "./src/test/resources/test.txt";
      assertNotNull(logFile);
      assertThat(logFile).isEqualTo(correctResult);
   }

   @Test
   public void testAsk() {
      String inputString  = "add\nbulbasaur\ngrass\nask\n1\ngrass\nlog\n./src/test/resources/test.txt\nexit";
      Scanner scanner = new Scanner(inputString);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};

      cardSystem.run(args);

      String logFile = null;
      try {
         logFile = fileAccess.readFromFile("./src/test/resources/test.txt").trim();
      } catch (IOException e) {
         fail(e.getMessage());
      }

      String correctResult = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "add\n" +
            "The card:\n" +
            "bulbasaur\n" +
            "The definition of the card:\n" +
            "grass\n" +
            "The pair (\"bulbasaur\":\"grass\") has been added.\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "ask\n" +
            "How many times to ask?\n" +
            "1\n" +
            "Print the definition of \"bulbasaur\":\n" +
            "grass\n" +
            "Correct!\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "log\n" +
            "File name:\n" +
            "./src/test/resources/test.txt";
      assertNotNull(logFile);
      assertThat(logFile).isEqualTo(correctResult);
   }

   @Test
   public void testHardestCard() {
      String inputString = "add\nbulbasaur\ngrass\nask\n2\nwrong\ngrass\nhardest card\nlog\n./src/test/resources/test.txt\nexit";
      Scanner scanner = new Scanner(inputString);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};

      cardSystem.run(args);

      String logFile = null;
      try {
         logFile = fileAccess.readFromFile("./src/test/resources/test.txt").trim();
      } catch (IOException e) {
         fail(e.getMessage());
      }

      String correctResult = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "add\n" +
            "The card:\n" +
            "bulbasaur\n" +
            "The definition of the card:\n" +
            "grass\n" +
            "The pair (\"bulbasaur\":\"grass\") has been added.\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "ask\n" +
            "How many times to ask?\n" +
            "2\n" +
            "Print the definition of \"bulbasaur\":\n" +
            "wrong\n" +
            "Wrong. The right answer is \"grass\"\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "grass\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "hardest card\n" +
            "The hardest card is \"bulbasaur\". You have 1 errors answering it.\n" +
            "\n" +
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n" +
            "log\n" +
            "File name:\n" +
            "./src/test/resources/test.txt";
      assertNotNull(logFile);
      assertThat(logFile).isEqualTo(correctResult);
   }

   @Test
   public void testWrongAnswer() {
      String inputString = "add\nbulbasaur\ngrass\nask\n1\nwrong\nexit";
      Scanner scanner = new Scanner(inputString);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};

      cardSystem.run(args);

      Map<String, Flashcard> cards = cardSystem.getCards();

      assertThat(cards.containsKey("bulbasaur")).isTrue();
      assertThat(cards.get("bulbasaur").getMistakeCount()).isEqualTo(1);
   }

   @Test
   public void testReset() {
      String inputString = "add\nbulbasaur\ngrass\nask\n1\nwrong\nreset stats\nexit";
      Scanner scanner = new Scanner(inputString);
      FlashcardManagementSystem cardSystem = new FlashcardManagementSystem(scanner);
      String[] args = {};

      cardSystem.run(args);

      Map<String, Flashcard> cards = cardSystem.getCards();

      assertThat(cards.containsKey("bulbasaur")).isTrue();
      assertThat(cards.get("bulbasaur").getMistakeCount()).isEqualTo(0);
   }
}
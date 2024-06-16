package flashcards.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

class SimpleLoggerTest {

   private FileAccess fileAccess = new FileAccess();
   private SimpleLogger logger = new SimpleLogger(fileAccess);

   @Test
   void addInput() {
      String testPath = "./src/test/resources/test.txt";
      String inputString1 = "test1";
      String inputString2 = "test2";
      String correctResult = "test1\ntest2\n";
      logger.addInput(inputString1);
      logger.addInput(inputString2);

      try {
         logger.saveLogs(testPath);
      } catch (IOException swallow) {}

      String result = null;
      try {
         result = fileAccess.readFromFile(testPath);
      } catch (IOException e) {
         fail(e.getMessage());
      }

      assertThat(result).isEqualTo(correctResult);
   }
}
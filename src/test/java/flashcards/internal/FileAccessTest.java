package flashcards.internal;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class FileAccessTest {

   private static String filePath = "./src/test/resources/test.txt";
   private static FileAccess fileAccess = new FileAccess();

   @Test
   void writeAndReadFromFile() {
      String message = "test";
      try {
         fileAccess.writeToFile(filePath, message);
      } catch (IOException e) {
         fail(e.getMessage());
      }

      String result = null;
      try {
         result = fileAccess.readFromFile(filePath).trim();
      } catch (IOException e) {
         fail(e.getMessage());
      }

      assertNotNull(result);
      assertThat(result).isEqualTo(message);
   }
}

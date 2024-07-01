package org.griddynamics.flashcards.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleLogger {

   private List<String> messages = new ArrayList<>();
   private FileAccess fileAccess;

   public SimpleLogger(FileAccess fileAccess) {
      this.fileAccess = fileAccess;
   }

   public void info(String message) {
      messages.add(message);
      System.out.println(message);
   }

   public void addInput(String message) {
      messages.add(message);
   }

   public void saveLogs(String filePath) throws IOException {
      fileAccess.writeToFile(filePath, messages.stream().collect(Collectors.joining("\n")));
   }
}

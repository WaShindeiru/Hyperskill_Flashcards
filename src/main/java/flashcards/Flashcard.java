package flashcards;

public class Flashcard implements Comparable<Flashcard> {

   private String term;
   private String definition;
   private int mistakeCount;

   public Flashcard(String term, String definition, int mistakeCount) {
      this.term = term;
      this.definition = definition;
      this.mistakeCount = mistakeCount;
   }

   public Flashcard(String term, String definition) {
      this(term, definition, 0);
   }

   public String getTerm() {
      return term;
   }

   public String getDefinition() {
      return definition;
   }

   public int getMistakeCount() {
      return mistakeCount;
   }

   public void addMistake() {
      mistakeCount++;
   }

   public void resetMistakeCount() {
      mistakeCount = 0;
   }

   @Override
   public int compareTo(Flashcard flashcard) {
      return this.mistakeCount - flashcard.mistakeCount;
   }
}

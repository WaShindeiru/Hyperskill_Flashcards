package flashcards.args;

import com.beust.jcommander.Parameter;

public class Args {

   @Parameter(names = "-import")
   public String importPath;

   @Parameter(names = "-export")
   public String exportPath;
}

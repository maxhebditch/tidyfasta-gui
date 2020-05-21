import com.proteinsol.tidyfasta.gui.TidyFASTAGUI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TidyFASTAGUITest {

    @Test
    void assembleLinesTest(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        List<String> testInput = Arrays.asList(new String[]{">SEQ1", "AAAAAAAAA","AAAAAAAAAAA"});
        String output = GUI.assembleLines(testInput);

        Assertions.assertEquals( ">SEQ1\nAAAAAAAAA\nAAAAAAAAAAA" ,output);

    }
}

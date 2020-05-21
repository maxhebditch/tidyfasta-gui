import com.proteinsol.tidyfasta.gui.TidyFASTAGUI;
import com.proteinsol.tidyfasta.packages.ReadFASTAAndFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SuppressWarnings("SpellCheckingInspection")
public class TidyFASTAGUITest {

    @Test
    void handleSubmission(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        String testInput = ">SEQ1\nAAAAAAAAA\nAAAAAAAAAAA";
        GUI.handleSubmission(testInput);
        String testOutput = GUI.getValidOutput();

        Assertions.assertEquals( ">SEQ1\nAAAAAAAAAAAAAAAAAAAA" ,testOutput);

    }

    @Test
    void handleSubmissionShort(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        GUI.submitSequence("AAAAAAAAAA");
        String testOutput = GUI.getValidOutput();

        Assertions.assertEquals( ">Sequence-0\nAAAAAAAAAA" ,testOutput);

    }


    @Test
    void testOpenFile(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        File inputFile = new File("src/test/resources/opentest.fasta");
        GUI.openFile(inputFile);
        String output = GUI.getSubmittedFASTA();
        Assertions.assertEquals(">OPENTEST\nAAAAAAAAA",output);

    }

    @Test
    void testWriteFile(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        File inputFile = new File("src/test/resources/savetest.fasta");

        try {
            Files.deleteIfExists(inputFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GUI.writeFile(inputFile);

        Assertions.assertTrue(inputFile.exists());

        try {
            Files.deleteIfExists(inputFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testWriteFileNoSuffix(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        File inputCorrectFile = new File("src/test/resources/savetest.fasta");

        try {
            Files.deleteIfExists(inputCorrectFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        File inputFile = new File("src/test/resources/savetest");

        GUI.writeFile(inputFile);

        Assertions.assertTrue(inputCorrectFile.exists());

        try {
            Files.deleteIfExists(inputCorrectFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testOpenAndSubmitFile(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        File inputFile = new File("src/test/resources/opentest.fasta");
        GUI.openFile(inputFile);
        GUI.submitSequence(GUI.getSubmittedFASTA());
        String output = GUI.getValidOutput();
        Assertions.assertEquals(">OPENTEST\nAAAAAAAAA",output);

    }

    @Test
    void testSingleError(){
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(">SEQ\nAA");

        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");
        GUI.buildErrorMessage(Assignment);

        Assertions.assertEquals("The following error occurred\n" +
                "Submitted sequence >SEQ had 2 amino acids, which is shorter than the limit of 3 amino acids.\n",
                GUI.getErrorMessages());
    }

    @Test
    void testSingleErrorSingleValid(){
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(">GOOD\nAAAAA\n>BAD\nAA");

        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");
        GUI.buildErrorMessage(Assignment);

        Assertions.assertEquals("1/2 of the sequences submitted contained errors.\n\n" +
                        "The following error occurred\nSubmitted sequence >BAD had 2 amino acids, " +
                        "which is shorter than the limit of 3 amino acids.\n",
                GUI.getErrorMessages());
    }

    @Test
    void testLessTenErrors(){
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(">SEQ\nAA\n>SEQ2\nAXAAA");

        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");
        GUI.buildErrorMessage(Assignment);

        Assertions.assertEquals("The following errors occurred\n" +
                        "Submitted sequence >SEQ had 2 amino acids, which is shorter than the limit of 3 amino acids.\n" +
                        "Non Canonical amino acid found in >SEQ2.\n",
                GUI.getErrorMessages());
    }

    @Test
    void testMoreThanTenErrors(){
        String testInput = ">Seq1\nAA\n>Seq2\nAA\n>Seq3\nAA\n>Seq4\nAA\n>Seq5\nAA\n" +
                ">Seq6\nAA\n>Seq7\nAA\n>Seq8\nAA\n>Seq9\nAA\n>Seq10\nAA\n>Seq11\nAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(testInput);

        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");
        GUI.buildErrorMessage(Assignment);

        String output =  "All 11 sequences contained errors, the first 10 errors were:\n\n"+
                "Submitted sequence >Seq1 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq2 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq3 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq4 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq5 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq6 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq7 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq8 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq9 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq10 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n\n";

        Assertions.assertEquals(output, GUI.getErrorMessages());
    }

    @Test
    void testMoreThanTenErrorsOneValid(){
        String testInput = ">Seq0\nAAAA\n>Seq1\nAA\n>Seq2\nAA\n>Seq3\nAA\n>Seq4\nAA\n>Seq5\nAA\n" +
                ">Seq6\nAA\n>Seq7\nAA\n>Seq8\nAA\n>Seq9\nAA\n>Seq10\nAA\n>Seq11\nAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(testInput);

        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");
        GUI.buildErrorMessage(Assignment);

        String output =  "11/12 of the sequences submitted contained errors.\n\nThe first 10 errors were:\n\n"+
                "Submitted sequence >Seq1 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq2 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq3 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq4 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq5 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq6 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq7 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq8 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq9 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n"+
                "Submitted sequence >Seq10 had 2 amino acids, which is shorter than the limit of 3 amino acids.\n\n";

        Assertions.assertEquals(output, GUI.getErrorMessages());
    }

    @Test
    void clipboardCopy(){
        TidyFASTAGUI GUI = new TidyFASTAGUI("TEST");

        String testInput = ">SEQ1\nAAAAAAAAA\nAAAAAAAAAAA";
        GUI.handleSubmission(testInput);
        GUI.copyValidFASTAToClipboard();

        String testOutput = null;

        try {
            testOutput = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertEquals( ">SEQ1\nAAAAAAAAAAAAAAAAAAAA",testOutput);

    }

}

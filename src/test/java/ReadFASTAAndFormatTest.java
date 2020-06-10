import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTANoSequence;
import com.proteinsol.tidyfasta.packages.FASTAObject;
import com.proteinsol.tidyfasta.packages.ReadFASTAAndFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class ReadFASTAAndFormatTest {

    @Test
    void simpleFormat(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID = ">SEQ1";
        String expectedSequence = "AAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID = ObjArray.get(0).id;
        String returnedSequence = ObjArray.get(0).sequence;

        Assertions.assertEquals(expectedID,returnedID);
        Assertions.assertEquals(expectedSequence,returnedSequence);
    }

    @Test
    void multipleFormat(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n>SEQ2\nTAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID2 = ">SEQ2";
        String expectedSequence2 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID2 = ObjArray.get(1).id;
        String returnedSequence2 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
    }

    @Test
    void multipleFormatIdentical(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n" +
                ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n" +
                ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID2 = ">SEQ1";
        String expectedSequence2 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID3 = ">SEQ1";
        String expectedSequence3 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID2 = ObjArray.get(1).id;
        String returnedSequence2 = ObjArray.get(1).sequence;
        String returnedID3 = ObjArray.get(2).id;
        String returnedSequence3 = ObjArray.get(2).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);
    }

    @Test
    void AABrokenOverMultipleLinesFix(){
        String input = ">SEQ1\nAAAAAAA\nTAAAAAAAAAAAAAAAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAATAAAAAAAAAAAAAAAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
    }

    @Test
    void multipleFormatConsecutive(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAC\n" +
                ">SEQ2\nDAAAAAAAAAAAAAAAAATAAAAAAAE\n" +
                ">SEQ3\nFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAC";
        String expectedID2 = ">SEQ2";
        String expectedSequence2 = "DAAAAAAAAAAAAAAAAATAAAAAAAE";
        String expectedID3 = ">SEQ3";
        String expectedSequence3 = "FFFFFFFFFFFFFFFFFFFFFFFFFFF";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();

        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID2 = ObjArray.get(1).id;
        String returnedSequence2 = ObjArray.get(1).sequence;
        String returnedID3 = ObjArray.get(2).id;
        String returnedSequence3 = ObjArray.get(2).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);
    }

    @Test
    void testValidCount() throws ExceptionsFASTABadAA, ExceptionsFASTALength, ExceptionsFASTANoSequence {
        String input = ">SEQ1\nAAAAAAAT\nAAAAAAAAAAAAC\n\n>Seq2\nDAAAAAAAAAAAAAAAAAAAAAAE"+
                "\n\n\n>Seq\nFAA\nAAAAT\nAAXAAAAAAAAAAAG\n\n>seq1\nHAAA\nAAAAATAAAAAAAAAAAI";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(3,Assignment.getValidatedNumber());
    }

    @Test
    void testSubmittedCount() throws ExceptionsFASTABadAA, ExceptionsFASTALength, ExceptionsFASTANoSequence {
        String input = ">SEQ1\nAAAAAAAT\nAAAAAAAAAAAAC\n\n>Seq2\nDAAAAAAAAAAAAAAAAAAAAAAE"+
                "\n\n\n>Seq\nFAA\nAAAAT\nAAAAAXAAAAAAAAG\n\n>seq1\nHAAA\nAAAAATAAAAAAAAAAAI";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(4,Assignment.getSubmittedNumber());
    }

    @Test
    void testErrorCount() throws ExceptionsFASTABadAA, ExceptionsFASTALength, ExceptionsFASTANoSequence {
        String input = ">SEQ1\nAAAAAAAT\nAAAAAAAAAAAAC\n\n>Seq2\nDAAAAAAAAAAAAAAAAAAAAAAE"+
                "\n\n\n>Seq\nFAA\nAAAAT\nAAAAAXAAAAAAAAG\n\n>seq1\nHAAA\nAAAAATAAAAAAAAAAAI";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(1,Assignment.getNumErrors());
    }

    @Test
    void testErrorArray() throws ExceptionsFASTABadAA, ExceptionsFASTALength, ExceptionsFASTANoSequence {
        String input = ">SEQ1\nAAAAAAAT\nAAAAAAAAAAAAC\n\n>Seq2\nDAAAAAAAAAAAAAAAAAAAAAAE"+
                "\n\n\n>Seq\nFAA\nAAAAT\nAAAAAXAAAAAAAAG\n\n>seq1\nHAAA\nAAAAATAAAAAAAAAAAI";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);
        String[] ErrArray = Assignment.getErrMsgArray();

        Assertions.assertEquals(1,ErrArray.length);
        Assertions.assertEquals("Non Canonical amino acid found in >Seq.",ErrArray[0]);
    }

    @Test
    void missingIDOnlySymbolFix() {
        String input = ">\nAAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID = ">Sequence-0";
        String expectedSequence = "AAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID = ObjArray.get(0).id;
        String returnedSequence = ObjArray.get(0).sequence;

        Assertions.assertEquals(expectedID, returnedID);
        Assertions.assertEquals(expectedSequence, returnedSequence);
    }

    @Test
    void missingIDFix(){
        String input = "MAAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID = ">Sequence-0";
        String expectedSequence = "MAAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID = ObjArray.get(0).id;
        String returnedSequence = ObjArray.get(0).sequence;

        Assertions.assertEquals(expectedID,returnedID);
        Assertions.assertEquals(expectedSequence,returnedSequence);
    }

    @Test
    void missingIDFixMulti(){
        String input = "MAAAAAAAAAAAA\nAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID = ">Sequence-0";
        String expectedSequence = "MAAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID = ObjArray.get(0).id;
        String returnedSequence = ObjArray.get(0).sequence;

        Assertions.assertEquals(expectedID,returnedID);
        Assertions.assertEquals(expectedSequence,returnedSequence);
    }


    @Test
    void excessWhiteSpaceFix(){
        String input = "   >SEQ1    \n   AAAAAAAT\t    \nAAAAAAAAAAAAA       \n \t \n\n\n  \n\n>Seq2 " +
                "   \nAAAAAAAAAAAAAAAAAAAAAAAA\n"+
                "\n     \n\t\n\nAAA\nAAAAT\nAAAAAAAAAAAAAA\n     " +
                "   \n \nAAA\n   \t AAAAAATAAAAAAAAAAAA         \n\n\n\n";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID0 = ">SEQ1";
        String expectedSequence0 = "AAAAAAATAAAAAAAAAAAAA";
        String expectedID1 = ">Seq2";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAAAAAAAA";
        String expectedID2 = ">Sequence-0";
        String expectedSequence2 = "AAAAAAATAAAAAAAAAAAAAA";
        String expectedID3 = ">Sequence-1";
        String expectedSequence3 = "AAAAAAAAATAAAAAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID0 = ObjArray.get(0).id;
        String returnedSequence0 = ObjArray.get(0).sequence;
        String returnedID1 = ObjArray.get(1).id;
        String returnedSequence1 = ObjArray.get(1).sequence;
        String returnedID2 = ObjArray.get(2).id;
        String returnedSequence2 = ObjArray.get(2).sequence;
        String returnedID3 = ObjArray.get(3).id;
        String returnedSequence3 = ObjArray.get(3).sequence;

        Assertions.assertEquals(expectedID0,returnedID0);
        Assertions.assertEquals(expectedSequence0,returnedSequence0);
        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);
    }

    @Test
    void LeadingWhitespaceSeqFix() {
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n>SEQ2\n TAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID3 = ">SEQ2";
        String expectedSequence3 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID3 = ObjArray.get(1).id;
        String returnedSequence3 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);

    }

    @Test
    void LeadingWhitespaceBeforeIDFix(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n >SEQ2\nTAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID3 = ">SEQ2";
        String expectedSequence3 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID3 = ObjArray.get(1).id;
        String returnedSequence3 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);

    }

    @Test
    void LeadingWhitespaceAfterIDFix(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n>SEQ2        \nTAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID3 = ">SEQ2";
        String expectedSequence3 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID3 = ObjArray.get(1).id;
        String returnedSequence3 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);

    }

    @Test
    void noBlankLineBetweenIDFix(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n>SEQ2\nTAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID3 = ">SEQ2";
        String expectedSequence3 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID3 = ObjArray.get(1).id;
        String returnedSequence3 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);

    }

    @Test
    void multipleIssuesFix(){
        String input = " \n>SEQ1    \n   AAAAAAAT\t    \nAAAAAAAAAAAAA       \n \t \n\n\n  \n\n>Seq2 " +
                "   \nAAAAAAAAAAAAAAAAAAAAAAAA\n"+
                "       \t        >Seq3\nAAA\nAAAAT\n   \t    AAAAAAAAAAAAAA\n     " +
                "   \n \nAAA\n   \t AAAAAATAAAAAAAAAAAA         \n\n\n\n";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID0 = ">SEQ1";
        String expectedSequence0 = "AAAAAAATAAAAAAAAAAAAA";
        String expectedID1 = ">Seq2";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAAAAAAAA";
        String expectedID2 = ">Seq3";
        String expectedSequence2 = "AAAAAAATAAAAAAAAAAAAAA";
        String expectedID3 = ">Sequence-0";
        String expectedSequence3 = "AAAAAAAAATAAAAAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID0 = ObjArray.get(0).id;
        String returnedSequence0 = ObjArray.get(0).sequence;
        String returnedID1 = ObjArray.get(1).id;
        String returnedSequence1 = ObjArray.get(1).sequence;
        String returnedID2 = ObjArray.get(2).id;
        String returnedSequence2 = ObjArray.get(2).sequence;
        String returnedID3 = ObjArray.get(3).id;
        String returnedSequence3 = ObjArray.get(3).sequence;

        Assertions.assertEquals(expectedID0,returnedID0);
        Assertions.assertEquals(expectedSequence0,returnedSequence0);
        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);
    }

    @Test
    void stragglerSequenceFix(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n\n\n\n\nTAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID2 = ">Sequence-0";
        String expectedSequence2 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID2 = ObjArray.get(1).id;
        String returnedSequence2 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
    }

    @Test
    void stragglerSequenceFixIdentical(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n\n\n\n\nCAAAAAAAAAAAAAAAAATAAAAAAAC" +
                "\n\n\n\n\n\nTAAAAAAAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID2 = ">Sequence-0";
        String expectedSequence2 = "CAAAAAAAAAAAAAAAAATAAAAAAAC";
        String expectedID3 = ">Sequence-1";
        String expectedSequence3 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID2 = ObjArray.get(1).id;
        String returnedSequence2 = ObjArray.get(1).sequence;
        String returnedID3 = ObjArray.get(2).id;
        String returnedSequence3 = ObjArray.get(2).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
        Assertions.assertEquals(expectedID3,returnedID3);
        Assertions.assertEquals(expectedSequence3,returnedSequence3);
    }

    @Test
    void stragglerSequenceMultiLineFix(){
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAATAAAAAAAA\n\n\n\n\n\nTAAAAAA\nAAAAAAAAAAATAAAAAAAA";
        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedID1 = ">SEQ1";
        String expectedSequence1 = "AAAAAAAAAAAAAAAAAATAAAAAAAA";
        String expectedID2 = ">Sequence-0";
        String expectedSequence2 = "TAAAAAAAAAAAAAAAAATAAAAAAAA";

        List<FASTAObject> ObjArray = Assignment.getArrayFASTA();
        String returnedID1 = ObjArray.get(0).id;
        String returnedSequence1 = ObjArray.get(0).sequence;
        String returnedID2 = ObjArray.get(1).id;
        String returnedSequence2 = ObjArray.get(1).sequence;

        Assertions.assertEquals(expectedID1,returnedID1);
        Assertions.assertEquals(expectedSequence1,returnedSequence1);
        Assertions.assertEquals(expectedID2,returnedID2);
        Assertions.assertEquals(expectedSequence2,returnedSequence2);
    }

    @Test
    void checkErrorMessageTooShort() {
        String input = ">SEQ1\nAA";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(
                "Submitted sequence >SEQ1 had 2 amino acids, which is shorter than the limit of 3 amino acids.",
                Assignment.getErrMsg());
    }

    @Test
    void checkErrorMessageTooLong() {
        String ID = ">SEQ1\n";
        String AA = "A".repeat(100000);

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(ID+AA);

        Assertions.assertEquals(
                "Submitted sequence >SEQ1 had 100000 amino acids, " +
                        "which is longer than the limit of 50000 amino acids.",
                Assignment.getErrMsg());
    }

    @Test
    void checkErrorMessageBadAA() {
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAA";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(
                "Non Canonical amino acid found in >SEQ1.",
                Assignment.getErrMsg());
    }

    @Test
    void checkErrorMessageIDonly() {
        String input = ">SEQ1\n";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(
                "Submitted sequence >SEQ1 had no associated sequence.",
                Assignment.getErrMsg());
    }

    @Test
    void checkErrorMessageIDonlyLastLine() {
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n>SEQ2";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(
                "Submitted sequence >SEQ2 had no associated sequence.",
                Assignment.getErrMsg());
    }

    @Test
    void checkErrorMessageStragglerIDonlyLastLine() {
        String input = ">SEQ1\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\t\n>SEQ2";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        Assertions.assertEquals(
                "Submitted sequence >SEQ2 had no associated sequence.",
                Assignment.getErrMsg());
    }

    @Test
    void checkErrorMessageMultipleIssues() {
        String ID = ">Seq1\n";
        String AA = "A".repeat(100000);
        String TooLong = ID+AA;

        String badChar = "\n\n>Seq2\nAAAAAAAAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAA";

        String input = TooLong+badChar+"\n\n>Seq3";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);
        String expectedErrmsg = "Submitted sequence >Seq1 had 100000 amino acids," +
                " which is longer than the limit of 50000 amino acids.\n" +
                "Non Canonical amino acid found in >Seq2.\n" +
                "Submitted sequence >Seq3 had no associated sequence.";

        Assertions.assertEquals( expectedErrmsg, Assignment.getErrMsg());
    }

    @Test
    void checkErrorLastItem(){
        String input = ">TEST1\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n" +
                ">TEST2\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n" +
                ">BAD\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAAAAAAAAA";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedErrmsg = "Non Canonical amino acid found in >BAD.";

        Assertions.assertEquals( expectedErrmsg, Assignment.getErrMsg());

    }

    @Test
    void checkErrorIDinSequence(){
        String input = ">TEST1\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA>TEST2\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA>BAD\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAAAAAAAAA";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedErrmsg = "Non Canonical amino acid found in >TEST1.";

        Assertions.assertEquals( expectedErrmsg, Assignment.getErrMsg());

    }

    @Test
    void checkErrorLeadingSpaceIgnore(){
        String input = " \n>TEST1\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n  " +
                "\nAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAAAAAAAAA";

        ReadFASTAAndFormat Assignment = new ReadFASTAAndFormat(input);

        String expectedErrmsg = "Non Canonical amino acid found in >TEST1.\n"+
                "Non Canonical amino acid found in >Sequence-0.";

        Assertions.assertEquals( expectedErrmsg, Assignment.getErrMsg());
    }

}

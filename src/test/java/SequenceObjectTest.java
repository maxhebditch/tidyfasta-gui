import com.proteinsol.tidyfasta.exceptions.exceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTANoSequence;
import com.proteinsol.tidyfasta.packages.FASTAObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("SpellCheckingInspection")
public class SequenceObjectTest {

    @Test
    void setSequence(){
        FASTAObject FASTA = new FASTAObject("AAAAAAAAAAAAAAAAAAAAAAAAA");
        assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAA" ,FASTA.Sequence);
    }

    @Test
    void setIDAndSequence(){
        FASTAObject FASTA = new FASTAObject(">Seq1","AAAAAAAAAAAAAAAAAAAAA");
        assertEquals(">Seq1",FASTA.ID);
        assertEquals("AAAAAAAAAAAAAAAAAAAAA",FASTA.Sequence);
    }

    @Test
    void correctLowercaseInput(){
        FASTAObject FASTA = new FASTAObject("aaaaaaaaaaaaaaaaaaaaa");
        assertEquals("AAAAAAAAAAAAAAAAAAAAA",FASTA.Sequence);
    }

    @Test
    void getNAA(){
        FASTAObject FASTA = new FASTAObject("AAAAAAAAAAAAAAAAAAAAA");
        assertEquals(21, FASTA.getNAA());
    }

    @Test
    void throwExceptionSequenceTooShort(){

        Exception exception = assertThrows(exceptionsFASTALength.class, () -> {
            FASTAObject FASTA = new FASTAObject("AA");
        });

        String expectedErrorMsg = "Submitted sequence >Protein-Sol-Sequence had 2 amino acids, " +
                "which is shorter than the limit of 3 amino acids.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMsg,actualMessage);
    }

    @Test
    void throwExceptionSequenceTooLong() {
        String AA = "A";
        String sequence = AA.repeat(100000);

        Exception exception = assertThrows(exceptionsFASTALength.class, () -> {
            FASTAObject FASTA = new FASTAObject(sequence);
        });

        String expectedErrorMsg = "Submitted sequence >Protein-Sol-Sequence"
                + " had 100000 amino acids, which is longer than the limit of 50000 amino acids.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMsg,actualMessage);
    }

    @Test
    void throwExceptionIDOnly(){
        String input = ">SEQ1";

        Exception exception = assertThrows(exceptionsFASTANoSequence.class, () -> {
            FASTAObject FASTA = new FASTAObject(input);
        });

        String expectedErrorMsg = "Submitted sequence >SEQ1 had no associated sequence.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMsg,actualMessage);

    }

    @Test
    void throwExceptionBadAA() {
        String ID = ">SEQ1";
        String sequence = "AAAAAAAAAAAAAAAAAAAAAAXAAAAAAAAAAAAAAAAAAAAAAAAa";

        Exception exception = assertThrows(exceptionsFASTABadAA.class, () -> {
            FASTAObject FASTA = new FASTAObject(ID, sequence);
        });

        String expectedErrorMsg = "Non Canonical amino acid found in >SEQ1.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedErrorMsg,actualMessage);
    }

    @Test
    void throwExceptionBadAASpaceMid() {
        String ID = ">SEQ1";
        String spaceSequence = "AAAAAAAAAAAAAAAAAAAAAA AAAAAAAAAAAAAAAAAAAAAAAA";

        Exception exception = assertThrows(exceptionsFASTABadAA.class, () -> {
            FASTAObject FASTA = new FASTAObject(ID, spaceSequence);
        });

        String expectedErrorMsg = "Non Canonical amino acid found in >SEQ1.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMsg, actualMessage);
    }

    @Test
    void throwExceptionBadAASpaceLeading() {
        String ID = ">SEQ1";
        String SpaceSequence = " TAAAAAAAAAAAAAAAAATAAAAAAAA";

        Exception exception = assertThrows(exceptionsFASTABadAA.class, () -> {
            FASTAObject FASTA = new FASTAObject(ID, SpaceSequence);
        });

        String expectedErrorMsg = "Non Canonical amino acid found in >SEQ1.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMsg, actualMessage);
    }

    @Test
    void throwExceptionBadAANumber() {
        String ID = ">SEQ1";
        String NumberSequence = "AAAAAAAAAAAAAAAAAAAAAA1AAAAAAAAAAAAAAAAAAAAAAAA";

        Exception exception = assertThrows(exceptionsFASTABadAA.class, () -> {
            FASTAObject FASTA = new FASTAObject(ID, NumberSequence);
        });

        String expectedErrorMsg = "Non Canonical amino acid found in >SEQ1.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedErrorMsg, actualMessage);
    }
}

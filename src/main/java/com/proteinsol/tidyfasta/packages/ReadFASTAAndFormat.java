package com.proteinsol.tidyfasta.packages;

//TODO logs

import com.proteinsol.tidyfasta.exceptions.exceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTANoSequence;

import java.util.*;

public class ReadFASTAAndFormat {
    public int getValidatedNumber(){ return ArrayFASTA.size(); }
    public int getSubmittedNumber(){ return ArrayFASTA.size()+numErrors; }
    public int getNumErrors(){ return numErrors; }
    public ArrayList<FASTAObject> getArrayFASTA(){ return ArrayFASTA; }
    public String getErrMsg(){ return String.join("\n", errorMessages); }
    public String[] getErrMsgArray(){ return errorMessages.toArray(new String[0]); }

    private String submittedFASTA;
    private final ArrayList<FASTAObject> ArrayFASTA = new ArrayList<>();
    private int AutomaticNameCount = 0;
    private int numErrors = 0;
    private final Set<String> errorMessages = new LinkedHashSet<>();

    public ReadFASTAAndFormat(String submittedFASTA){
        this.submittedFASTA = submittedFASTA;
        assignIDAndSequence();
    }

    private void assignIDAndSequence() {

        //Variables to hold sequence as String and a StringBuilder to hold
        String ID = null;
        StringBuilder SequenceCollector = new StringBuilder();

        //hack to add a buffer to the of the submission to prevent ugly
        //code in checking the last item
        submittedFASTA = submittedFASTA + "\n\t\n";

        //Make array
        String[] SplitInputBlankLines = submittedFASTA.split("\\r?\\n");

        //number comparisons to walk through the set
        int idx = 0;
        final int endNum = SplitInputBlankLines.length-1;

        // Walk through algorithm
        while (idx < endNum) {
            //looks like ID
            if (SplitInputBlankLines[idx].trim().startsWith(">")) {
                ID = SplitInputBlankLines[idx].trim();
                idx++;
            }

            //looks like Sequence
            if (!SplitInputBlankLines[idx].trim().startsWith(">") && SplitInputBlankLines[idx].trim().length() > 0) {

                //Walk through whilst consecutive lines still look like sequences
                //refactor candidate
                while (idx < endNum &&
                        !SplitInputBlankLines[idx].trim().startsWith(">") &&
                        SplitInputBlankLines[idx].trim().length() > 0) {

                    //add to builder and increment
                    SequenceCollector.append(SplitInputBlankLines[idx].trim());
                    idx++;
                }
            }

            //identify blank lines
            if (SplitInputBlankLines[idx].trim().length() == 0) {
                //walk through fasta lines
                while (idx < endNum && SplitInputBlankLines[idx].trim().length() == 0) {
                    idx++;
                }
            }

            //now finished walking through, try and build FASTA object
            //if the sequence is missing just create error message
            if (SequenceCollector.length() == 0) {
                if (ID != null ) {
                    errorMessages.add("Submitted sequence " + ID + " had no associated sequence.");
                }
            } else {

                //generate ID name if missing
                if (ID == null || ID.length() == 1) { ID = ">Sequence-" + AutomaticNameCount++; }

                //try and build object
                try {
                    ArrayFASTA.add(new FASTAObject(ID, SequenceCollector.toString()));
                } catch (exceptionsFASTALength | exceptionsFASTABadAA | exceptionsFASTANoSequence err) {
                    numErrors++;
                    errorMessages.add(err.getMessage());
                }
            }

            //empty variables
            SequenceCollector.setLength(0);
            ID = null;
        }

    }
}

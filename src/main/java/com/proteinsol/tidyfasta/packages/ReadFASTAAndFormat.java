package com.proteinsol.tidyfasta.packages;

//TODO logs

import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTANoSequence;

import java.util.*;

public class ReadFASTAAndFormat {
    public int getValidatedNumber(){ return arrayFASTA.size(); }
    public int getSubmittedNumber(){ return arrayFASTA.size()+numErrors; }
    public int getNumErrors(){ return numErrors; }
    public List<FASTAObject> getArrayFASTA(){ return arrayFASTA; }
    public String getErrMsg(){ return String.join("\n", errorMessages); }
    public String[] getErrMsgArray(){ return errorMessages.toArray(new String[0]); }

    private String submittedFASTA;
    private final ArrayList<FASTAObject> arrayFASTA = new ArrayList<>();
    private int automaticNameCount = 0;
    private int numErrors = 0;
    private final Set<String> errorMessages = new LinkedHashSet<>();

    public ReadFASTAAndFormat(String submittedFASTA){
        this.submittedFASTA = submittedFASTA;
        assignIDAndSequence();
    }

    private void assignIDAndSequence() {

        //Variables to hold sequence as String and a StringBuilder to hold
        String id = null;
        StringBuilder sequenceCollector = new StringBuilder();

        //hack to add a buffer to the of the submission to prevent ugly
        //code in checking the last item
        submittedFASTA = submittedFASTA + "\n\t\n";

        //Make array
        String[] splitInputBlankLines = submittedFASTA.split("\\r?\\n");

        //number comparisons to walk through the set
        int idx = 0;
        final int endNum = splitInputBlankLines.length-1;

        // Walk through algorithm
        while (idx < endNum) {
            //looks like ID
            if (splitInputBlankLines[idx].trim().startsWith(">")) {
                id = splitInputBlankLines[idx].trim();
                idx++;
            }

            //looks like Sequence
            if (!splitInputBlankLines[idx].trim().startsWith(">") && splitInputBlankLines[idx].trim().length() > 0) {

                //Walk through whilst consecutive lines still look like sequences
                //refactor candidate
                while (idx < endNum &&
                        !splitInputBlankLines[idx].trim().startsWith(">") &&
                        splitInputBlankLines[idx].trim().length() > 0) {

                    //add to builder and increment
                    sequenceCollector.append(splitInputBlankLines[idx].trim());
                    idx++;
                }
            }

            //identify blank lines
            if (splitInputBlankLines[idx].trim().length() == 0) {
                //walk through fasta lines
                while (idx < endNum && splitInputBlankLines[idx].trim().length() == 0) {
                    idx++;
                }
            }

            //now finished walking through, try and build FASTA object
            //if the sequence is missing just create error message
            if (sequenceCollector.length() == 0) {
                if (id != null ) {
                    errorMessages.add("Submitted sequence " + id + " had no associated sequence.");
                }
            } else {

                //generate ID name if missing
                if (id == null || id.length() == 1) { id = ">Sequence-" + automaticNameCount++; }

                //try and build object
                try {
                    arrayFASTA.add(new FASTAObject(id, sequenceCollector.toString()));
                } catch (ExceptionsFASTALength | ExceptionsFASTABadAA | ExceptionsFASTANoSequence err) {
                    numErrors++;
                    errorMessages.add(err.getMessage());
                }
            }

            //empty variables
            sequenceCollector.setLength(0);
            id = null;
        }

    }
}

package com.proteinsol.tidyfasta.packages;

import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTANoSequence;
import com.proteinsol.tidyfasta.utilities.MaybeID;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadFASTAAndFormat {
    public int getValidatedNumber(){ return arrayFASTA.size(); }
    public int getSubmittedNumber(){ return arrayFASTA.size()+getNumErrors(); }
    public int getNumErrors(){ return getErrMsgArray().length; }
    public List<FASTAObject> getArrayFASTA(){ return arrayFASTA; }
    public String getErrMsg(){ return String.join("\n", errorMessages); }
    public String[] getErrMsgArray(){ return errorMessages.toArray(new String[0]); }

    private String submittedFASTA;
    private final ArrayList<FASTAObject> arrayFASTA = new ArrayList<>();
    private int automaticNameCount = 0;
    private final Set<String> errorMessages = new LinkedHashSet<>();

    Logger logger = Logger.getLogger(ReadFASTAAndFormat.class.getName());

    public ReadFASTAAndFormat(String submittedFASTA){
        this.submittedFASTA = submittedFASTA;
        logger.log(Level.FINER, () -> "Reading FASTA data "+submittedFASTA);
        assignIDAndSequence();
    }

    private void assignIDAndSequence() {

        //Variables to hold sequence as String and a StringBuilder to hold
        MaybeID id = new MaybeID();
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
            if (id.testLine(splitInputBlankLines[idx].trim())){
                logger.log(Level.FINER, () -> "ID identified as "+ id.getID());
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
                logger.log(Level.FINER, () -> "Sequence identified as " + sequenceCollector.toString() );
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
                if (id.found()) {
                    String errMsg = "Submitted sequence " + id.getID() + " had no associated sequence.";
                    logger.log(Level.INFO, errMsg);
                    errorMessages.add(errMsg);
                }
            } else {
                //generate ID name if missing
                if (!id.found()) {
                    id.setID(">Sequence-" + automaticNameCount++);
                    logger.log(Level.INFO, () -> "Placeholder name given for sequence without name " + id.getID());
                }

                //try and build object
                try {
                    arrayFASTA.add(new FASTAObject(id.getID(), sequenceCollector.toString()));
                } catch (ExceptionsFASTALength | ExceptionsFASTABadAA | ExceptionsFASTANoSequence err) {
                    logger.log(Level.INFO, () -> "Errors raised " + err.getMessage());
                    errorMessages.add(err.getMessage());
                }
            }

            //empty variables
            sequenceCollector.setLength(0);
            id.empty();
        }

    }
}

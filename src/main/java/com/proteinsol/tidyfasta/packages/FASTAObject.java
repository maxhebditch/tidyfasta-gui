package com.proteinsol.tidyfasta.packages;

import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.ExceptionsFASTANoSequence;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FASTAObject {
    public final String sequence;
    public final String id;
    private int naa;

    private static final int MAX_NAA_ACCEPTED = 50000;
    private static final int MIN_NAA_ACCEPTED = 3;

    public int getNaa() {
        return naa;
    }

    Logger logger = Logger.getLogger(FASTAObject.class.getName());

    public FASTAObject(String subSequence){

        logger.log(Level.FINE,() -> "New FASTA object creation attempt from sequence "+subSequence);

        if ( subSequence.startsWith(">")) {
            String errMsg = "Submitted sequence " + subSequence + " had no associated sequence.";
            logger.log(Level.FINE, errMsg);
            throw new ExceptionsFASTANoSequence(errMsg);
        }

        id = ">Protein-Sol-Sequence";
        sequence = subSequence.toUpperCase();

        validateLength();
        validateAA();
    }

    public FASTAObject(String subID, String subSequence){

        id = subID;
        sequence = subSequence.toUpperCase();

        logger.log(Level.FINE,() -> "New FASTA object creation attempt from ID "+id+" and sequence "+subSequence);

        validateLength();
        validateAA();
    }

    public void validateLength(){

        naa = sequence.length();
        logger.log(Level.FINE,() -> "Item "+id+" has "+naa+" amino acids");

        String errMsg = "Submitted sequence %s had %d amino acids, which is %s than the limit of %d amino acids.";

        if (naa > MAX_NAA_ACCEPTED) {
            String tooLongError = String.format(errMsg,id,naa,"longer",MAX_NAA_ACCEPTED);
            logger.log(Level.INFO,tooLongError);
            throw new ExceptionsFASTALength(tooLongError);
        } else if (naa < MIN_NAA_ACCEPTED) {
            String tooShortError = String.format(errMsg,id,naa,"shorter",MIN_NAA_ACCEPTED);
            logger.log(Level.INFO,tooShortError);
            throw new ExceptionsFASTALength(tooShortError);
        }

    }

    public void validateAA() {
        String regex = "([^qwertyipasdfghklcvnmQWERTYIPASDFGHKLCVNM]+)";
        Pattern nonCanonicalAA = Pattern.compile(regex);
        Matcher matcher = nonCanonicalAA.matcher(sequence);

        if(matcher.find()){
            String errMsg = "Non Canonical amino acid found in " + id + ".";
            logger.log(Level.INFO,errMsg);
            throw new ExceptionsFASTABadAA(errMsg);
        }
    }

}

package com.proteinsol.tidyfasta.packages;

import com.proteinsol.tidyfasta.exceptions.exceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTANoSequence;

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

    public FASTAObject(String subSequence){

        if ( subSequence.startsWith(">")) {
            String errMsg = "Submitted sequence " + subSequence + " had no associated sequence.";
            throw new exceptionsFASTANoSequence(errMsg);
        }

        id = ">Protein-Sol-Sequence";
        sequence = subSequence.toUpperCase();

        validateLength();
        validateAA();
    }

    public FASTAObject(String subID, String subSequence){

        id = subID;
        sequence = subSequence.toUpperCase();

        validateLength();
        validateAA();
    }

    public void validateLength(){

        naa = sequence.length();

        if ((naa > MAX_NAA_ACCEPTED) || (naa < MIN_NAA_ACCEPTED)) {
            String lengthError = null;
            int warningValue = 0;

            if (naa > MAX_NAA_ACCEPTED) {
                lengthError = "longer";
                warningValue = MAX_NAA_ACCEPTED;
            } else {
                lengthError = "shorter";
                warningValue = MIN_NAA_ACCEPTED;
            }

            String errMsg = String.format("Submitted sequence %s had %d amino acids, which is %s than the limit of " +
                    "%d amino acids.", id, naa, lengthError, warningValue);

            throw new exceptionsFASTALength(errMsg);
        }

    }

    public void validateAA() {
        String regex = "([^qwertyipasdfghklcvnmQWERTYIPASDFGHKLCVNM]+)";
        Pattern nonCanonicalAA = Pattern.compile(regex);
        Matcher matcher = nonCanonicalAA.matcher(sequence);

        if(matcher.find()){
            throw new exceptionsFASTABadAA("Non Canonical amino acid found in " + id + "." );
        }
    }
}

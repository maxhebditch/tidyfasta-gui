package com.proteinsol.tidyfasta.packages;

import com.proteinsol.tidyfasta.exceptions.exceptionsFASTABadAA;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTALength;
import com.proteinsol.tidyfasta.exceptions.exceptionsFASTANoSequence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FASTAObject {
    public String Sequence;
    public String ID;
    public int NAA;

    private static final int MAX_NAA_ACCEPTED = 50000;
    private static final int MIN_NAA_ACCEPTED = 3;

    public int getNAA() {
        return NAA;
    }

    public FASTAObject(String subSequence)
            throws exceptionsFASTABadAA, exceptionsFASTALength, exceptionsFASTANoSequence {

        if ( subSequence.startsWith(">")) {
            String errMsg = "Submitted sequence " + subSequence + " had no associated sequence.";
            throw new exceptionsFASTANoSequence(errMsg);
        }

        ID = ">Protein-Sol-Sequence";
        Sequence = subSequence;

        validateLength();
        validateAA();
        Sequence = Sequence.toUpperCase();
    }

    public FASTAObject(String subID, String subSequence)
            throws exceptionsFASTABadAA, exceptionsFASTALength, exceptionsFASTANoSequence {

        ID = subID;
        Sequence = subSequence;

        validateLength();
        validateAA();
        Sequence = Sequence.toUpperCase();
    }

    public void validateLength() throws exceptionsFASTALength, exceptionsFASTANoSequence {

        NAA = Sequence.length();

        if (NAA > MAX_NAA_ACCEPTED){
            String errMsg = "Submitted sequence " + ID + " had " + NAA
                    + " amino acids, which is longer than the limit of "
                    + MAX_NAA_ACCEPTED + " amino acids.";
            throw new exceptionsFASTALength(errMsg);
        } else if (NAA < MIN_NAA_ACCEPTED){
            String errMsg = "Submitted sequence " + ID + " had " + NAA
                    + " amino acids, which is shorter than the limit of "
                    + MIN_NAA_ACCEPTED + " amino acids.";
            throw new exceptionsFASTALength(errMsg);
        }
    }

    public void validateAA() throws exceptionsFASTABadAA {
        String regex = "([^qwertyipasdfghklcvnmQWERTYIPASDFGHKLCVNM]+)";
        Pattern NonCanonicalAA = Pattern.compile(regex);
        Matcher matcher = NonCanonicalAA.matcher(Sequence);

        if(matcher.find()){
            throw new exceptionsFASTABadAA("Non Canonical amino acid found in " + ID + "." );
        }
    }
}

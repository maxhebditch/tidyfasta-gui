package com.proteinsol.tidyfasta.exceptions;

public class exceptionsFASTANoSequence extends RuntimeException{

    public exceptionsFASTANoSequence(String errorMessage){
        super(errorMessage);
    }

    public exceptionsFASTANoSequence(String errorMessage, Throwable error){
        super(errorMessage, error);
    }
}

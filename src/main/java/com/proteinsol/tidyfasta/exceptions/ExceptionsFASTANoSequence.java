package com.proteinsol.tidyfasta.exceptions;

public class ExceptionsFASTANoSequence extends RuntimeException{
    public ExceptionsFASTANoSequence(String errorMessage){
        super(errorMessage);
    }
}

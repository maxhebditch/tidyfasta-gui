package com.proteinsol.tidyfasta.exceptions;

public class exceptionsFASTABadAA extends RuntimeException{

    public exceptionsFASTABadAA(String errorMessage){
        super(errorMessage);
    }

    public exceptionsFASTABadAA(String errorMessage, Throwable error){
        super(errorMessage, error);
    }
}

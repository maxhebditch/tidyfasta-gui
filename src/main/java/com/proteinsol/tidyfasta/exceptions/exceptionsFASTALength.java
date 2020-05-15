package com.proteinsol.tidyfasta.exceptions;

public class exceptionsFASTALength extends RuntimeException{

    public exceptionsFASTALength(String errorMessage){
        super(errorMessage);
    }

    public exceptionsFASTALength(String errorMessage, Throwable error){
        super(errorMessage, error);
    }
}

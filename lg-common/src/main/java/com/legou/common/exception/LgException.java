package com.legou.common.exception;

import lombok.Getter;

@Getter
public class LgException extends RuntimeException{
    private int status;
    public LgException(int status){
        this.status = status;
    }

    public LgException(int status, String message){
        super(message);
        this.status = status;
    }
    public LgException(int status, String message, Throwable cause){
        super(message,cause);
        this.status = status;
    }
    public LgException(int status, Throwable cause){
        super(cause);
        this.status = status;
    }
}

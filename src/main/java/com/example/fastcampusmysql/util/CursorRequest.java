package com.example.fastcampusmysql.util;

public record CursorRequest (Long key, Integer size){
    public static final Long NONE = -1L;

    public Boolean hasKey(){
        return key != null;
    }

    public CursorRequest next(Long key){
        return new CursorRequest(key, size);
    }
}

package com.r2dbc.orm.first_draft.exception.common;

import lombok.Builder;

public class CommonExceptions {

    public static class DataNotFoundException extends RuntimeException {
        @Builder
        public DataNotFoundException(String message) {
            super(message);
        }
    }

}

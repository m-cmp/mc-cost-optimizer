package com.mcmp.costselector.model.common;

import org.springframework.http.HttpStatus;

public enum ResponseSpecification implements ResponseSpecTranslator{
    OK(200) {

        @Override
        public String getMessage() {
            return HttpStatus.OK.getReasonPhrase();
        }
    },

    /* 400 */
    INVALID_AUTHORITY(406) {

        @Override
        public String getMessage() {
            return "Invalid Authority";
        }
    },
    MISSING_PARAMETER(412) {

        @Override
        public String getMessage() {
            return "Missing parameter";
        }
    },
    INVALID_PARAMETER(416) {

        @Override
        public String getMessage() {
            return "Invalid parameter";
        }
    },
    NOT_FOUND_USER(453) {

        @Override
        public String getMessage() {
            return "Not Found User";
        }
    },
    BAD_REQUEST(464) {

        @Override
        public String getMessage() {
            return "Bad request";
        }
    },

    /* 500 */
    INTERNAL_ERROR(500) {

        @Override
        public String getMessage() {
            return HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        }
    },
    DATA_EMPTY(520) {

        @Override
        public String getMessage() {
            return "Data empty";
        }
    },
    INSERT_FAILED(521) {

        @Override
        public String getMessage() {
            return "Insert failed";
        }
    },
    UPDATE_FAILED(522) {

        @Override
        public String getMessage() {
            return "Update failed";
        }
    },
    DELETE_FAILED(523) {

        @Override
        public String getMessage() {
            return "Delete failed";
        }
    },
    ITEM_DUPLICATION(526) {

        @Override
        public String getMessage() {
            return "Item duplication";
        }
    },
    UNHANDLED_ERROR(999) {

        @Override
        public String getMessage() {
            return "Unhandled Error";
        }
    },;

    private final int code;

    ResponseSpecification(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

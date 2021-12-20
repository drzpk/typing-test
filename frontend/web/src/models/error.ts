export interface ValidationErrorsModel {
    errors: Array<ValidationError>;
}

export interface ValidationError {
    type: string;
    message: string;
}

export interface FieldValidationError extends ValidationError {
    type: "field";
    field: string;
}

export class ValidationFailedError {
    errors: Array<ValidationError>;

    constructor(errors: Array<ValidationError>) {
        this.errors = errors;
    }
}

export interface ErrorCodeModel {
    code: ErrorCode;
    message: string;
    object: any | null;
    additionalData: { [key: string]: any } | null;
}

export enum ErrorCode {
    // noinspection JSUnusedGlobalSymbols
    TEST_DEFINITION_NOT_FOUND = "TEST_DEFINITION_NOT_FOUND",
    CANNOT_DELETE_FINISHED_TEST = "CANNOT_DELETE_FINISHED_TEST",
    TEST_START_TIMEOUT = "TEST_START_TIMEOUT",
    TEST_START_WRONG_STATE = "TEST_START_WRONG_STATE",
    TEST_FINISH_TIMEOUT = "TEST_FINISH_TIMEOUT",
    TEST_FINISH_WRONG_STATE = "TEST_FINISH_WRONG_STATE",
    TEST_REGENERATE_WORD_ERROR = "TEST_REGENERATE_WORD_ERROR",

    // Artifical, client-side errors
    WORD_LIST_NOT_FOUND = "WORD_LIST_NOT_FOUND",
    WORD_IMPORT_SYNTAX_ERROR = "WORD_IMPORT_SYNTAX_ERROR",
    UNKNOWN_ERROR = "UNKNOWN_ERROR"
}

export class ServerError {
    data: ErrorCodeModel;

    constructor(data: ErrorCodeModel) {
        this.data = data;
    }
}

export class UserNotLoggedInError {}
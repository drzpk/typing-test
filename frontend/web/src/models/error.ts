export interface ValidationErrors {
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
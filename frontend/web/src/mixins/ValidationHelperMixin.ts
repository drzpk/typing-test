import {Component, Vue} from "vue-property-decorator";
import {validationMixin} from "vuelidate";
import {FieldValidationError, ValidationFailedError} from "@/models/error";
import {ValidationMessagesState} from "@/models/validation-support";
import {extractFromObject} from "@/utils/object-utils";

@Component({
    mixins: [
        validationMixin
    ]
})
export default class ValidationHelperMixin extends Vue {
    formSent = false;
    state: ValidationMessagesState | null = null;

    mounted(): void {
        this.state = {
            validations: this.$v,
            serverFieldErrors: {},
            validationMessages: this.$options.validationMessages
        };
    }

    triggerValidation(): void {
        this.formSent = true;
        this.$v.$touch();
    }

    validateState(name: string): boolean | null {
        const element = this.$v[name];
        return element?.$dirty ? (!element?.$error && !this.state!.serverFieldErrors[name]) : null;
    }

    resetState(name: string): void {
        const element = this.$v[name];
        element?.$reset();
        this.removeServerFieldError(name);
        this.formSent = false;
    }

    processValidationError(error: ValidationFailedError) {
        let serverFieldNameMapping = this.$options.serverFieldNameMapping;
        if (!serverFieldNameMapping)
            serverFieldNameMapping = {};

        const fieldErrors: any = {};
        for (let i = 0; i < error.errors.length; i++) {
            const validationError = error.errors[i];
            if (validationError.type != "field")
                continue;

            const fieldError = validationError as FieldValidationError;
            let fieldName = fieldError.field;
            if (serverFieldNameMapping![fieldName])
                fieldName = serverFieldNameMapping![fieldName];

            if (!this.fieldExists(fieldName))
                console.warn(`Field ${fieldName} isn't defined in validations`);

            fieldErrors[fieldName] = fieldError.message;
        }

        this.state!.serverFieldErrors = fieldErrors;
    }

    private fieldExists(fieldName: string): boolean {
        return extractFromObject(fieldName, this.state!.validations) != null;
    }

    private removeServerFieldError(fieldName: string) {
        delete this.state!.serverFieldErrors[fieldName];
    }
}

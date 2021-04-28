import {Validation} from "vuelidate";
import {Vue} from "vue-property-decorator";
import {ValidationMessages} from "vue/types/options";
import {ValidationGroups, ValidationProperties } from "vue/types/vue";

export interface ValidationMessagesState {
    validations: ValidationProperties<Vue> & ValidationGroups & Validation;
    serverFieldErrors: { [key: string]: string };
    validationMessages: ValidationMessages | undefined;
}

declare module 'vue/types/options' {
    type DynamicValidationMessage = (validation: Validation) => string;
    type ValidationMessageDefinition = string | DynamicValidationMessage;

    interface ValidationMessages {
        [prop: string]: ValidationMessages | ValidationMessageDefinition;
    }

    interface ComponentOptions<V extends Vue> {
        validationMessages?: ValidationMessages;
        // server field name -> local field name
        serverFieldNameMapping?: { [key: string]: string };
    }
}
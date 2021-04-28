<template>
    <div>
        <b-form-invalid-feedback v-for="message in messages" force-show :key="message">
            {{ message }}
        </b-form-invalid-feedback>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {Validation} from "vuelidate";
    import {ValidationMessagesState} from "@/models/validation-support";
    import {extractFromObject} from "@/utils/object-utils";

    @Component({
        props: [
            "fieldName",
            "state"
        ]
    })
    export default class ValidationMessageManager extends Vue {
        messages: Array<string> = [];

        private fieldName!: string;
        private state!: ValidationMessagesState;

        @Watch("state.validations", {deep: true})
        onValidationsUpdated() {
            this.updateMessages();
        }

        @Watch("state.serverFieldErrors", {deep: true})
        onServerFieldErrorsChanged() {
            this.updateMessages();
        }

        private updateMessages(): void {
            const vuelidateObject = extractFromObject(this.fieldName, this.state.validations) as Validation;
            if (!vuelidateObject) {
                console.warn(`Field ${this.fieldName} wasn't found in the validations definition.`);
                return;
            }

            const validationMessages = extractFromObject(this.fieldName, this.state.validationMessages) as { [key: string]: string };
            if (!validationMessages) {
                console.warn(`Validation messages weren't found for field ${this.fieldName}.`);
                return;
            }

            const messages: Array<string> = [];
            for (const key in validationMessages!) {
                if (Object.prototype.hasOwnProperty.call(validationMessages!, key)
                    && vuelidateObject.$dirty
                    && !(vuelidateObject as any)[key]) {
                    messages.push(validationMessages![key] as string);
                }
            }

            const serverError = this.state.serverFieldErrors[this.fieldName];
            if (serverError)
                messages.push(serverError);

            this.messages = messages;
        }
    }
</script>

<style lang="scss">

</style>
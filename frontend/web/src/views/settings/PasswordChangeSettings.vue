<template>
    <div>
        <b-form @submit.stop.prevent="changePassword">
            <b-form-group label-for="current-password" label="Current password">
                <b-form-input id="current-password" type="password" name="currentPassword"
                              v-model="currentPassword" :state="validateState('currentPassword')"
                              @input="resetState('currentPassword')"></b-form-input>

                <ValidationMessageManager field-name="currentPassword" :state="state"/>
            </b-form-group>

            <b-form-group label-for="new-password" label="New password">
                <b-form-input id="new-password" type="password" name="newPassword"
                              v-model="newPassword" :state="validateState('newPassword')"
                              @input="resetState('newPassword')"></b-form-input>

                <ValidationMessageManager field-name="newPassword" :state="state"/>
            </b-form-group>

            <b-form-group label-for="repeat-new-password" label="Repeat new password">
                <b-form-input id="repeat-new-password" type="password" name="newPasswordRepeat"
                              v-model="newPasswordRepeat" :state="validateState('newPasswordRepeat')"
                              @input="resetState('newPasswordRepeat')"></b-form-input>

                <ValidationMessageManager field-name="newPasswordRepeat" :state="state"/>
            </b-form-group>

            <div class="update-button-wrapper">
                <b-button @click="changePassword" :disabled="formSent">Change</b-button>
            </div>
        </b-form>
        <pre>
            {{$v}}
        </pre>
    </div>
</template>

<script lang="ts">
    import {Component} from "vue-property-decorator";
    import {AuthenticationDetails} from "@/models/user";
    import {mapState} from "vuex";
    import {required} from "vuelidate/lib/validators";
    import ApiService from "@/services/Api.service";
    import {mixins} from "vue-class-component";
    import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
    import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
    import {ValidationFailedError} from "@/models/error";

    @Component({
        components: {ValidationMessageManager},
        computed: mapState([
            "authenticationDetails"
        ]),
        validations: {
            currentPassword: {
                required
            },
            newPassword: {
                required
            },
            newPasswordRepeat: {
                required,
                sameAsPassword: function (value) {
                    return (this as PasswordChangeSettings).newPassword == value;
                }
            }
        },
        validationMessages: {
            currentPassword: {
                required: "Current password is required."
            },
            newPassword: {
                required: "New password is required."
            },
            newPasswordRepeat: {
                required: "Repeated password is required.",
                sameAsPassword: "Repeated password doesn't match the new password."
            }
        },
        serverFieldNameMapping: {
            "oldPassword": "currentPassword"
        }
    })
    export default class PasswordChangeSettings extends mixins(ValidationHelperMixin) {
        authenticationDetails!: AuthenticationDetails;
        currentPassword = "";
        newPassword = "";
        newPasswordRepeat = "";

        changePassword(): void {
            this.triggerValidation();
            if (this.$v.$invalid)
                return;

            const request = {
                oldPassword: this.currentPassword,
                newPassword: this.newPassword
            };

            ApiService.changePassword(request)
                .catch(error => {
                    if (error instanceof ValidationFailedError) {
                        this.processValidationError(error);
                    }
                })
        }
    }
</script>

<style lang="scss">
    .settings-column {
        padding-left: 3em;
        padding-right: 3em;
    }

    div.update-button-wrapper {
        text-align: right;

        .error {
            font-size: 0.8em;
            margin-bottom: 0.7em;
            color: red;
        }
    }
</style>
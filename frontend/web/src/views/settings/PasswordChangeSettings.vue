<template>
    <div>
        <b-form @submit.stop.prevent="changePassword">
            <b-form-group label-for="current-password" label="Current password">
                <b-form-input id="current-password" type="password" name="currentPassword"
                              v-model="currentPassword" :state="validateState('currentPassword')"
                              @input="resetState('currentPassword')"></b-form-input>

                <b-form-invalid-feedback v-if="!$v.currentPassword.required">
                    Current password is required.
                </b-form-invalid-feedback>
            </b-form-group>

            <b-form-group label-for="new-password" label="New password">
                <b-form-input id="new-password" type="password" name="newPassword"
                              v-model="newPassword" :state="validateState('newPassword')"
                              @input="resetState('newPassword')"></b-form-input>

                <b-form-invalid-feedback v-if="!$v.newPassword.required">
                    New password is required.
                </b-form-invalid-feedback>
            </b-form-group>

            <b-form-group label-for="repeat-new-password" label="Repeat new password">
                <b-form-input id="repeat-new-password" type="password" name="newPasswordRepeat"
                              v-model="newPasswordRepeat" :state="validateState('newPasswordRepeat')"
                              @input="resetState('newPasswordRepeat')"></b-form-input>

                <b-form-invalid-feedback v-if="!$v.newPasswordRepeat.required">
                    Repeated password is required.
                </b-form-invalid-feedback>
                <b-form-invalid-feedback v-if="!$v.newPasswordRepeat.sameAsPassword">
                    Repeated password doesn't match the new password.
                </b-form-invalid-feedback>
            </b-form-group>

            <div class="update-button-wrapper">
                <b-button @click="changePassword" :disabled="formSent">Change</b-button>
            </div>
        </b-form>
        {{$v}}
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {AuthenticationDetails} from "@/models/user";
    import {mapState} from "vuex";
    import {required} from "vuelidate/lib/validators";
    import {validationMixin} from "vuelidate";
    import ApiService from "@/services/Api.service";

    @Component({
        computed: mapState([
            "authenticationDetails"
        ]),
        mixins: [
            validationMixin
        ],
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
        }
    })
    export default class PasswordChangeSettings extends Vue {
        authenticationDetails!: AuthenticationDetails;
        currentPassword = "";
        newPassword = "";
        newPasswordRepeat = "";
        formSent = false;

        changePassword() {
            this.formSent = true;
            this.$v.$touch();

            if (this.$v.$invalid)
                return;

            const request = {
                oldPassword: this.currentPassword,
                newPassword: this.newPassword
            };
            ApiService.changePassword(request);
        }

        validateState(name: string) {
            const element = this.$v[name];
            return element?.$dirty ? !element?.$error : null;
        }

        resetState(name: string) {
            const element = this.$v[name];
            element?.$reset();
            this.formSent = false;
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
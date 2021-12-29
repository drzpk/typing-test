<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="10" lg="6" offset="1" offset-lg="3">
                    <b-form id="login-form" @submit.stop.prevent="createNewUser">
                        <h4>Create new account</h4>
                        <br>

                        <b-form-group label-cols="3" label="E-mail">
                            <b-form-input type="text" v-model="email" @input="doResetState('email')"
                                          placeholder="E-mail" :state="validateState('email')"
                                          :disabled="registered"/>

                            <ValidationMessageManager field-name="email" :state="state"/>
                        </b-form-group>

                        <b-form-group label-cols="3" label="Display name">
                            <b-form-input type="text" v-model="displayName" @input="doResetState('displayName')"
                                          placeholder="Display name" :state="validateState('displayName')"
                                          :disabled="registered"/>

                            <ValidationMessageManager field-name="displayName" :state="state"/>
                        </b-form-group>

                        <b-form-group label-cols="3" label="Password">
                            <b-form-input type="password" v-model="password" @input="doResetState('password')"
                                          placeholder="Password" :state="validateState('password')"
                                          :disabled="registered"/>

                            <ValidationMessageManager field-name="password" :state="state"/>
                        </b-form-group>

                        <b-form-group label-cols="3" label="Repeat password">
                            <b-form-input type="password" v-model="passwordRepeat"
                                          @input="doResetState('passwordRepeat')"
                                          placeholder="Repeat password" :state="validateState('passwordRepeat')"
                                          :disabled="registered"/>

                            <ValidationMessageManager field-name="passwordRepeat" :state="state"/>
                        </b-form-group>

                        <div id="form-controls">
                            <b-button type="submit" variant="primary"
                                      :disabled="unknownError || formSent || registered">Submit</b-button>
                        </div>

                        <span class="incorrect" v-if="unknownError">Unknown error has occurred. Contact the administrator.</span>

                        <div v-show="registered">
                            <br>
                            <b-alert show="show" variant="success">
                                Account has been created. Before you staring using it, the administrator
                                must activate it. <router-link to="/login">Go to login page.</router-link>
                            </b-alert>
                        </div>
                    </b-form>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {Component} from "vue-property-decorator";
import {RegisterData} from "@/store";
import {email, required} from "vuelidate/lib/validators";
import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
import {mixins} from "vue-class-component";
import {ValidationFailedError} from "@/models/error";

@Component({
        components: {ValidationMessageManager},
        validations: {
            email: {
                required,
                email
            },
            displayName: {
                required
            },
            password: {
                required
            },
            passwordRepeat: {
                required,
                sameAsPassword: function (value) {
                    return (this as Register).password == value;
                }
            }
        },
        validationMessages: {
            email: {
                required: "Email is required.",
                email: "Value must be a valid email address."
            },
            displayName: {
                required: "Display name is required."
            },
            password: {
                required: "Password is required."
            },
            passwordRepeat: {
                required: "Repeated password is required.",
                sameAsPassword: "Repeated password doesn't match the new password."
            }
        }
    })
    export default class Register extends mixins(ValidationHelperMixin) {
        email = "";
        displayName = "";
        password = "";
        passwordRepeat = "";

        unknownError = false;
        registered = false;

        doResetState(name: string): void {
            this.unknownError = false;
            this.resetState(name);
        }

        createNewUser(): void {
            this.triggerValidation();
            if (this.$v.$invalid)
                return;

            const registerData: RegisterData = {
                email: this.email,
                displayName: this.displayName,
                password: this.password
            };

            this.$store.dispatch("register", registerData).then(() => {
                this.registered = true;
            }).catch(error => {
                if (error instanceof ValidationFailedError) {
                    this.processValidationError(error);
                } else {
                    this.unknownError = true;
                }
            });
        }
    }
</script>

<style lang="scss" scoped>
    #login-form {
        margin-top: 4em;
        padding: 2em;
        border: 1px solid lightgray;
        border-radius: 5px;
    }

    #form-controls {
        text-align: right;
    }

    .incorrect {
        color: red;
    }
</style>
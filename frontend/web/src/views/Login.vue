<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="10" md="6" offset="1" offset-md="3">
                    <b-form id="login-form" @submit="loginUser">
                        <h4>Sign in to the application</h4>
                        <br>

                        <b-form-group label-cols="3" label="E-mail">
                            <b-form-input type="email" v-model="email" @input="resetIncorrectStatus"
                                          placeholder="E-mail" required/>
                        </b-form-group>
                        <b-form-group label-cols="3" label="Password">
                            <b-form-input type="password" v-model="password" @input="resetIncorrectStatus"
                                          placeholder="Password" required/>
                        </b-form-group>
                        <div id="form-controls">
                            <router-link to="/register">Create new account</router-link>
                            <b-button type="submit" variant="primary" :disabled="incorrect">Submit</b-button>
                        </div>

                        <span class="incorrect" v-if="incorrect">Entered credentials are incorrect.</span>
                    </b-form>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import router from "@/router";

@Component
    export default class Login extends Vue {
        email = "";
        password = "";
        incorrect = false;

        resetIncorrectStatus(): void {
            this.incorrect = false;
        }

        loginUser(event: Event): void {
            event.preventDefault();
            this.$store.dispatch("login", {email: this.email, password: this.password}).then(() => {
                router.push({name: "TestPage"});
            }).catch(() => {
                this.incorrect = true;
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

        button {
            float: right;
        }
    }

    .incorrect {
        color: red;
    }
</style>
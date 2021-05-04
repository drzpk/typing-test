<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card title="Administration" v-if="authenticationDetails && authenticationDetails.isAdmin">
                        <p>You have administration rights to this application.</p>
                        <b-button @click="$router.push('/settings/admin')">Open administration panel</b-button>
                    </b-card>

                    <b-card title="Account settings">
                        <b-row>
                            <b-col cols="6" class="settings-column">
                                <AccountSettings/>
                            </b-col>
                            <b-col cols="6" class="settings-column">
                                <PasswordChangeSettings/>
                            </b-col>
                        </b-row>
                    </b-card>

                    <b-card title="Account information">
                        <b-form-group label-cols="3" label-for="email-address" label="Email address">
                            <b-form-input id="email-address" disabled
                                          :value="authenticationDetails.email"></b-form-input>
                        </b-form-group>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import AccountSettings from "@/views/settings/AccountSettings.vue";
    import PasswordChangeSettings from "@/views/settings/PasswordChangeSettings.vue";
    import {mapState} from "vuex";
    import {AuthenticationDetails} from "@/models/user";

    @Component({
        components: {
            AccountSettings,
            PasswordChangeSettings
        },
        computed: mapState([
            "authenticationDetails"
        ])
    })
    export default class Settings extends Vue {
        authenticationDetails!: AuthenticationDetails
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
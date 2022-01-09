<template>
    <div id="settings">
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card title="Administration" v-if="authenticationDetails && authenticationDetails.isAdmin">
                        <p>You have administration rights to this application.</p>
                        <b-button @click="$router.push('/settings/admin')">Open administration panel</b-button>
                    </b-card>

                    <b-card title="Account information">
                        <b-form-group label-cols="3" label-for="email-address" label="Email address">
                            <b-form-input id="email-address" disabled
                                          :value="authenticationDetails.email"></b-form-input>
                        </b-form-group>

                        <b-form-group label-cols="3" label-for="registration-date" label="Registration date">
                            <b-form-input id="registration-date" disabled
                                          :value="registrationDate"></b-form-input>
                        </b-form-group>

                        <b-form-group label-cols="3" label-for="completed-tests" label="Completed tests">
                            <b-form-input id="completed-tests" disabled
                                          :value="completedTests"></b-form-input>
                        </b-form-group>

                        <b-form-group label-cols="3" label-for="tests-per-day" label="Tests per day">
                            <b-form-input id="tests-per-day" disabled
                                          :value="testsPerDay"></b-form-input>
                        </b-form-group>
                    </b-card>

                    <b-card title="Test statistics">
                        <p>Click the button below to view your statistics.</p>
                        <b-button @click="$router.push({name: 'TestStats'})">Show statistics</b-button>
                    </b-card>

                    <b-card title="Account settings" v-loading-overlay="pendingRequest">
                        <b-row>
                            <b-col cols="6" class="settings-column">
                                <AccountSettings/>
                            </b-col>
                            <b-col cols="6" class="settings-column">
                                <PasswordChangeSettings/>
                            </b-col>
                        </b-row>
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
import {mapGetters, mapState} from "vuex";
import {AuthenticationDetails, GlobalStats} from "@/models/user";
import {formatTime} from "@/utils/time-utils";
import ApiService from "@/services/Api.service";

@Component({
    components: {
        AccountSettings,
        PasswordChangeSettings
    },
    computed: {
        ...mapState([
            "authenticationDetails"
        ]),
        ...mapGetters([
            "pendingRequest"
        ])
    }
})
export default class Settings extends Vue {
    authenticationDetails!: AuthenticationDetails
    pendingRequest!: boolean;

    globalStats: GlobalStats | null = null;

    get registrationDate(): string {
        return formatTime(this.authenticationDetails.createdAt);
    }

    get completedTests(): string {
        if (this.globalStats != null)
            return this.globalStats.completedTests.toString();
        else
            return "";
    }

    get testsPerDay(): string {
        if (this.globalStats != null)
            return (Math.floor(this.globalStats.testsPerDay * 100 + 0.5) / 100).toString();
        else
            return "";
    }

    mounted(): void {
        ApiService.getUserGlobalStats().then(stats => this.globalStats = stats);
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
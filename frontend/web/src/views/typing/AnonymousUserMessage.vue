<template>
    <div>
        <b-alert :show="showMessage" variant="warning">
            You have completed the test as an anonymous user. Your tests results will be lost unless
            you
            <router-link to="/login">sign in</router-link>
            to your account.
        </b-alert>
    </div>
</template>

<script lang="ts">
import Vue from "vue";
import {Component} from "vue-property-decorator";
import {TestStateModel} from "@/models/tests";
import {mapGetters} from "vuex";

@Component({
    computed: mapGetters([
        "testState",
        "isLoggedIn"
    ])
})
export default class AnonymousUserMessage extends Vue {
    private testState!: TestStateModel | undefined;
    private isLoggedIn!: boolean

    get showMessage(): boolean {
        return this.testState == TestStateModel.FINISHED && !this.isLoggedIn;
    }
}
</script>
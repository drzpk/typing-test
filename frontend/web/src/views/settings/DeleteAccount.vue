<template>
    <div v-if="authenticationDetails && !authenticationDetails.isAdmin">
        <div>
            <b-button variant="danger" v-b-modal:account-deletion-modal>Delete account</b-button>
        </div>

        <b-modal
            id="account-deletion-modal"
            title="Account deletion"
            cancel-title="No"
            ok-variant="danger"
            :ok-disabled="yesButtonTitle !== 'Yes'"
            :ok-title="yesButtonTitle"
            @shown="onModalShown"
            @ok="doDeleteAccount">
            <p>Deleting your account will erase all of your data (including the test statistics)
                from the application. This action is <strong>irriversible</strong>.</p>
            <p>Are you sure you want to continue?</p>
        </b-modal>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import {mapState} from "vuex";
import {AuthenticationDetails} from "@/models/user";

const yesButtonTimeout = 5;

@Component({
    computed: mapState(["authenticationDetails"])
})
export default class DeleteAccount extends Vue {
    authenticationDetails!: AuthenticationDetails | null;

    private yesButtonTitle = "Yes"
    private intervalId: number | null = null

    onModalShown(): void {
        if (this.intervalId != null)
            clearInterval(this.intervalId)

        let counter = yesButtonTimeout;
        const handler = () => {
            if (--counter == 0) {
                this.yesButtonTitle = "Yes";
                clearInterval(this.intervalId!);
                this.intervalId = null;
            } else {
                this.yesButtonTitle = `Yes (${counter})`;
            }
        };

        this.yesButtonTitle = `Yes (${yesButtonTimeout})`;
        this.intervalId = setInterval(handler, 1000);
    }

    doDeleteAccount(): void {
        this.$store.dispatch("deleteCurrentUser").then(() => {
            this.$router.push("/");
            window.location.reload();
        });
    }
}
</script>

<style lang="scss">

</style>
<template>
    <div>
        <b-form-group label-for="display-name" label="Display name">
            <b-form-input id="display-name" v-model="currentDisplayName"></b-form-input>
        </b-form-group>

        <div class="update-button-wrapper">
            <b-button @click="changeDisplayName">Change</b-button>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {AuthenticationDetails} from "@/models/user";
    import {mapState} from "vuex";
    import ApiService from "@/services/Api.service";

    @Component({
        computed: mapState([
            "authenticationDetails"
        ])
    })
    export default class AccountSettings extends Vue {
        authenticationDetails!: AuthenticationDetails;
        currentDisplayName = "";

        mounted(): void {
            this.currentDisplayName = this.authenticationDetails.displayName;
        }

        changeDisplayName() {
            this.$store.dispatch("changeDisplayName", this.currentDisplayName)
                .catch(error => {
                    console.error(error);
                });
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
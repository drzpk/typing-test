<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card title="Test definitions" sub-title="Test management." v-loading-overlay="testDefinitionsLoading">
                        <TestDefinitions/>
                    </b-card>
                    <b-card title="Word lists" sub-title="Management of the word lists that are used in tests." v-loading-overlay="wordListsLoading">
                        <WordLists/>
                    </b-card>
                    <b-card title="Users" sub-title="Accounts registered in the application." v-loading-overlay="usersLoading">
                        <Users/>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import WordLists from "@/views/settings/admin/word/WordLists.vue";
import TestDefinitions from "@/views/settings/admin/test/TestDefinitions.vue";
import Users from "@/views/settings/admin/user/Users.vue";

@Component({
        components: {Users, TestDefinitions, WordLists}
    })
    export default class AdminSettings extends Vue {

        get testDefinitionsLoading(): boolean {
            return this.$store.getters.hasPendingRequest("loadTestDefinitions");
        }

        get wordListsLoading(): boolean {
            return this.$store.getters.hasPendingRequest("loadWordLists");
        }

        get usersLoading(): boolean {
            return this.$store.getters.hasPendingRequest("loadUsers");
        }
    }
</script>

<style lang="scss" scoped>
</style>
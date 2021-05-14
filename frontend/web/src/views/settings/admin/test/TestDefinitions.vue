<template>
    <div>
        <b-table v-if="testDefinitions && testDefinitions.length > 0" :items="testDefinitions" :fields="fields" @row-clicked="clickListItem" id="test-definitions-table">

        </b-table>
        <p v-else>There are no test definitions available. Add one using the button below.</p>

        <b-button @click="$router.push('/settings/admin/test-definitions/new')">Add a test definition</b-button>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {WordList} from "@/models/words";

    @Component({
        computed: mapGetters([
            "testDefinitions"
        ])
    })
    export default class TestDefinitions extends Vue {
        testDefinitions!: Array<TestDefinitions>;
        fields = [
            {
                key: "id",
                label: "Identifier"
            },
            {
                key: "name"
            },
            {
                key: "duration"
            },
            {
                key: "isActive"
            }
        ];

        mounted(): void {
            this.$store.dispatch("reloadTestDefinitions");
        }

        clickListItem(clicked: WordList): void {
            this.$router.push("/settings/admin/test-definitions/" + clicked.id.toString())
        }
    }
</script>

<style lang="scss">
    table#word-lists-table tr {
        cursor: pointer;
    }
</style>
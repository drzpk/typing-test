<template>
    <div>
        <b-table v-if="wordLists.length > 0" :items="wordLists" :fields="fields" @row-clicked="clickListItem" id="word-lists-table">

        </b-table>
        <p v-else>There are no word lists available. Add one using the button below.</p>

        <b-button @click="$router.push('/settings/admin/word-lists/new')">Add a list</b-button>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {WordList} from "@/models/words";

    @Component({
        computed: mapGetters([
            "wordLists"
        ])
    })
    export default class WordLists extends Vue {
        wordLists!: Array<WordList>;
        fields = [
            {
                key: "id",
                label: "Identifier"
            },
            {
                key: "name"
            },
            {
                key: "language"
            }
        ];

        clickListItem(clicked: WordList): void {
            this.$router.push("/settings/admin/word-lists/" + clicked.id.toString())
        }
    }
</script>

<style lang="scss">
    table#word-lists-table tr {
        cursor: pointer;
    }
</style>
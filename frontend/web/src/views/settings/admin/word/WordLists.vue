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
import {WordListModel, WordListType} from "@/models/words";

@Component({
        computed: mapGetters([
            "wordLists"
        ])
    })
    export default class WordLists extends Vue {
        wordLists!: Array<WordListModel>;
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
            },
            {
                key: "type",
                formatter: (type: WordListType) => type.substr(0, 1) + type.substr(1).toLowerCase()
            }
        ];

        mounted(): void {
            this.$store.dispatch("reloadWordLists");
        }

        clickListItem(clicked: WordListModel): void {
            this.$router.push("/settings/admin/word-lists/" + clicked.id.toString())
        }
    }
</script>

<style lang="scss">
    table#word-lists-table tr {
        cursor: pointer;
    }
</style>
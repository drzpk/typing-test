<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card title="Word lists" sub-title="Management of the word lists available in test.">
                        <WordLists/>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import WordLists from "@/views/settings/admin/word/WordLists.vue";

    @Component({
        components: {WordLists}
    })
    export default class AdminSettings extends Vue {
        mounted(): void {
            this.$store.dispatch("reloadWordLists");
        }
    }
</script>

<style lang="scss">

</style>
<template>
    <!--suppress HtmlUnknownBooleanAttribute -->
    <b-container fluid style="margin-top: 1em">
        <b-row>
            <b-col offset="3" cols="6">
                <TestSelection/>
            </b-col>
        </b-row>
        <b-row>
            <b-col offset="3" cols="6">
                <TestPanel/>
            </b-col>
        </b-row>
        <b-row>
            <b-col offset="3" cols="6">
                <TestSummary/>
            </b-col>
        </b-row>
        <b-row>
            <b-col offset="3" cols="6">
                <AnonymousUserMessage/>
            </b-col>
        </b-row>
        <b-row>
            <b-col offset="3" cols="6">
                <TestBestResults/>
            </b-col>
        </b-row>
    </b-container>
</template>

<script lang="ts">
import {Component, Vue, Watch} from "vue-property-decorator";
import TestPanel from "@/views/typing/test/TestPanel.vue";
import TestSelection from "@/views/typing/TestSelection.vue";
import {TestDefinitionModel} from "@/models/test-definition";
import {mapGetters} from "vuex";
import TestTimer from "@/views/typing/test/TestTimer.vue";
import TestSummary from "@/views/typing/TestSummary.vue";
import TestBestResults from "@/views/typing/TestBestResults.vue";
import AnonymousUserMessage from "@/views/typing/AnonymousUserMessage.vue";

@Component({
        components: {AnonymousUserMessage, TestBestResults, TestSummary, TestTimer, TestSelection, TestPanel},
        computed: {
            ...mapGetters(["activeUserTestDefinition"])
        }
    })
    export default class TypingPage extends Vue {
        activeUserTestDefinition!: TestDefinitionModel | undefined;

        @Watch("activeUserTestDefinition")
        onActiveTestDefinitionChanged(): void {
            if (this.activeUserTestDefinition != null)
                this.$store.dispatch("createTest");
        }
    }
</script>

<style lang="scss">

</style>
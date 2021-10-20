<template>
    <div>
        <b-form-textarea
            id="word-list-text"
            v-model="text"
            @input="updateState"
            rows="10"
        ></b-form-textarea>
        <b-form-invalid-feedback :force-show="hasError">Text cannot contain the '|' character.</b-form-invalid-feedback>

        <p>Words: {{ wordCount }} &nbsp;Characters: {{ characterCount }}</p>
        <b-alert variant="warning" :show="showShortTextWarning">
            The text is very short and won't be accepted even by short tests (1 minute).
            At least {{ minimumCharactersToHideShortTextWarning }} characters-long text is recommended.
        </b-alert>

        <b-button @click="saveText" :disabled="hasError">Save</b-button>
    </div>
</template>

<script lang="ts">
import {Component, Prop, Vue, Watch} from "vue-property-decorator";
import {WordListText} from "@/store/admin";
import WordService from "@/services/Word.service";
import {WordListModel} from "@/models/words";
import {mapGetters} from "vuex";

@Component({
    computed: mapGetters([
        "currentWordList"
    ])
})
export default class WordListFixedText extends Vue {
    @Prop()
    wordListId!: number;

    currentWordList!: WordListModel | null

    wordCount = 0;
    characterCount = 0;
    text = "";
    hasError = false;

    get showShortTextWarning(): boolean {
        return this.characterCount < this.minimumCharactersToHideShortTextWarning;
    }

    get minimumCharactersToHideShortTextWarning(): number {
        return WordService.getFixedTextCharacterLengthWarningThreshold();
    }

    @Watch("currentWordList")
    onWordListUpdated() {
        this.updateText();
    }

    mounted(): void {
        this.updateText();
        this.updateState();
    }

    private updateText(): void {
        if (this.currentWordList !== null)
            this.text = this.currentWordList.text;
    }

    private updateState(): void {
        this.wordCount = this.text.trim().split(/\s+/).length;
        this.characterCount = this.text.length;
        this.hasError = this.text.indexOf("|") > -1;
    }

    private saveText(): void {
        const wordListText: WordListText = {
            id: this.wordListId,
            text: this.text
        }
        this.$store.dispatch("setWordListText", wordListText);
    }
}
</script>

<style lang="scss">

</style>
<template>
    <div id="text-display-container">
        <div id="text-display" class="test-panel-font">
        <span class="word" v-for="word in words" :key="word.no"
              :class="['word-' + word.no, {good: word.good === true, bad: word.good === false, highlight: word.current === true}]">{{ word.word }}</span>
        </div>
    </div>
</template>

<script lang="ts">
import {Component, Vue, Watch} from "vue-property-decorator";
import {TestModel} from "@/models/tests";
import {mapGetters} from "vuex";
import {WordChangeEvent} from "@/models/events";
import WordService from "@/services/Word.service";

@Component({
    computed: {
        ...mapGetters(["activeTest"])
    }
})
export default class TextDisplay extends Vue {
    activeTest!: TestModel | undefined;

    words: Array<Word> = [];
    private currentWordNo = 0;
    private textDisplayElement!: HTMLDivElement | undefined;
    private currentOffset = 0;

    mounted(): void {
        this.$root.$on(WordChangeEvent.NAME, this.onUserWordInput);
        this.textDisplayElement = document.getElementById("text-display") as HTMLDivElement;
        this.updateWords();
    }

    destroyed(): void {
        this.$root.$off(WordChangeEvent.NAME, this.onUserWordInput);
    }

    @Watch("activeTest")
    updateWords(): void {
        if (this.activeTest) {
            this.words = this.createWordsFromTest();
        } else {
            this.words = TextDisplay.generateRandomWords();
        }

        this.currentWordNo = 0;
        this.words[0].current = true;
        this.resetDisplayOffset();
    }

    private onUserWordInput(event: WordChangeEvent): void {
        const currentWord = this.words[this.currentWordNo];
        if (!currentWord)
            return;

        const match = WordService.wordsMatch(currentWord.word, event.word, event.complete);
        if (event.complete)
            currentWord.good = match;
        else
            currentWord.good = !match ? false : undefined; // For current word display only misspellings

        if (event.complete) {
            currentWord.current = false;
            this.currentWordNo++;
            if (this.words[this.currentWordNo]) {
                this.words[this.currentWordNo].current = true;
                this.updateDisplayOffset(this.currentWordNo - 1, this.currentWordNo);
            }
        }

        // data:
        // currentWord

    }

    private resetDisplayOffset(): void {
        this.currentOffset = 0;
        if (this.textDisplayElement != null)
            this.textDisplayElement.style.top = "0";
    }

    private updateDisplayOffset(previousWordNo: number, currentWordNo: number): void {
        if (!this.textDisplayElement)
            return;

        const previousRect = this.$el.getElementsByClassName("word-" + previousWordNo)[0].getBoundingClientRect();
        const currentRect = this.$el.getElementsByClassName("word-" + currentWordNo)[0].getBoundingClientRect();

        const diff = currentRect.top - previousRect.top;
        if (diff == 0)
            return;

        this.currentOffset += diff;
        this.textDisplayElement.style.top = `${-this.currentOffset}px`;
    }

    private createWordsFromTest(): Array<Word> {
        const words: Array<Word> = [];
        const split = this.activeTest!.selectedWords.split("|");
        for (let i = 0; i < split.length; i++) {
            const word = new Word(i, split[i]);
            words.push(word);
        }
        return words;
    }

    private static generateRandomWords(): Array<Word> {
        return WordService.generateRandomWords(30)
            .map((wordString: string, index: number) => new Word(index, wordString));
    }
}

class Word {
    no: number;
    word: string;
    good: boolean | undefined = undefined;
    current: boolean | undefined = undefined;

    constructor(no: number, word: string) {
        this.no = no;
        this.word = word;
    }
}
</script>

<style lang="scss" scoped>
#text-display-container {
    position: relative;
    border: 1px solid darkblue;
    border-radius: 6px;

    height: 7.6em;
    overflow: hidden;
}

#text-display {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    padding: 0.2em;
}

.word {
    color: black;
    padding: 0.3em;
    line-height: 1em;

    &.good {
        color: green;
    }

    &.bad {
        color: red;
    }

    &.highlight {
        background-color: #d7d7d7;
        border-radius: 6px;

        &.bad {
            color: black;
            background-color: red;
        }
    }
}
</style>
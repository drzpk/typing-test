<template>
    <div id="text-display-container">
        <div id="text-display" class="test-panel-font">
        <span class="word" v-for="word in words" :key="word.no"
              :class="['word-' + word.no, {good: word.good === true, bad: word.good === false, highlight: word.current === true}]">{{word.word}}</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {TestModel} from "@/models/tests";
    import {mapGetters} from "vuex";
    import {WordChangeEvent} from "@/models/events";

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

            const match = TextDisplay.wordsMatch(currentWord.word, event.word, event.complete);
            if (event.complete)
                currentWord.good = match;
            else
                currentWord.good = !match ? false : undefined; // For current word display only misspellings

            if (event.complete) {
                currentWord.current = false;
                this.currentWordNo++;
                this.words[this.currentWordNo].current = true;
                this.updateDisplayOffset(this.currentWordNo - 1, this.currentWordNo);
            }
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

        private static wordsMatch(target: string, current: string, matchWhole: boolean): boolean {
            const length = matchWhole ? Math.max(target.length, current.length) : Math.min(target.length, current.length);
            for (let i = 0; i < length; i++) {
                const left = target.length > i ? target.charCodeAt(i) : null;
                const right = current.length > i ? current.charCodeAt(i) : null;

                if (left != right || left == null || right == null)
                    return false;
            }

            return true;
        }

        private static generateRandomWords(): Array<Word> {
            const words: Array<Word> = [];
            for (let i = 0; i < 30; i++) {
                const word = new Word(i, TextDisplay.generateRandomWord());
                words.push(word);
            }

            return words;
        }

        private static generateRandomWord(): string {
            const asciiStart = "a".charCodeAt(0);
            const asciiEnd = "z".charCodeAt(0);
            const diff = asciiEnd - asciiStart;

            const characters = 2 + Math.floor(Math.random() * 14);
            let word = "";
            for (let i = 0; i < characters; i++) {
                const rand = Math.floor(Math.random() * diff);
                const char = String.fromCharCode(asciiStart + rand);
                word += char;
            }

            return word;
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
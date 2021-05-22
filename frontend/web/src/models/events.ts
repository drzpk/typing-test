export class WordChangeEvent {
    static NAME = "word-change-event";

    word: string;
    complete: boolean;

    constructor(word: string, complete: boolean) {
        this.word = word;
        this.complete = complete;
    }
}
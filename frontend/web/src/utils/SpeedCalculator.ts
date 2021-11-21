import WordService from "@/services/Word.service";

export default class SpeedCalculator {
    selectedWords: string[] = [];
    enteredWords: string[] = [];

    private readonly testStartTime: number;
    private readonly charactersPerWord: number;

    constructor(testStartTime: Date, charactersPerWord: number) {
        this.testStartTime = testStartTime.getTime();
        this.charactersPerWord = charactersPerWord;
    }

    calculateSpeed(): number {
        const testDurationMinutes = (new Date().getTime() - this.testStartTime) / 1000 / 60;
        return this.getTotalCorrectKeyStrokes() / testDurationMinutes / WordService.getCharactersPerWord();
    }

    private getTotalCorrectKeyStrokes(): number {
        let correctKeyStrokes = 0;

        const len = Math.max(this.selectedWords.length, this.enteredWords.length);
        for (let i = 0; i < len; i++) {
            correctKeyStrokes += SpeedCalculator.getCorrectKeyStrokes(this.selectedWords[i] || "", this.enteredWords[i] || "");
        }

        return correctKeyStrokes;
    }

    private static getCorrectKeyStrokes(selectedWord: string, enteredWord: string): number {
        let correctKeyStrokes = 0;

        const len = Math.max(selectedWord.length, enteredWord.length);
        for (let i = 0; i < len; i++) {
            const selectedChar = selectedWord[i];
            const enteredChar = enteredWord[i];

            if (selectedChar == enteredChar)
                correctKeyStrokes++;
        }

        return correctKeyStrokes;
    }
}
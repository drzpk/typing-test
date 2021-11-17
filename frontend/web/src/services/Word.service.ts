import {WordListModel} from "@/models/words";

class WordService {
    wordsMatch(target: string, current: string, matchWhole: boolean): boolean {
        if (matchWhole)
            return current === target;
        else
            return target.startsWith(current);
    }

    generateRandomWords(size: number): Array<string> {
        const words: string[] = [];
        for (let i = 0; i < size; i++) {
            words.push(WordService.generateRandomWord());
        }

        return words;
    }

    countWords(wordList: WordListModel): number {
        const text = wordList.text;
        const split = text?.split(/\s+/);
        return split!.length;
    }

    getFixedTextCharacterLengthWarningThreshold(): number {
        // return maximum_theoretical_wpm * characters_per_word;
        return 250 * 5;
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

export default new WordService();
class WordService {
    wordsMatch(target: string, current: string, matchWhole: boolean): boolean {
        const length = matchWhole ? Math.max(target.length, current.length) : Math.min(target.length, current.length);
        for (let i = 0; i < length; i++) {
            const left = target.length > i ? target.charCodeAt(i) : null;
            const right = current.length > i ? current.charCodeAt(i) : null;

            if (left != right || left == null || right == null)
                return false;
        }

        return true;
    }

    generateRandomWords(size: number): Array<string> {
        const words: string[] = [];
        for (let i = 0; i < size; i++) {
            words.push(WordService.generateRandomWord());
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

export default new WordService();
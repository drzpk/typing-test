import WordService from "@/services/Word.service";

describe('Word.service.ts - wordsMatch', () => {
    test('should match partial word', () => {
        const match = WordService.wordsMatch("longWord", "longW", false);
        expect(match).toBe(true);
    });

    test('should match whole word', () => {
        const match = WordService.wordsMatch("abcdef", "abcdef", true);
        expect(match).toBe(true);
    });

    test('should not match whole different words', () => {
        const match = WordService.wordsMatch("abcdef", "abxdef", true);
        expect(match).toBe(false);
    });

    test('should not match shorter incorrect word', () => {
        const match = WordService.wordsMatch("thinking", "thingi", false);
        expect(match).toBe(false);
    });

    test('should not match longer word', () => {
        const match = WordService.wordsMatch("complete", "completeness", false);
        expect(match).toBe(false);
    });

    test('should not match misspelled word', () => {
        const match = WordService.wordsMatch("correct", "correst", false);
        expect(match).toBe(false);
    });
});
import SpeedCalculator from "@/utils/SpeedCalculator";


describe('SpeedCalculator', () => {
    test('should calculate speed', () => {
        const selectedWords = "good0|good1|good2|good3|good4|good5|good6|good7|good8|good9|long_good_A|long_good_B|long_good_C".split("|");
        const enteredWords = "good0|bad|good2|good3|good4|good5|bad|good7|bad|good9|long_good_A|long_XXX_B|long_good_C".split("|");

        const testDurationSeconds = 5;
        const testStartDate = new Date(new Date().getTime() - testDurationSeconds * 1000);

        //       0   1   2   3   4   5   6   7   8   9   10   11   12
        // good: 5 + 0 + 5 + 5 + 5 + 5 + 0 + 5 + 0 + 5 + 11 +  5 + 11
        const goodStrokes = 5 + 5 + 5 + 5 + 5 + 5 + 5 + 11 +  5 + 11;
        const durationMinutes = testDurationSeconds / 60;
        const charactersPerWord = 5;

        const calculator = new SpeedCalculator(testStartDate, charactersPerWord);
        calculator.selectedWords = selectedWords;
        calculator.enteredWords = enteredWords;

        const result = calculator.calculateSpeed();
        expect(result).toBeCloseTo(goodStrokes / durationMinutes / charactersPerWord, 2);
    });
});
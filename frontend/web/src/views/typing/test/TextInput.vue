import {TestStateModel} from "@/models/tests";
<template>
    <div id="text-input" class="test-panel-font">
        <!--suppress HtmlFormInputWithoutLabel -->
        <input id="text-input-input" type="text" :disabled="inputDisabled" :placeholder="placeholder"
               @input="onInputChanged">
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {TestStateModel} from "@/models/tests";
    import {WordChangeEvent} from "@/models/events";

    @Component({
        computed: {
            ...mapGetters(["testState", "activeTestStarted"])
        }
    })
    export default class TextInput extends Vue {
        testState!: TestStateModel | undefined;
        activeTestStarted!: boolean;

        get inputDisabled(): boolean {
            return this.testState !== TestStateModel.STARTED && this.testState !== TestStateModel.CREATED
        }

        get placeholder(): string {
            if (this.testState == TestStateModel.CREATED)
                return "Start typing to start the test.";
            else
                return "";
        }

        @Watch("testState")
        onTestStateChanged(): void {
            if (this.testState == TestStateModel.CREATED)
                (document.getElementById("text-input-input") as HTMLInputElement).value = "";
        }

        onInputChanged(event: InputEvent): void {
            this.startTestIfNecessary();

            const element = event.target as HTMLInputElement;
            const value = element.value || "";

            const trimmedInput = value.trim();
            const endsWithSpace = value.length > 0 ? value.charAt(value.length - 1) === " " : false;
            const containsWord = trimmedInput.length > 0;


            if (endsWithSpace || !containsWord)
                element.value = "";

            const completeWord = endsWithSpace && containsWord;
            const wordChangeEvent = new WordChangeEvent(trimmedInput, completeWord);
            this.$root.$emit(WordChangeEvent.NAME, wordChangeEvent);

            if (completeWord)
                this.$store.commit("addEnteredWord", trimmedInput);
            if (event.type === "deleteContentBackward")
                this.$store.commit("incrementBackspaceCount");
        }

        private startTestIfNecessary(): void {
            if (!this.activeTestStarted && this.testState == TestStateModel.CREATED)
                this.$store.dispatch("startTest");
        }
    }
</script>

<style lang="scss" scoped>
    #text-input {
        margin-top: 1em;
        padding: 0.08em;
        background-color: #a0a0a0;
    }

    input {
        width: 60%;
    }
</style>
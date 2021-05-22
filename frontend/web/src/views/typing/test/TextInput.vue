<template>
    <div id="text-input" class="test-panel-font">
        <!--suppress HtmlFormInputWithoutLabel -->
        <input type="text" :disabled="inputDisabled" @input="onInputChanged">
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {TestStateModel} from "@/models/tests";
    import {WordChangeEvent} from "@/models/events";

    @Component({
        computed: {
            ...mapGetters(["testState"])
        }
    })
    export default class TextInput extends Vue {
        testState!: TestStateModel | undefined;

        get inputDisabled(): boolean {
            return this.testState !== TestStateModel.STARTED && this.testState !== TestStateModel.CREATED
        }

        onInputChanged(event: InputEvent): void {
            const element = event.target as HTMLInputElement;
            const value = element.value || "";

            const trimmedInput = value.trim();
            const endsWithSpace = value.length > 0 ? value.charAt(value.length - 1) === " " : false;
            const containsWord = trimmedInput.length > 0;


            if (endsWithSpace || !containsWord)
                element.value = "";

            const wordChangeEvent = new WordChangeEvent(trimmedInput, endsWithSpace && containsWord);
            this.$root.$emit(WordChangeEvent.NAME, wordChangeEvent);
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
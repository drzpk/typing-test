<!--suppress HtmlUnknownBooleanAttribute -->
<template>
    <div id="test-summary-container" v-if="testResult != null">
        <b-container fluid>
            <b-row>
                <b-col offset="2" cols="8">
                    <b-container fluid id="test-summary">
                        <b-row>
                            <b-col cols="12" class="header">
                                Test summary
                            </b-col>
                        </b-row>
                        <b-row>
                            <b-col cols="5">
                                <div class="wpm">
                                    <span class="value">{{ wordsPerMinute }} WPM</span>
                                    <span class="label">Speed</span>
                                </div>
                                <div class="h-divider"></div>
                                <div class="accuracy">
                                    <span class="value">{{ accuracy }}%</span>
                                    <span class="label">Accuracy</span>
                                </div>
                            </b-col>
                            <b-col cols="7" class="stats-container">
                                <div class="stats-list">
                                    <div class="stats-content">
                                        <div>Correct words: <span
                                            class="value">{{ testResult.correctWords }}</span></div>
                                        <div>Incorrect words: <span
                                            class="value negative">{{ testResult.incorrectWords }}</span></div>
                                        <div>Correct keystrokes: <span
                                            class="value">{{ testResult.correctKeystrokes }}</span></div>
                                        <div>Incorrect keystrokes: <span
                                            class="value negative">{{ testResult.incorrectKeystrokes }}</span></div>
                                    </div>
                                </div>
                            </b-col>
                        </b-row>
                    </b-container>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {TestResultModel} from "@/models/tests";
import {Component, Vue} from "vue-property-decorator";
import {mapGetters} from "vuex";

@Component({
    computed: {
        ...mapGetters(["testResult"])
    }
})
export default class TestSummary extends Vue {
    testResult!: TestResultModel | null;

    get wordsPerMinute(): string {
        if (!this.testResult)
            return "";

        return Math.floor(this.testResult.wordsPerMinute).toString();
    }

    get accuracy(): string {
        if (!this.testResult)
            return "";

        const accuracy = Math.floor(this.testResult.accuracy * 1000);
        return (accuracy / 10).toString();
    }
}
</script>

<style lang="scss" scoped>
$border-color: #3f4c98;

#test-summary {
    margin-top: 2em;
    border: 1px solid $border-color;
    border-radius: 5px;

    .header {
        padding: 0.5em 0.9em;
        font-size: 1.1em;
        font-weight: bold;
        color: white;
        background-color: #5581f1;
    }

    .wpm, .accuracy {
        padding: 0.8em 0;

        .value {
            display: block;
            font-size: 2em;
            font-weight: bold;
            color: #27ae49;
            text-align: center;
        }

        .label {
            display: block;
            font-size: 0.8em;
            text-align: center;
            color: gray;
        }
    }

    .h-divider {
        height: 1px;
        margin: 0 1.5em;
        background-color: $border-color;
    }

    .stats-container {
        border-left: 1px solid $border-color;
    }

    .stats-list {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;

        .stats-content {
            text-align: right;

            > div {
                margin: 0.4em 0;

                .value {
                    font-weight: bold;

                    &.negative {
                        color: red;
                    }
                }
            }
        }
    }
}
</style>
<template>
    <div id="best-test-results" v-show="testBestResults.length > 0">
        <h3>Best test results</h3>

        <p><b-icon icon="question-circle-fill" shift-v="8" @click="showScoreboardHelp"></b-icon></p>

        <b-table striped hover :items="testBestResults" :fields="fields">
            <!--suppress HtmlUnknownAttribute -->
            <template #cell(no)="data">
                #{{ data.index + 1 }}
            </template>
        </b-table>

        <b-modal id="scoreboard-help-modal" title="Scoreboard help" ok-only>
            <p>Below are some information regarding details of some of the scoreboard's columns.</p>

            <h6>Speed</h6>
            <p>
                Speed is displayed in a unit called <strong>WPM</strong> (words per minute). 1 WPM is equal to
                writing one 5-character-long word in one minute. Only correct keystrokes are considered for calculating
                the speed.
            </p>

            <h6>Accuracy</h6>
            <p>
                Accuracy is a measure of how close a text was rewritten by the user. A 100% accuracy means that no
                mistakes have been made, and every one of them lowers the overall result. The accuracy is defined as
                the ratio of correct keystrokes compared to the total number of keystrokes. In addition, using
                the backspace key is equal to 0.5 incorrect keystroke.
            </p>

            <h6>Score</h6>
            <p>
                Score is a unit by which all results are compared. It's derived from the speed and the accuracy
                using the following formula:
            </p>
            <pre>
effectiveSpeed = speed * accuracy
testTimeBonus = testTimeSeconds / 60f * accuracy * 100 / 3
SCORE = floor((effectiveSpeed + testTimeBonus) * 10 + 0.5f)
            </pre>
        </b-modal>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import {mapGetters} from "vuex";
import {TestBestResultModel} from "@/models/tests";
import DateService from "@/services/Date.service";
import {TestDefinitionModel} from "@/models/test-definition";
import {formatDuration} from "@/utils/time-utils";
import {BvTableFieldArray} from "bootstrap-vue/esm/components/table";

@Component({
    computed: {
        ...mapGetters(["testBestResults", "activeUserTestDefinition"])
    }
})
export default class TestBestResults extends Vue {
    testBestResults!: TestBestResultModel[];
    activeUserTestDefinition!: TestDefinitionModel | null;

    get fields(): BvTableFieldArray {
        const fields = [
            {
                key: "no",
                label: "Position"
            },
            {
                key: "userDisplayName",
                label: "User"
            },
            {
                key: "testCreatedAt",
                label: "Date",
                formatter: (date: Date) => DateService.formatDateToString(date)
            },
            {
                key: "speed",
                label: "Speed",
                formatter: (value: number) => `${Math.floor(value + 0.5)} WPM`
            },
            {
                key: "accuracy",
                formatter: (accuracy: number) => `${Math.floor(accuracy * 100 + 0.5)}%`
            },
            {
                key: "durationSeconds",
                label: "Duration",
                formatter: (duration: number) => formatDuration(duration)
            },
            {
                key: "score"
            }
        ];

        if (this.activeUserTestDefinition?.duration != null)
            fields.splice(fields.length - 2, 1);

        return fields;
    }

    showScoreboardHelp(): void {
        this.$bvModal.show("scoreboard-help-modal");
    }
}
</script>

<style lang="scss" scoped>
h3 {
    display: inline-block;
}

p {
    display: inline-block;
    margin-left: 0.4em;
}

.b-icon {
    cursor: pointer;
}

#best-test-results {
    margin-top: 3em;
}
</style>
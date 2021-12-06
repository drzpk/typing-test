<template>
    <b-modal id="scoreboard-help-modal" title="Scoreboard help" ok-only @ok="onDismissed">
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
</template>

<script lang="ts">
import {Component, Vue, Watch} from "vue-property-decorator";
import {mapGetters} from "vuex";

@Component({
    computed: mapGetters(["showTestBestResultsHelp"])
})
export default class TestBestResultsHelp extends Vue {
    showTestBestResultsHelp!: boolean;

    @Watch("showTestBestResultsHelp")
    onShowStateChanged(): void {
        if (this.showTestBestResultsHelp)
            this.$bvModal.show("scoreboard-help-modal");
    }

    onDismissed(): void {
        this.$store.commit("hideBestResultsHelp");
    }
}
</script>

<style lang="scss">

</style>
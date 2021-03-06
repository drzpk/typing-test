<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card title="Test statistics" sub-title="View the statistics of your finished tests."
                            v-loading-overlay="pendingRequest">
                        <b-form-group label-for="test-definition-select" label="Test" label-cols="3">
                            <b-form-select id="test-definition-select" :options="testDefinitionOptions"
                                           v-model="selectedTestDefinitionId"/>
                        </b-form-group>

                        <div v-if="showStats">
                            <table>
                                <tr>
                                    <td>Finished tests:</td>
                                    <td>{{ testStats.finishedTests }}</td>
                                </tr>
                                <tr>
                                    <td>Average speed:</td>
                                    <td>{{ Math.floor(testStats.userStats.averageSpeed * 10) / 10 }} WPM</td>
                                </tr>
                                <tr>
                                    <td>Average accuracy:</td>
                                    <td>{{ Math.floor(testStats.userStats.averageAccuracy * 1000) / 10 }}%</td>
                                </tr>
                                <tr>
                                    <td><br></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td>Global average speed:</td>
                                    <td>{{ Math.floor(testStats.globalStats.averageSpeed * 10) / 10 }} WPM</td>

                                </tr>
                                <tr>
                                    <td>Global average accuracy:</td>
                                    <td>{{ Math.floor(testStats.globalStats.averageAccuracy * 1000) / 10 }}%</td>

                                </tr>
                            </table>
                        </div>

                        <br>
                        <hr>
                        <br>

                        <b-alert variant="info" :show="!showStats">
                            Select a test to display statistics.
                        </b-alert>

                        <b-alert variant="danger" :show="showNotEnoughTestsWarning">
                            Not enough data to plot charts. Finish selected test at least 3 times to display them.
                        </b-alert>

                        <div v-if="showCharts">
                            <h5>Speed</h5>
                            <div class="chart">
                                <ProgressChart :data="testStats.speedValues" value-label="Speed [WPM]"
                                               color="rgba(52, 135, 187, 0.48)"/>
                            </div>

                            <br>
                            <h5>Accuracy</h5>
                            <div class="chart">
                                <ProgressChart :data="testStats.accuracyValues" value-label="Accuracy [%]"
                                               value-type="percentage" color="rgba(227, 130, 19, 0.48)"/>
                            </div>
                        </div>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {Component, Vue, Watch} from "vue-property-decorator";
import {TestDefinitionModel} from "@/models/test-definition";
import {mapGetters} from "vuex";
import {SelectOption} from "@/models/common";
import {TestStatsModel} from "@/models/test-stats";
import ProgressChart from "@/views/settings/stats/ProgressChart.vue";

@Component({
    components: {ProgressChart},
    computed: {
        ...mapGetters(["testDefinitionsWithStats", "testStats", "pendingRequest"])
    }
})
export default class TestStats extends Vue {
    testDefinitionsWithStats!: Array<TestDefinitionModel> | null;
    testStats!: TestStatsModel | null;
    pendingRequest!: boolean;

    testDefinitionOptions: Array<SelectOption> = [];
    selectedTestDefinitionId: number | null = null;

    get showNotEnoughTestsWarning(): boolean {
        return this.testStats != null && this.testStats.speedValues.length < 3;
    }

    get showStats(): boolean {
        return this.testStats != null;
    }

    get showCharts(): boolean {
        return !this.showNotEnoughTestsWarning && this.testStats != null;
    }

    mounted(): void {
        this.$store.dispatch("refreshTestDefinitionWithStats");
        this.refreshTestDefinitionOptions();
    }

    @Watch("testDefinitionsWithStats")
    onTestDefinitionsChanged(): void {
        this.refreshTestDefinitionOptions();
    }

    @Watch("selectedTestDefinitionId")
    onTestDefinitionSelected(): void {
        if (this.selectedTestDefinitionId != null)
            this.$store.dispatch("refreshTestStats", this.selectedTestDefinitionId);
    }

    private refreshTestDefinitionOptions(): void {
        const options: Array<SelectOption> = [];

        if (this.testDefinitionsWithStats != null && this.testDefinitionsWithStats.length > 0) {
            for (let i = 0; i < this.testDefinitionsWithStats.length; i++) {
                const item = this.testDefinitionsWithStats[i];
                options.push({value: item.id, text: item.name});
            }
        } else {
            options.push({value: null, text: "No test is available"});
        }

        this.testDefinitionOptions = options;
    }
}
</script>

<style lang="scss" scoped>
.chart {
    height: 400px;
}
</style>
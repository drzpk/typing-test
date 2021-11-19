<template>
    <div id="best-test-results" v-show="testBestResults.length > 0">
        <h3>Best test results</h3>
        <b-table striped hover :items="testBestResults" :fields="fields">
            <!--suppress HtmlUnknownAttribute -->
            <template #cell(no)="data">
                #{{ data.index + 1 }}
            </template>
        </b-table>
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
}
</script>

<style lang="scss">
#best-test-results {
    margin-top: 3em;
}
</style>
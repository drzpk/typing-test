<template>
    <div id="best-test-results" v-show="testBestResults.length > 0">
        <h3>Best test results</h3>
        <b-table striped hover :items="testBestResults" :fields="fields">
            <!--suppress HtmlUnknownAttribute -->
            <template #cell(no)="data">
                #{{data.index + 1}}
            </template>
        </b-table>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {TestBestResultModel} from "@/models/tests";
    import DateService from "@/services/Date.service";

    @Component({
        computed: {
            ...mapGetters(["testBestResults"])
        }
    })
    export default class TestBestResults extends Vue {
        testBestResults!: TestBestResultModel[];

        fields = [
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
                key: "score"
            }
        ]
    }
</script>

<style lang="scss">
#best-test-results {
    margin-top: 3em;
}
</style>
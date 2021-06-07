<script lang="ts">
    import {Line} from "vue-chartjs";
    import {Component, Mixins, Prop, Watch} from "vue-property-decorator";
    import {ChartData, ChartOptions} from "chart.js";
    import {TimedDataModel} from "@/models/test-stats";

    @Component
    export default class ProgressChart extends Mixins(Line) {
        @Prop({required: true})
        readonly data!: Array<TimedDataModel<any>>;

        @Prop()
        readonly valueLabel!: string;

        @Prop({default: "number"})
        readonly valueType!: "number" | "percentage";

        @Prop()
        readonly color: string | undefined;

        private labels: Date[] = [];
        private values: number[] = [];

        mounted(): void {
            this.updateChart();
        }

        @Watch("data")
        onDataChanged(): void {
            this.updateChart();
        }

        private updateChart(): void {
            this.prepareData();
            const data: ChartData = {
                labels: this.labels,
                datasets: [
                    {
                        label: this.valueLabel,
                        data: this.values,
                        backgroundColor: this.color
                    }
                ],
            };

            const options: ChartOptions = {
                responsive: true,
                scales: {
                    xAxes: [
                        {
                            type: "time",
                            time: {
                                unit: "day"
                            },
                            distribution: "series"
                        }
                    ],
                    yAxes: [
                        {
                            ticks: {
                                beginAtZero: true,
                                callback: (value: number | string): string | number | null | undefined => {
                                    if (this.valueType == "percentage")
                                        return `${value}%`;
                                    else
                                        return value;
                                },
                                min: this.valueType == "percentage" ? 0 : undefined,
                                max: this.valueType == "percentage" ? 100 : undefined
                            },
                        }
                    ]
                },
                legend: {
                    display: false
                },
                maintainAspectRatio: false
            };

            this.renderChart(data, options);
        }

        private prepareData(): void {
            const labels: Array<Date> = [];
            const values: Array<number> = [];
            for (let i = 0; i < this.data.length; i++) {
                const current = this.data[i];
                labels.push(new Date(current.timestamp * 1000));

                let value: number;
                switch (this.valueType) {
                    case "number":
                        value = current.value;
                        break;
                    case "percentage":
                        value = Math.floor(current.value * 1000) / 10;
                        break;
                }
                values.push(value);
            }

            this.labels = labels;
            this.values = values;
        }
    }
</script>

<style lang="scss">

</style>
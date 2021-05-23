import {TestStateModel} from "@/models/tests";
<template>
    <div>
        {{additionalText}}
        <div id="test-timer">
            <font-awesome-icon icon="clock" inverse/>
            <span>{{currentTime}}</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {TestModel, TestStateModel} from "@/models/tests";
    import {formatDuration} from "@/utils/time-utils";
    import {TestDefinitionModel} from "@/models/test-definition";

    @Component({
        computed: {
            ...mapGetters([
                "activeUserTestDefinition",
                "activeTest",
                "activeTestStarted"
            ])
        }
    })
    export default class TestTimer extends Vue {
        activeUserTestDefinition!: TestDefinitionModel | undefined;
        activeTest!: TestModel | undefined;
        activeTestStarted!: boolean;

        currentTime = "0:00";
        additionalText = "";

        private intervalId: number | null = null;

        destroyed(): void {
            this.stopTimer();
        }

        @Watch("activeTest")
        onTestChanged(): void {
            this.refreshCounter();
        }

        @Watch("activeTest.state")
        onStateChanged(): void {
            this.refreshCounter();
        }

        private refreshCounter(): void {
            if (!this.activeTest) {
                this.stopTimer();
                return;
            }

            this.additionalText = "";
            this.stopTimer();

            if (this.activeTest.state === TestStateModel.CREATED)
                this.onTestCreated();
            else if (this.activeTestStarted)
                this.onTestStarted();
        }

        private onTestCreated(): void {
            const startDueTime = this.activeTest?.startDueTime;
            if (!startDueTime)
                return;

            this.additionalText = "Remaining time to start:";
            this.createTimer(startDueTime);
        }

        private onTestStarted(): void {
            const endTime = new Date().getTime() + this.activeUserTestDefinition!.duration * 1000;
            this.createTimer(new Date(endTime));
        }

        private createTimer(endTime: Date): void {
            let id: number;
            const handler = () => {
                const diff = Math.floor((endTime.getTime() - new Date().getTime()) / 1000);
                if  (diff >= 0)
                    this.setTime(diff);
                else
                    clearInterval(id);
            };

            handler();
            id = setInterval(handler, 1000);
            this.intervalId = id;
        }

        private stopTimer(): void {
            if (this.intervalId)
                clearInterval(this.intervalId);
            this.setTime(0);
        }

        private setTime(newTime: number): void {
            this.currentTime = formatDuration(newTime);
        }
    }
</script>

<style lang="scss" scoped>
    #test-timer {
        display: inline-block;
        background-color: #2c3e50;
        padding: 0.4em 0.7em;
        font-size: 1.2em;
        border-radius: 4px;

        > span {
            margin-left: 0.5em;
            color: white;
        }
    }
</style>

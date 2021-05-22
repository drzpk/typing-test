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

    @Component({
        computed: {
            ...mapGetters(["activeTest"])
        }
    })
    export default class TestTimer extends Vue {
        activeTest!: TestModel | undefined;

        currentTime = "0:00";
        additionalText = "";

        private intervalHandle: number | null = null;

        destroyed(): void {
            this.stopTimer();
        }

        @Watch("activeTest.state")
        onStateChanged(): void {
            if (!this.activeTest) {
                this.stopTimer();
                return;
            }

            this.additionalText = "";
            this.stopTimer();

            switch (this.activeTest.state) {
                case TestStateModel.CREATED:
                    this.onTestCreated();
                    break;
                case TestStateModel.STARTED:
                    this.onTestStarted();
                    break;
            }
        }

        private onTestCreated(): void {
            const startDueTime = this.activeTest?.startDueTime;
            if (!startDueTime)
                return;

            this.additionalText = "Remaining time to start:";
            this.intervalHandle = setInterval(() => {
                const diff = startDueTime.getTime() - new Date().getTime();
                this.setTime(Math.floor(diff / 1000));
            }, 1000);
        }

        private onTestStarted(): void {
            const endTime = new Date().getTime() + this.activeTest!.definition.duration * 1000;
            this.intervalHandle = setInterval(() => {
                const diff = endTime - new Date().getTime();
                this.setTime(Math.floor(diff / 1000));
            }, 1000);
        }

        private stopTimer(): void {
            if (this.intervalHandle)
                clearInterval(this.intervalHandle);
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

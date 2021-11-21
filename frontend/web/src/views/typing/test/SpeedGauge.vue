<template>
    <div id="container">
        <div id="gauge">
            <div id="gauge-value" :style="{width: `${gaugePercentageValue}%`}"></div>
            <div id="speed-values">
                <div v-for="mark in marks"
                     :key="mark.positionPercentage"
                     :style="{left: `${mark.positionPercentage}%`}">
                    <div class="text">{{ mark.text }}</div>
                    <div class="line" v-show="mark.showLine"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {Component, Vue, Watch} from "vue-property-decorator";
import {mapGetters} from "vuex";
import {TestModel, TestStateModel} from "@/models/tests";
import WordService from "@/services/Word.service";
import SpeedCalculator from "@/utils/SpeedCalculator";

const MIN_SPEED = 0;
const MAX_SPEED = 150;
const DISPLAY_STEP = 25;

class Mark {
    readonly text: string;
    readonly showLine: boolean;
    readonly positionPercentage: number;

    constructor(text: string, showLine: boolean, positionPercentage: number) {
        this.text = text;
        this.showLine = showLine;
        this.positionPercentage = positionPercentage;
    }
}

@Component({
    computed: mapGetters([
        "activeTest",
        "enteredWords"
    ])
})
export default class SpeedGauge extends Vue {
    activeTest!: TestModel | null;
    enteredWords!: string[];

    marks: Array<Mark> = [];
    gaugePercentageValue = 0;

    private speedCalculator: SpeedCalculator | null = null;
    private speedRefreshHandle = -1;

    mounted(): void {
        for (let speed = MIN_SPEED; speed <= MAX_SPEED; speed += DISPLAY_STEP) {
            const showLine = speed != MIN_SPEED && speed != MAX_SPEED;
            const percentage = Math.floor(speed / MAX_SPEED * 100);

            const mark = new Mark(speed.toString(), showLine, percentage);
            this.marks.push(mark);
        }
    }

    destroyed(): void {
        if (this.speedRefreshHandle > -1)
            clearInterval(this.speedRefreshHandle);
    }

    @Watch("activeTest")
    private onActiveTestChanged(): void {
        if (this.activeTest && this.activeTest.state == TestStateModel.STARTED && this.activeTest?.startedAt) {
            this.speedCalculator = new SpeedCalculator(this.activeTest?.startedAt, WordService.getCharactersPerWord());
            this.speedRefreshHandle = setInterval(this.refreshSpeed, 1000);
        } else {
            this.speedCalculator = null;
            clearInterval(this.speedRefreshHandle);
            this.speedRefreshHandle = -1;
            this.gaugePercentageValue = 0;
        }
    }

    private refreshSpeed(): void {
        if (this.speedCalculator == null)
            return;

        this.speedCalculator.selectedWords = this.activeTest?.selectedWords.split("|");
        this.speedCalculator.enteredWords = this.enteredWords;

        const speed = this.speedCalculator.calculateSpeed();
        this.gaugePercentageValue = Math.floor(speed / MAX_SPEED * 1000) / 10;
    }
}
</script>

<style lang="scss" scoped>
$gauge-height: 1em;
$gauge-color: #2c3e50;
$text-offset: $gauge-height + 0.5em;
$border-radius: 0.6em;

#container {
    display: flex;
    justify-content: center;
    margin-top: 1em + $text-offset;

    #gauge {
        position: relative;
        width: 50%;
        height: $gauge-height;
        border: 2px solid $gauge-color;
        box-sizing: content-box;
        border-radius: $border-radius;

        #gauge-value {
            position: absolute;
            height: $gauge-height;
            background-color: #1e7e34;
            border-radius: $border-radius;

            transition: width 0.3s ease-in-out;
        }

        #speed-values {
            position: relative;
            width: 100%;
            height: 100%;

            > div {
                display: inline-block;
                position: absolute;
                margin: 0;
                text-align: center;

                .text {
                    position: absolute;
                    top: -$text-offset;
                    transform: translateX(-50%);
                }

                .line {
                    position: absolute;
                    top: 0;
                    display: inline-block;
                    width: 1px;
                    height: $gauge-height;
                    background: $gauge-color;
                    margin: auto;
                }
            }
        }
    }
}
</style>
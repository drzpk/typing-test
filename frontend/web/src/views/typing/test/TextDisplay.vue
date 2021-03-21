<template>
    <div id="text-display" class="test-panel-font">
        <span class="word" v-for="i in wordCount" :key="i" :class="{good: i === 0, bad: i === 1 || i === 6, highlight: i === 4 || i === 6}">słowo{{i}}</span>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component({
        data: () => {
            return {
                wordCount: [...Array(20).keys()]
            }
        }
    })
    export default class TextDisplay extends Vue {
    }
</script>

<style lang="scss" scoped>
    #text-display {
        display: flex;
        flex-wrap: wrap;
        padding: 0.2em;

        border: 1px solid darkblue;
        border-radius: 6px;
    }

    .word {
        color: black;
        padding: 0.3em;
        line-height: 1em;

        &.good {
            color: green;
        }

        &.bad {
            color: red;
        }

        &.highlight {
            background-color: #d7d7d7;
            border-radius: 6px;

            &.bad {
                color: black;
                background-color: red;
            }
        }
    }
</style>
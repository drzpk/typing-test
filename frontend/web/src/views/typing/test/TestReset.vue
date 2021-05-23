<template>
    <div id="test-reset">
        <font-awesome-icon icon="sync-alt" inverse size="lg" @click="reset"/>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class TestReset extends Vue {

        reset(): void {
            this.$store.dispatch("resetTest");
        }
    }
</script>

<style lang="scss">
    #test-reset {
        display: flex;
        align-items: center;
        margin-left: 0.5em;
        padding: 0 0.8em;
        background-color: #5581f1;
        border-radius: 4px;

        cursor: pointer;
        transition: background-color 0.1s linear;

        &:hover {
            background-color: #4861c3;
        }
    }
</style>
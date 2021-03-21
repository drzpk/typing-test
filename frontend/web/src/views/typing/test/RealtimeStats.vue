<template>
    <div>
        <div id="remaining-time">
            <font-awesome-icon icon="clock" inverse/>
            <span>0:00</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class RealtimeStats extends Vue {

    }
</script>

<style lang="scss" scoped>
    #remaining-time {
        display: inline-block;
        background-color: #2c3e50;
        margin: 0.5em 0;

        font-size: 1.8em;

        > span {
            margin-left: 0.5em;
            color: white;
        }
    }
</style>

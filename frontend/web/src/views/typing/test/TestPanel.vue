<template>
    <div id="test-panel">
        <RealtimeStats/>
        <TextDisplay/>
        <TextInput/>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import TextDisplay from "@/views/typing/test/TextDisplay.vue";
    import TextInput from "@/views/typing/test/TextInput.vue";
    import RealtimeStats from "@/views/typing/test/RealtimeStats.vue";

    @Component({
        components: {
            RealtimeStats,
            TextInput,
            TextDisplay
        }
    })
    export default class TestPanel extends Vue {

    }
</script>

<style lang="scss">
    #test-panel {
        margin-top: 1.2em;
    }

    .test-panel-font {
        font-family: "Times New Roman", serif;
        font-size: 2em;
        font-weight: 500;
    }
</style>
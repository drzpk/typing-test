<template>
    <!--suppress HtmlUnknownBooleanAttribute -->
    <b-container fluid style="margin-top: 1em">
        <b-row>
            <b-col offset="3" cols="6">
                <TestSelection/>
                <TestPanel/>
            </b-col>
        </b-row>
    </b-container>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import TestPanel from "@/views/typing/test/TestPanel.vue";
    import TestSelection from "@/views/typing/TestSelection.vue";

    @Component({
        components: {TestSelection, TestPanel}
    })
    export default class TypingPage extends Vue {

    }
</script>

<style lang="scss">

</style>
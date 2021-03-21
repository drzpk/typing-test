<template>
    <!--suppress HtmlUnknownBooleanAttribute -->
    <b-container fluid>
        <b-row>
            <b-col offset="3" cols="6">
                <TestPanel/>
            </b-col>
        </b-row>
    </b-container>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import TestPanel from "@/views/typing/test/TestPanel.vue";
    @Component({
        components: {TestPanel}
    })
    export default class TypingPage extends Vue {

    }
</script>

<style lang="scss">

</style>
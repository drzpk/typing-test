<template>
    <div id="test-panel-container">
        <div id="test-panel" :class="{blurred: showOverlay}">
            <TextDisplay/>
            <TextInput/>
        </div>

        <div id="test-panel-overlay" v-if="showOverlay">
            <div id="backdrop"></div>
            <div id="text-container">
                <div id="text">
                    <div v-if="showTestLoadingOverlay">
                        <LoadingSpinner/>
                    </div>
                    <div v-else-if="showTestErrorOverlay" id="test-error-container">
                        <div>{{testError.message}}</div>
                    </div>
                    <span v-else-if="showSelectTestOverlay" class="info">Select a test to begin.</span>
                    <span v-else-if="showTestOverOverlay" class="info">Test is finished.</span>
                </div>
            </div>
        </div>
    </div>

</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import TextDisplay from "@/views/typing/test/TextDisplay.vue";
    import TextInput from "@/views/typing/test/TextInput.vue";
    import {mapGetters} from "vuex";
    import {ErrorCodeModel} from "@/models/error";
    import {TestModel, TestStateModel} from "@/models/tests";
    import LoadingSpinner from "@/views/shared/LoadingSpinner.vue";

    @Component({
        components: {
            LoadingSpinner,
            TextInput,
            TextDisplay
        },
        computed: {
            ...mapGetters([
                "isLoading",
                "activeTest",
                "testError"
            ])
        }
    })
    export default class TestPanel extends Vue {
        isLoading!: boolean;
        activeTest!: TestModel | undefined;
        testError!: ErrorCodeModel | null;

        get showOverlay(): boolean {
            return this.showTestLoadingOverlay || this.showTestErrorOverlay || this.showSelectTestOverlay || this.showTestOverOverlay;
        }

        get showTestLoadingOverlay(): boolean {
            return this.isLoading;
        }

        get showTestErrorOverlay(): boolean {
            return this.testError != null;
        }

        get showSelectTestOverlay(): boolean {
            return !this.testError && !this.activeTest;
        }

        get showTestOverOverlay(): boolean {
            return !this.testError && this.activeTest?.state == TestStateModel.FINISHED;
        }
    }
</script>

<style lang="scss">
    #test-panel-container {
        position: relative;
    }

    #test-panel {
        margin-top: 1.2em;

        &.blurred {
            filter: blur(5px);
        }
    }

    .test-panel-font {
        font-family: "Times New Roman", serif;
        font-size: 2em;
        font-weight: 500;
    }

    #test-panel-overlay {
        position: absolute;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        z-index: 100;

        #backdrop {
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            z-index: -1;

            filter: blur(2px);
            background-color: #efefef;
            opacity: 0.8;
        }

        #text-container {
            display: flex;
            height: 100%;
            justify-content: center;
            align-items: center;

            #text {
                .info {
                    font-size: 2em;
                }
            }
        }
    }

    #test-error-container {
        > div {
            font-size: 1.3em;
            color: red;
        }
    }
</style>
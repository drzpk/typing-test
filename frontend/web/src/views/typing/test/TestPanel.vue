<template>
    <div id="test-panel-container">
        <div id="test-panel">
            <RealtimeStats/>
            <TextDisplay/>
            <TextInput/>
        </div>

<!--        <div id="test-panel-overlay">-->
<!--            <div id="backdrop"></div>-->
<!--            <div id="text-container">-->
<!--                <div id="text">-->
<!--                    Choose a test.-->
<!--                </div>-->
<!--            </div>-->
<!--        </div>-->
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
    #test-panel-container {
        position: relative;
    }

    #test-panel {
        margin-top: 1.2em;
       // filter: blur(10px);
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

            filter: blur(10px);
            background-color: #efefef;
            opacity: 0.5;
        }

        #text-container {
            display: flex;
            height: 100%;
            justify-content: center;
            align-items: center;

            #text {
                font-size: 2em;
            }
        }
    }
</style>
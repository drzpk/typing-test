<template>
    <div>
        <span class="active-selection">
            <b-button variant="success" :disabled="!activeUserTestDefinition" :pressed.sync="showTestSelector">
                {{activeUserTestDefinition ? activeUserTestDefinition.name : "no test available"}}
                <font-awesome-icon
                        class="arrow-icon"
                        :class="{reversed: showTestSelector}"
                        style="margin-left: 0.5em"
                        icon="arrow-down"/>
            </b-button>
        </span>

        <b-collapse :visible="showTestSelector && userTestDefinitions">
            <div class="test-selector-panel">
                <span class="test-selector-item" v-for="definition in testing" :key="definition">
                    <span class="country"><country-flag
                            :country="mapLanguageToCountryCode(definition.wordList.language)" size="small"/></span>
                    <span class="name">{{definition.name}}</span>
                    <span class="duration">{{formatDuration(definition.duration)}}</span>
                </span>
            </div>
        </b-collapse>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {TestDefinitionModel} from "@/models/test-definition";
    import {mapGetters} from "vuex";
    import {formatDuration} from "@/utils/time-utils";

    @Component({
        computed: {
            ...mapGetters([
                "userTestDefinitions",
                "activeUserTestDefinition"
            ])
        }
    })
    export default class TestSelection extends Vue {
        userTestDefinitions!: Array<TestDefinitionModel>;
        activeUserTestDefinition!: TestDefinitionModel | undefined;

        showTestSelector = true;

        get testing(): Array<TestDefinitionModel> { // todo: remove this after the tests
            if (!this.userTestDefinitions)
                return [];

            const double: Array<TestDefinitionModel> = [];
            double.push(...this.userTestDefinitions, ...this.userTestDefinitions, ...this.userTestDefinitions);
            return double;
        }

        mounted(): void {
            this.$store.dispatch("refreshUserTestDefinitions");
        }

        formatDuration(duration: number): string {
            return formatDuration(duration);
        }

        mapLanguageToCountryCode(lang: string): string {
            if (lang === "en") {
                return "us";
            } else {
                return lang;
            }
        }
    }
</script>

<style lang="scss" scoped>
    .arrow-icon {
        transition: transform 0.2s ease-in-out;

        &.reversed {
            transform: rotate(180deg);
        }
    }

    .test-selector-panel {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-around;
        margin-top: 0.5em;
        padding: 0.7em;
        border-radius: 10px;
        background-color: #ececec;

        .test-selector-item {
            display: inline-block;
            padding: 0.6em;
            margin: 0.3em 0.2em;

            border: 1px solid #5b5b5b;
            border-radius: 5px;

            text-align: center;
            cursor: pointer;

            transition: background-color 0.1s linear;
            &:hover {
                background-color: #bababa;
            }

            .name {
                font-weight: bold;
                margin: 0 0.4em;
            }
        }
    }
</style>
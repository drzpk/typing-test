<template>
    <div>
        <div class="top-row">
            <div class="active-selection">
                <b-button variant="success" :disabled="userTestDefinitions.length === 0"
                          :pressed.sync="showTestSelector">
                    {{ buttonText }}
                    <font-awesome-icon
                        class="arrow-icon"
                        :class="{reversed: showTestSelector}"
                        style="margin-left: 0.5em"
                        icon="arrow-down"/>
                </b-button>
            </div>

            <div class="test-control">
                <TestTimer/>
                <TestReset/>
            </div>
        </div>

        <b-collapse :visible="showTestSelector && userTestDefinitions != null">
            <div class="test-selector-panel">
                <span class="test-selector-item" v-for="definition in userTestDefinitions" :key="definition.id"
                      :class="{active: definition === activeUserTestDefinition}"
                      @click="selectTestDefinition(definition)">

                    <span class="country"><country-flag
                        :country="mapLanguageToCountryCode(definition.wordList.language)" size="small"/></span>
                    <span class="name">{{ definition.name }}</span>
                    <span class="duration" v-show="definition.duration > 0">
                        {{ formatDuration(definition.duration) }}
                    </span>
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
import TestTimer from "@/views/typing/test/TestTimer.vue";
import TestReset from "@/views/typing/test/TestReset.vue";

@Component({
    components: {TestReset, TestTimer},
    computed: {
        ...mapGetters([
            "isLoading",
            "userTestDefinitions",
            "activeUserTestDefinition"
        ])
    }
})
export default class TestSelection extends Vue {
    isLoading!: boolean;
    userTestDefinitions!: Array<TestDefinitionModel>;
    activeUserTestDefinition!: TestDefinitionModel | null;

    showTestSelector = true;

    get buttonText(): string {
        if (this.activeUserTestDefinition != null)
            return this.activeUserTestDefinition.name;
        else if (this.userTestDefinitions.length > 0)
            return "select a test";
        else
            return "no test available";
    }

    mounted(): void {
        this.$store.dispatch("refreshUserTestDefinitions");
    }

    selectTestDefinition(definition: TestDefinitionModel): void {
        if (!this.isLoading)
            this.$store.commit("setActiveUserTestDefinition", definition);
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
.top-row {
    display: flex;
    justify-content: space-between;
}

.arrow-icon {
    transition: transform 0.2s ease-in-out;

    &.reversed {
        transform: rotate(180deg);
    }
}

.test-control {
    display: flex;
    justify-content: flex-end;
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

        &.active {
            background-color: #bababa;
            cursor: not-allowed;
        }

        .name {
            font-weight: bold;
            margin: 0 0.4em;
        }
    }
}
</style>
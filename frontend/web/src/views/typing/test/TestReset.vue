<template>
    <div id="test-reset-container">
        <div id="test-reset" :class="{'disabled': !isTestResetAvailable}">
            <font-awesome-icon icon="sync-alt" inverse size="lg" @click="reset"/>
        </div>
        <b-popover target="test-reset" triggers="hover" placement="bottom" delay="600"
                   :disabled="testResetUnavailableText == null">
            {{ testResetUnavailableText }}
        </b-popover>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import {mapGetters} from "vuex";
import {TestModel} from "@/models/tests";

@Component({
    computed: mapGetters(["activeTest", "isTestResetAvailable"])
})
export default class TestReset extends Vue {
    activeTest!: TestModel | null;
    isTestResetAvailable!: boolean;

    get testResetUnavailableText(): string | null {
        if (!this.activeTest)
            return "No test is selected.";
        else if (!this.isTestResetAvailable)
            return "Selected test type doesn't allow reseting word list.";
        else
            return null;
    }

    reset(): void {
        if (this.isTestResetAvailable)
            this.$store.dispatch("resetTest");
    }
}
</script>

<style lang="scss">
#test-reset-container {
    display: flex;
}

#test-reset {
    display: flex;
    align-items: center;
    margin-left: 0.5em;
    padding: 0 0.8em;
    background-color: #5581f1;
    border-radius: 4px;

    cursor: pointer;
    transition: background-color 0.1s linear;

    &:not(.disabled):hover {
        background-color: #4861c3;
    }

    &.disabled {
        cursor: not-allowed;
        background-color: #8ea8f1;
    }
}
</style>
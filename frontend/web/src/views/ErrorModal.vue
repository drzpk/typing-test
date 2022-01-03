<template>
    <div v-if="errorCode">
        <b-modal id="application-error-modal"
                 visible
                 centered
                 no-close-on-backdrop
                 no-close-on-esc
                 ok-only
                 header-bg-variant="danger"
                 header-text-variant="light"
                 size="lg"
                 @close="onModalClosed"
                 @ok="onModalClosed"
                 :title="title">

            <p class="message">{{ errorCode.message }}</p>

            <div>
                <p class="details">Code: <strong>{{ errorCode.code }}</strong></p>
                <p v-if="errorCode.object" class="details">Object: <strong>{{ errorCode.object }}</strong></p>
            </div>

            <div v-if="hasAdditionalData" class="additional-data">
                <b-button v-b-toggle:error-modal-additional-data size="sm">Show/hide additional data</b-button>

                <b-collapse id="error-modal-additional-data">
                    <pre>{{ JSON.stringify(errorCode.additionalData, null, 4) }}</pre>
                </b-collapse>
            </div>

        </b-modal>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import {mapGetters} from "vuex";
import {ErrorCodeModel} from "@/models/error";

@Component({
    computed: mapGetters([
        "errorCode",
        "errorRequestName"
    ])
})
export default class ErrorModal extends Vue {
    errorCode!: ErrorCodeModel | null;
    errorRequestName!: string | null;

    get title(): string {
        let title = "Error while executing request";
        if (this.errorRequestName)
            title += ` '${this.errorRequestName}'`;
        return title;
    }

    get hasAdditionalData(): boolean {
        if (!this.errorCode || !this.errorCode.additionalData)
            return false;

        for (let key in this.errorCode.additionalData) {
            if (Object.prototype.hasOwnProperty.call(this.errorCode.additionalData, key))
                return true;
        }

        return false;
    }

    onModalClosed(): void {
        this.$store.commit("clearError");
    }
}
</script>

<style lang="scss" scoped>
@import "~bootstrap/scss/bootstrap-reboot";

.message {
    font-size: 1.1em;
}

.details {
    color: $secondary;
    font-size: 0.8em;

    &:not(:last-child) {
        margin-bottom: 0;
    }
}

.additional-data {
    margin-top: 2em;

    pre {
        margin-top: 1em;
        padding: 0.4em;
        border-radius: 0.3em;
        background-color: $gray-400;
    }
}
</style>
<template>
    <div v-if="currentWordList != null">
        <br>
        <section>
            <h5>Export</h5>
            <b-button variant="primary" @click="exportWordList">Export</b-button>
        </section>

        <br>
        <section>
            <h5>Import</h5>

            <b-form @submit.stop.prevent>
                <b-row>
                    <b-col cols="6">
                        <b-form-group description="Import mode">
                            <b-form-select name="importMode" v-model="importMode" :state="validateState('importMode')"
                                           @input="resetState('importMode')">
                                <b-form-select-option value="append">Append to existing words</b-form-select-option>
                                <b-form-select-option value="delete">Delete existing words</b-form-select-option>
                            </b-form-select>

                            <ValidationMessageManager field-name="importMode" :state="state"/>
                        </b-form-group>
                    </b-col>

                    <b-col cols="6">
                        <b-form-group description="Update word's popularity if it already exists.">
                            <b-form-checkbox name="updateExistingWords" v-model="updateExistingWords"
                                             :disabled="importMode === 'delete'">
                                Update existing words
                            </b-form-checkbox>
                        </b-form-group>
                    </b-col>
                </b-row>

                <b-row>
                    <b-col cols="6">
                        <b-form-file name="file" v-model="file" accept="application/json"
                                     :state="validateState('file')"></b-form-file>

                        <ValidationMessageManager field-name="file" :state="state"/>
                    </b-col>
                </b-row>

                <b-row>
                    <b-col>
                        <br>
                        <b-alert :show="importMode === 'delete'" variant="warning">
                            <strong>Warning: </strong>all existing words will be deleted. Make sure that you're
                            importing the correct file or make a backup by exporting the current word list first.
                        </b-alert>
                        <b-button variant="primary" @click="importWordList">Import</b-button>
                    </b-col>
                </b-row>
            </b-form>

        </section>
    </div>
</template>

<script lang="ts">
import {Component} from "vue-property-decorator";
import {mapGetters} from "vuex";
import {WordListModel} from "@/models/words";
import {mixins} from "vue-class-component";
import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
import {required} from "vuelidate/lib/validators";
import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
import {WordImportData} from "@/store/admin";

@Component({
    components: {ValidationMessageManager},
    computed: mapGetters([
        "currentWordList"
    ]),
    validations: {
        importMode: {
            required
        },
        file: {
            required
        }
    },
    validationMessages: {
        importMode: {
            required: "Mode selection is required."
        },
        file: {
            required: "File is required."
        }
    }
})
export default class WordListImportExport extends mixins(ValidationHelperMixin) {
    currentWordList!: WordListModel | null;

    // Import form
    importMode: "append" | "delete" | null = null;
    updateExistingWords = false;
    file: File | null = null;

    exportWordList(): void {
        if (this.currentWordList == null)
            return;

        this.$store.dispatch("exportWords", this.currentWordList.id);
    }

    importWordList(): void {
        this.triggerValidation();
        if (this.$v.$invalid)
            return;

        const data: WordImportData = {
            mode: this.importMode!,
            updateExisting: this.updateExistingWords,
            file: this.file!
        };

        this.$store.dispatch("importWords", data).then(status => {
            if (status) {
                this.file = null;
                this.$v.$reset();

                this.$bvToast.toast("Word list has been successfully imported.", {
                    title: "Success",
                    variant: "success"
                });
            }
        });
    }
}
</script>

<style lang="scss">

</style>
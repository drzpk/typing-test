<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card :title="isCreated ? 'Test definition details' : 'New test definition'"
                            v-loading-overlay="pendingRequest">
                        <b-form @submit.stop.prevent>
                            <b-form-group label-for="name" label="Name" label-cols="4">
                                <b-form-input id="name" type="text" name="name"
                                              v-model="model.name" :state="validateState('model.name')"
                                              @input="resetState('model.name')"></b-form-input>

                                <ValidationMessageManager field-name="model.name" :state="state"/>
                            </b-form-group>

                            <b-form-group label-for="variableDuration" label="Variable duration" label-cols="4"
                                          description="User will have to type all words from the list."
                                          v-if="showVariableDurationCheckbox">
                                <b-form-checkbox id="variableDuration" name="variableDuration"
                                                 v-model="model.variableDuration"
                                                 :state="validateState('model.variableDuration')"
                                                 @input="resetState('model.variableDuration')"></b-form-checkbox>
                            </b-form-group>

                            <b-form-group label-for="duration" label="Duration (seconds)" label-cols="4">
                                <b-form-input id="duration" type="number" name="duration"
                                              :disabled="model.variableDuration"
                                              v-model="model.duration" :state="validateState('model.duration')"
                                              @input="resetState('model.duration')"></b-form-input>

                                <ValidationMessageManager field-name="model.duration" :state="state"/>
                            </b-form-group>

                            <b-form-group label-for="wordList" label="Word list" label-cols="4">
                                <b-form-select id="wordList" name="wordlist" :options="wordListOptions"
                                               v-model="model.wordListId" :state="validateState('model.wordListId')"
                                               @input="resetState('model.wordListId')"></b-form-select>

                                <ValidationMessageManager field-name="model.wordListId" :state="state"/>
                            </b-form-group>

                            <b-form-group label-for="isActive" label="Is active" label-cols="4">
                                <b-form-checkbox id="isActive" name="isActive"
                                                 v-model="model.isActive"></b-form-checkbox>
                            </b-form-group>

                            <b-button-group>
                                <b-button @click="saveButton">Save</b-button>
                                <b-button v-b-modal:delete-test-definition-modal v-show="isCreated" variant="danger">
                                    Delete
                                </b-button>
                            </b-button-group>

                            <b-modal id="delete-test-definition-modal" title="Deletion of the word list"
                                     @ok="deleteButton">
                                Are you sure you want to delete this test definition?
                            </b-modal>
                        </b-form>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {Component, Watch} from "vue-property-decorator";
import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
import {maxLength, required, requiredIf} from "vuelidate/lib/validators";
import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
import {mixins} from "vue-class-component";
import ApiService from "@/services/Api.service";
import {ValidationFailedError} from "@/models/error";
import WordListWords from "@/views/settings/admin/word/WordListWords.vue";
import {CreateUpdateTestDefinitionRequest, TestDefinitionModel} from "@/models/test-definition";
import {mapGetters} from "vuex";
import {WordListModel, WordListType} from "@/models/words";
import {SelectOption} from "@/models/common";
import Vue from "vue";

const requiredIfDurationIsVariable = requiredIf((vm: any, parentVm?: Vue) => (parentVm as TestDefinition)?.model?.variableDuration == true);
const positiveIfDurationIsVariable = (value: any, siblings: Model) => siblings.variableDuration || value > 0;

@Component({
    components: {WordListWords, ValidationMessageManager},
    computed: {
        ...mapGetters([
            "currentTestDefinition",
            "wordLists",
            "pendingRequest"
        ])
    },
    validations: {
        model: {
            name: {
                required,
                maxLength: maxLength(128)
            },
            duration: {
                required: requiredIfDurationIsVariable,
                minValue: positiveIfDurationIsVariable
            },
            wordListId: {
                required
            }
        }
    },
    validationMessages: {
        model: {
            name: {
                required: "Name is required.",
                maxLength: "Name length cannot exceed 128 characters."
            },
            duration: {
                required: "Duration is required.",
                minValue: "Duration must be a positive number."
            },
            wordListId: {
                required: "Word list is required"
            }
        }
    }
})
export default class TestDefinition extends mixins(ValidationHelperMixin) {
    currentTestDefinition!: TestDefinitionModel | null;
    wordLists!: Array<WordListModel>;
    pendingRequest!: boolean;

    currentTestDefinitionId: number | null = null;

    model = new Model();

    get isCreated(): boolean {
        return this.currentTestDefinitionId != null;
    }

    get wordListOptions(): Array<SelectOption> {
        if (!this.wordLists)
            return [];

        const options: Array<SelectOption> = [];
        for (let i = 0; i < this.wordLists.length; i++) {
            const text = `${this.wordLists[i].name} (${this.wordLists[i].language})`;
            options.push({value: this.wordLists[i].id, text: text});
        }

        return options;
    }

    get showVariableDurationCheckbox(): boolean {
        if (!this.model.wordListId)
            return false;

        let found: WordListModel | null = null;
        for (let wordList of this.wordLists) {
            if (wordList.id == this.model.wordListId) {
                found = wordList;
                break;
            }
        }

        return !!found && found.type == WordListType.FIXED;
    }

    @Watch("currentTestDefinition")
    onCurrentTestDefinitionChanged(current: TestDefinitionModel | null) {
        if (current != null)
            this.loadTestDefinition();
    }

    mounted(): void {
        if (this.$route.params.id) {
            const parsed = parseInt(this.$route.params.id);
            if (!isNaN(parsed)) {
                this.currentTestDefinitionId = parsed;
                this.$store.dispatch("setCurrentTestDefinition", this.currentTestDefinitionId);
            }
        }

        if (this.wordLists.length == 0)
            this.$store.dispatch("reloadWordLists");
    }

    saveButton(): void {
        this.triggerValidation();
        if (this.$v.$invalid)
            return;

        if (this.isCreated)
            this.updateTestDefinition();
        else
            this.createTestDefinition();
    }

    deleteButton(): void {
        this.$store.dispatch("deleteTestDefinition", this.currentTestDefinition!.id).then(() => {
            this.$router.push("/settings/admin");
        });
    }

    private loadTestDefinition(): void {
        if (this.currentTestDefinition == null)
            return;

        this.model.name = this.currentTestDefinition.name;
        this.model.wordListId = this.currentTestDefinition.wordList ? this.currentTestDefinition.wordList.id : null;
        this.model.variableDuration = this.currentTestDefinition.duration == null;
        this.model.duration = this.currentTestDefinition.duration ? this.currentTestDefinition.duration : 0;
        this.model.isActive = this.currentTestDefinition.isActive;
    }

    private createTestDefinition(): void {
        const request: CreateUpdateTestDefinitionRequest = {
            name: this.model.name,
            duration: this.model.variableDuration ? null : this.model.duration,
            wordListId: this.model.wordListId,
            isActive: this.model.isActive
        };

        // Todo: move the API call into the store
        ApiService.createTestDefinition(request)
            .then(() => {
                this.$store.dispatch("reloadTestDefinitions");
                this.$router.push("/settings/admin");
            })
            .catch(error => {
                if (error instanceof ValidationFailedError)
                    this.processValidationError(error);
                else
                    console.error(error);
            });
    }

    private updateTestDefinition(): void {
        const request: CreateUpdateTestDefinitionRequest = {
            name: this.model.name,
            duration: this.model.variableDuration ? null : this.model.duration,
            wordListId: this.model.wordListId,
            isActive: this.model.isActive
        };

        ApiService.updateTestDefinition(this.currentTestDefinition!.id, request)
            .then(() => {
                this.$store.dispatch("reloadTestDefinitions");
            })
            .catch(error => {
                if (error instanceof ValidationFailedError)
                    this.processValidationError(error);
                else
                    console.error(error);
            });
    }
}

class Model {
    name = "";
    wordListId: number | null = null;
    variableDuration = false;
    duration = 0;
    isActive = false;
}
</script>

<style lang="scss">

</style>
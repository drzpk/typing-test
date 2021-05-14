<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card :title="isCreated ? 'Test definition details' : 'New test definition'">
                        <b-form @submit.stop.prevent="saveButton">
                            <b-form-group label-for="name" label="Name" label-cols="4">
                                <b-form-input id="name" type="text" name="name"
                                              v-model="model.name" :state="validateState('model.name')"
                                              @input="resetState('model.name')"></b-form-input>

                                <ValidationMessageManager field-name="model.name" :state="state"/>
                            </b-form-group>

                            <b-form-group label-for="duration" label="Duration (seconds)" label-cols="4">
                                <b-form-input id="duration" type="number" name="duration"
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
                                                 v-model="model.isActive"
                                                 :state="validateState('model.isActive')"></b-form-checkbox>

                                <ValidationMessageManager field-name="model.isActive" :state="state"/>
                            </b-form-group>

                            <b-button @click="saveButton">Save</b-button>
                        </b-form>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component} from "vue-property-decorator";
    import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
    import {maxLength, minValue, required} from "vuelidate/lib/validators";
    import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
    import {mixins} from "vue-class-component";
    import ApiService from "@/services/Api.service";
    import {ValidationFailedError} from "@/models/error";
    import WordListWords from "@/views/settings/admin/word/WordListWords.vue";
    import {CreateUpdateTestDefinitionRequest, TestDefinitionModel} from "@/models/test-definition";
    import {mapGetters} from "vuex";
    import {WordList} from "@/models/words";
    import {SelectOption} from "@/models/common";

    @Component({
        components: {WordListWords, ValidationMessageManager},
        computed: {
            ...mapGetters(["wordLists"])
        },
        validations: {
            model: {
                name: {
                    required,
                    maxLength: maxLength(128)
                },
                duration: {
                    required,
                    minValue: minValue(1)
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
        wordLists!: Array<WordList>;

        testDefinition: TestDefinitionModel | null = null;

        model = {
            name: "",
            wordListId: null,
            duration: 0,
            isActive: false
        };

        get isCreated(): boolean {
            return this.testDefinition != null;
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

        mounted(): void {
            if (this.$route.params.id) {
                const id = parseInt(this.$route.params.id);
                this.loadTestDefinition(id);
            }
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

        private loadTestDefinition(id: number): void {
            const testDefinition = this.$store.getters.getTestDefinition(id);
            this.testDefinition = testDefinition;

            this.model.name = testDefinition.name;
            this.model.wordListId = testDefinition.wordList.id;
            this.model.duration = testDefinition.duration;
            this.model.isActive = testDefinition.isActive;
        }

        private createTestDefinition(): void {
            const request: CreateUpdateTestDefinitionRequest = {
                name: this.model.name,
                duration: this.model.duration,
                wordListId: this.model.wordListId,
                isActive: this.model.isActive
            };

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
                duration: this.model.duration,
                wordListId: this.model.wordListId,
                isActive: this.model.isActive
            };

            ApiService.updateTestDefinition(this.testDefinition!.id, request)
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
</script>

<style lang="scss">

</style>
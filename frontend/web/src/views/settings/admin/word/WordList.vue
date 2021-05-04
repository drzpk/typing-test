<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2">
                    <b-card title="Word list details">
                        <b-form @submit.stop.prevent="saveButton">
                            <b-form-group label-for="name" label="Name" label-cols="4">
                                <b-form-input id="name" type="text" name="name"
                                              v-model="name" :state="validateState('name')"
                                              @input="resetState('name')" :disabled="isCreated"></b-form-input>

                                <ValidationMessageManager field-name="name" :state="state"/>
                            </b-form-group>
                            <b-form-group label-for="name" label="Language" label-cols="4">
                                <b-form-select id="language" name="language" :options="availableLanguages"
                                               v-model="language" :state="validateState('language')"
                                               @input="resetState('language')" :disabled="isCreated"></b-form-select>

                                <ValidationMessageManager field-name="language" :state="state"/>
                            </b-form-group>

                            <b-button @click="saveButton" v-if="!isCreated">Save</b-button>
                        </b-form>
                    </b-card>

                    <b-card title="Words">
                        <WordListWords v-if="isCreated" :word-list-id="currentWordListId"></WordListWords>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component} from "vue-property-decorator";
    import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
    import {maxLength, required} from "vuelidate/lib/validators";
    import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
    import {mixins} from "vue-class-component";
    import ApiService from "@/services/Api.service";
    import {ValidationFailedError} from "@/models/error";
    import WordListWords from "@/views/settings/admin/word/WordListWords.vue";

    @Component({
        components: {WordListWords, ValidationMessageManager},
        validations: {
            name: {
                required,
                maxLength: maxLength(128)
            },
            language: {
                required
            }
        },
        validationMessages: {
            name: {
                required: "Name is required.",
                maxLength: "Name length cannot exceel 128 characters"
            },
            language: {
                required: "Language is required"
            }
        }
    })
    export default class WordList extends mixins(ValidationHelperMixin) {
        availableLanguages: Array<string> = [];

        // Form
        name = "";
        language = "";

        private currentWordListId = -1;

        get isCreated(): boolean {
            return this.currentWordListId != -1;
        }

        mounted(): void {
            this.$store.dispatch("getAvailableLanguages").then(languages => this.availableLanguages = languages);
            if (this.$route.params.id) {
                this.currentWordListId = parseInt(this.$route.params.id);
                this.loadWordList();
            }
        }

        saveButton(): void {
            if (this.isCreated)
                return; // Update is not supported

            this.triggerValidation();
            if (this.$v.$invalid)
                return;

            this.createWordList();
        }

        private loadWordList(): void {
            const wordList = this.$store.getters.getWordList(this.currentWordListId!);
            this.name = wordList.name;
            this.language = wordList.language;
        }

        private createWordList(): void {
            ApiService.createWordList(this.name, this.language)
                .then(() => {
                    this.$store.dispatch("reloadWordLists");
                    this.$router.push("/settings/admin");
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
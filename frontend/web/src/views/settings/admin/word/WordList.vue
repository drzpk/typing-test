<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="8" offset="2" v-loading-overlay="pendingRequest">
                    <b-card title="Word list details">
                        <b-form @submit.stop.prevent>
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
                            <b-form-group label-for="type" label="Type" label-cols="4">
                                <b-form-select id="type" name="type" :options="availableTypes"
                                               v-model="type" :state="validateState('type')"
                                               @input="resetState('type')" :disabled="isCreated"></b-form-select>

                                <ValidationMessageManager field-name="type" :state="state"/>
                            </b-form-group>

                            <b-button @click="createWordList" v-if="!isCreated">Save</b-button>
                            <b-button v-b-modal:delete-word-list-modal v-if="isCreated" variant="danger">Delete
                            </b-button>

                            <b-modal id="delete-word-list-modal" title="Deletion of the word list" @ok="deleteWordList">
                                Are you sure you want to delete this word list?
                            </b-modal>
                        </b-form>
                    </b-card>

                    <b-card v-if="typeString === 'RANDOM'" title="Words"
                            sub-title="List of available words from which random words will be selected for each test.">
                        <WordListWords v-if="isCreated"></WordListWords>
                    </b-card>
                    <b-card v-if="typeString === 'FIXED'" title="Text"
                            sub-title="Fixed, always the same text to rewrite by a user.">
                        <WordListFixedText v-if="isCreated" :word-list-id="currentWordListId"></WordListFixedText>
                    </b-card>

                    <b-card v-if="typeString === 'RANDOM' && isCreated" title="Word import/export"
                            sub-title="Import or export words using a JSON file.">
                        <WordListImportExport></WordListImportExport>
                    </b-card>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
import {Component, Watch} from "vue-property-decorator";
import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
import {maxLength, required} from "vuelidate/lib/validators";
import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
import {mixins} from "vue-class-component";
import {ValidationFailedError} from "@/models/error";
import WordListWords from "@/views/settings/admin/word/WordListWords.vue";
import {WordListModel, WordListType} from "@/models/words";
import {SelectOption} from "@/models/common";
import WordListFixedText from "@/views/settings/admin/word/WordListFixedText.vue";
import {mapGetters} from "vuex";
import WordListImportExport from "@/views/settings/admin/word/WordListImportExport.vue";
import {CreateWordListData} from "@/store/admin";

@Component({
    components: {WordListImportExport, WordListFixedText, WordListWords, ValidationMessageManager},
    computed: {
        ...mapGetters([
            "currentWordList",
            "pendingRequest"
        ])
    },
    validations: {
        name: {
            required,
            maxLength: maxLength(128)
        },
        language: {
            required
        },
        type: {
            required
        }
    },
    validationMessages: {
        name: {
            required: "Name is required.",
            maxLength: "Name length cannot exceel 128 characters."
        },
        language: {
            required: "Language is required."
        },
        type: {
            required: "Type is required."
        }
    }
})
export default class WordList extends mixins(ValidationHelperMixin) {
    currentWordList!: WordListModel | null;
    pendingRequest!: boolean;

    availableLanguages: Array<string> = [];
    availableTypes: SelectOption[] = [];

    // Form
    name = "";
    language = "";
    type: WordListType | null = null;

    private currentWordListId = -1;

    get isCreated(): boolean {
        return this.currentWordListId != -1;
    }

    get typeString(): string {
        return this.type != null ? this.type.toString() : "";
    }

    @Watch("currentWordList")
    onCurrentWordListChanged(current: WordListModel | null) {
        if (current != null)
            this.loadWordList();
    }

    mounted(): void {
        this.$store.dispatch("getAvailableLanguages").then(languages => this.availableLanguages = languages);
        if (this.$route.params.id) {
            this.currentWordListId = parseInt(this.$route.params.id);
            this.$store.dispatch("setCurrentWordList", this.currentWordListId);
        }

        this.availableTypes = Object.values(WordListType).map(it => {
            return {
                value: it,
                text: it.substr(0, 1).toUpperCase() + it.substr(1).toLowerCase()
            };
        });
    }

    createWordList(): void {
        if (this.isCreated)
            return; // Update is not supported

        this.triggerValidation();
        if (this.$v.$invalid)
            return;

        this.doCreateWordList();
    }

    deleteWordList(): void {
        this.$store.dispatch("deleteWordList", this.currentWordListId);
    }

    private loadWordList(): void {
        this.name = this.currentWordList!.name;
        this.language = this.currentWordList!.language;
        this.type = this.currentWordList!.type;
    }

    private doCreateWordList(): void {
        const data: CreateWordListData = {
            name: this.name,
            language: this.language,
            type: this.type
        };

        this.$store.dispatch("createWordList", data).then(() => {
            this.$router.push("/settings/admin");
        }).catch(error => {
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
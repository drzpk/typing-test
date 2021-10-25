<template>
    <div>
        <b-table v-if="currentWordListWords" :items="currentWordListWords.content" :fields="fields">
            <!--suppress HtmlUnknownAttribute -->
            <template #cell(actions)="word">
                <font-awesome-icon icon="pencil-alt" style="cursor: pointer" @click="editWord(word.item)"/>
                <span style="margin: 0.4em"></span>
                <font-awesome-icon icon="trash" style="cursor: pointer" @click="confirmDeleteWord(word.item)"/>
            </template>
        </b-table>

        <b-pagination
            v-if="currentWordListWords"
            v-model="pagedRequest.page"
            :total-rows="currentWordListWords.metadata.totalElements"
            :per-page="currentWordListWords.metadata.size"
            @change="onPageChanged"
        ></b-pagination>

        <b-button @click="addWord">Add a word</b-button>

        <b-modal id="word-editor-modal" :title="wordEditor.createMode ? 'Add a word' : 'Edit the word'" @ok="saveWord">
            <b-form-group label-for="word" label="Word" label-cols="4" v-if="wordEditor.createMode">
                <b-form-input id="word" type="text" name="name"
                              v-model="wordEditor.word" :state="validateState('wordEditor.word')"
                              @input="resetState('wordEditor.word')"></b-form-input>

                <ValidationMessageManager field-name="wordEditor.word" :state="state"/>
            </b-form-group>
            <b-form-group label-for="popularity" label="Popularity" label-cols="4">
                <b-form-input id="popularity" type="text" name="popularity"
                              v-model="wordEditor.popularity" :state="validateState('wordEditor.popularity')"
                              @input="resetState('wordEditor.popularity')"></b-form-input>

                <ValidationMessageManager field-name="wordEditor.popularity" :state="state"/>
            </b-form-group>
        </b-modal>

        <b-modal id="word-delete-modal" title="Confirmation" @ok="doDeleteWord">
            Are you sure you want to delete the word '{{ wordEditor.word }}'?
        </b-modal>
    </div>
</template>

<script lang="ts">
import {Component} from "vue-property-decorator";
import {PagedRequest} from "@/models/pagination";
import {WordListWord, WordListWordsModel} from "@/models/words";
import {mixins} from "vue-class-component";
import ValidationHelperMixin from "@/mixins/ValidationHelperMixin";
import ValidationMessageManager from "@/views/shared/ValidationMessageManager.vue";
import {maxLength, minValue, required} from "vuelidate/lib/validators";
import {BvModalEvent} from "bootstrap-vue";
import {ValidationFailedError} from "@/models/error";
import {mapGetters} from "vuex";
import {CreateWordData, UpdateWordPopularityData} from "@/store/admin";

@Component({
    components: {ValidationMessageManager},
    computed: mapGetters(["currentWordListWords"]),
    validations: function () {
        const rules: { [key: string]: any } = {
            wordEditor: {
                word: {
                    required,
                    maxLength: maxLength(64)
                },
                popularity: {
                    required,
                    minValue: minValue(1)
                }
            }
        };

        if (!(this as WordListWords).wordEditor.createMode)
            delete rules.wordEditor.word;

        return rules;
    },
    validationMessages: {
        wordEditor: {
            word: {
                required: "Word is required.",
                maxLength: "Max length is 64."
            },
            popularity: {
                required: "Popularity is required.",
                minValue: "Popularity must be a positive number."
            }
        }
    },
    serverFieldNameMapping: {
        word: "wordEditor.word",
        popularity: "wordEditor.popularity"
    }
})
export default class WordListWords extends mixins(ValidationHelperMixin) {
    currentWordListWords!: WordListWordsModel;
    pagedRequest = new PagedRequest();

    wordEditor = {
        id: -1,
        word: "",
        popularity: 1,
        createMode: false
    };

    fields = [
        {
            key: "id",
            label: "Identifier"
        },
        {
            key: "word"
        },
        {
            key: "popularity"
        },
        {
            key: "actions"
        }
    ];

    mounted(): void {
        this.pagedRequest.size = 10;
        this.refreshWordList();
    }

    onPageChanged(newPageNo: number): void {
        this.pagedRequest.page = newPageNo;
        this.refreshWordList();
    }

    addWord(): void {
        this.wordEditor.createMode = true;
        this.wordEditor.word = "";
        this.wordEditor.popularity = 1;
        this.$bvModal.show("word-editor-modal");
    }

    editWord(word: WordListWord): void {
        this.wordEditor.createMode = false;
        this.wordEditor.id = word.id;
        this.wordEditor.popularity = word.popularity;
        this.$bvModal.show("word-editor-modal");
    }

    saveWord(event: BvModalEvent): void {
        event.preventDefault();

        this.triggerValidation();
        if (this.$v.$anyError)
            return;

        let promise: Promise<any>;
        if (this.wordEditor.createMode) {
            const payload: CreateWordData = {
                word: this.wordEditor.word,
                popularity: this.wordEditor.popularity
            };
            promise = this.$store.dispatch("createWord", payload);
        } else {
            const payload: UpdateWordPopularityData = {
                wordId: this.wordEditor.id,
                popularity: this.wordEditor.popularity
            };
            promise = this.$store.dispatch("updateWordPopularity", payload);
        }

        promise.catch(error => {
            if (error instanceof ValidationFailedError)
                this.processValidationError(error);
            else
                console.error(error);
        }).then(() => {
            this.$bvModal.hide("word-editor-modal");
            this.onWordListRefreshed();
        })
    }

    confirmDeleteWord(word: WordListWord): void {
        this.wordEditor.id = word.id;
        this.wordEditor.word = word.word;
        this.$bvModal.show("word-delete-modal");
    }

    doDeleteWord(): void {
        this.$store.dispatch("deleteWord", this.wordEditor.id)
            .catch(error => console.error(error));
    }

    private refreshWordList(): void {
        this.$store.dispatch("refreshCurrentWordListWords", this.pagedRequest).then(this.onWordListRefreshed);
    }

    private onWordListRefreshed(): void {
        // Page number must be bound to a local instance.
        // When it was bound directly by :value to currentWordList.metadata.page, the number
        // wasn't updated on page change even though the internal state of the pagination component was.
        this.pagedRequest.page = this.currentWordListWords.metadata.page;
    }
}
</script>

<style lang="scss">

</style>
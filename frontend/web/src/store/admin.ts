import {Module} from "vuex";
import {RootState} from "@/store/index";
import {WordList} from "@/models/words";
import ApiService from "@/services/Api.service";
import {TestDefinitionModel} from "@/models/test-definition";

export interface AdminState {
    wordLists: Array<WordList> | null;
    testDefinitions: Array<TestDefinitionModel> | null;
    availableLanguages: Array<string> | null;
    currentWordList: WordList | null;
}

const adminModule: Module<AdminState, RootState> = {
    state: {
        wordLists: null,
        testDefinitions: null,
        availableLanguages: null,
        currentWordList: null
    },
    getters: {
        wordLists(state): Array<WordList> {
            if (state.wordLists != null)
                return state.wordLists;
            else
                return [];
        },
        testDefinitions(state): Array<TestDefinitionModel> {
            if (state.testDefinitions != null)
                return state.testDefinitions;
            else
                return [];
        },
        getWordList(state): (wordListId: number) => WordList {
            return (wordListId: number): WordList => {
                if (state.wordLists == null)
                    throw new Error("Word lists not available");

                for (let i = 0; i < state.wordLists.length; i++) {
                    if (state.wordLists[i].id === wordListId)
                        return state.wordLists[i];
                }

                throw new Error(`Word list with id ${wordListId} wasn't found.`);
            };
        },
        getTestDefinition(state): (testDefinitionId: number) => TestDefinitionModel {
            return (testDefinitionId: number) => {
                if (state.testDefinitions == null)
                    throw new Error("Test definitions not available");

                for (let i = 0; i < state.testDefinitions.length; i++) {
                    if (state.testDefinitions[i].id === testDefinitionId)
                        return state.testDefinitions[i];
                }

                throw new Error(`Test definition with id ${testDefinitionId} wasn't found.`)
            };
        }
    },
    mutations: {},
    actions: {
        reloadWordLists(context) {
            ApiService.getWordLists().then(lists => {
                context.state.wordLists = lists;
            }).catch(error => {
                console.error(error);
            });
        },

        reloadTestDefinitions(context) {
            ApiService.getTestDefinitions().then(definitions => {
                context.state.testDefinitions = definitions;
            }).catch(error => {
                console.error(error);
            })
        },

        getAvailableLanguages(context): Promise<Array<string>> {
            if (context.state.availableLanguages == null)
                context.state.availableLanguages = ["pl", "en"];

            return Promise.resolve(context.state.availableLanguages);
        }
    }
};

export default adminModule;
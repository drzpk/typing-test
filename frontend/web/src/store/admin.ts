// noinspection JSIgnoredPromiseFromCall

import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {WordListModel} from "@/models/words";
import ApiService from "@/services/Api.service";
import {TestDefinitionModel} from "@/models/test-definition";
import {SearchUsersRequest, SearchUsersResponse, UserModel} from "@/models/user";
import {PageMetadata} from "@/models/pagination";
import {withPendingRequest} from "@/utils/store-utils";

export interface WordListText {
    id: number;
    text: string
}

interface AdminState {
    wordLists: Array<WordListModel> | null;
    testDefinitions: Array<TestDefinitionModel> | null;
    availableLanguages: Array<string> | null;
    currentTestDefinition: TestDefinitionModel | null;
    currentWordList: WordListModel | null;
    usersState: UsersState;
}

class UsersState {
    users: UserModel[] | null = null;
    metadata: PageMetadata | null = null;
    inactiveOnly = false;
}

const adminModule: Module<AdminState, RootState> = {
    state: {
        wordLists: null,
        testDefinitions: null,
        availableLanguages: null,
        currentTestDefinition: null,
        currentWordList: null,
        usersState: new UsersState()
    },
    getters: {
        wordLists(state): Array<WordListModel> {
            if (state.wordLists != null)
                return state.wordLists;
            else
                return [];
        },
        currentWordList(state): WordListModel | null {
          return state.currentWordList;
        },
        testDefinitions(state): Array<TestDefinitionModel> {
            if (state.testDefinitions != null)
                return state.testDefinitions;
            else
                return [];
        },
        currentTestDefinition(state): TestDefinitionModel | null {
            return state.currentTestDefinition;
        },
        users(state): UserModel[] {
            if (state.usersState.users)
                return state.usersState.users;
            else
                return [];
        },
        usersMetadata(state): PageMetadata | null {
            return state.usersState.metadata;
        }
    },
    mutations: {
        setWordLists(state, wordLists: WordListModel[]) {
            state.wordLists = wordLists;
        },
        setCurrentWordList(state, wordList: WordListModel | null) {
            state.currentWordList = wordList;
        },
        setTestDefinitions(state, testDefinitions: TestDefinitionModel[]) {
            state.testDefinitions = testDefinitions;
        },
        setCurrentTestDefinition(state, testDefinition: TestDefinitionModel | null) {
            state.currentTestDefinition = testDefinition;
        },
        setAvailableLanguages(state, availableLanguages: string[]) {
            state.availableLanguages = availableLanguages;
        },
        setUsers(state, response: SearchUsersResponse) {
            state.usersState.users = response.content;
            state.usersState.metadata = response.metadata;
        },
        setUserInactiveOnly(state, inactiveOnly: boolean) {
            state.usersState.inactiveOnly = inactiveOnly;
        }
    },
    actions: {
        reloadWordLists(context: ActionContext<any, any>) {
            withPendingRequest("loadWordLists", context, () => {
                return ApiService.getWordLists().then(lists => {
                    context.commit("setWordLists", lists);
                }).catch(error => {
                    console.error(error);
                });
            });
        },

        reloadTestDefinitions(context: ActionContext<any, any>) {
            withPendingRequest("loadTestDefinitions", context, () => {
                return ApiService.getTestDefinitions().then(definitions => {
                    context.commit("setTestDefinitions", definitions);
                }).catch(error => {
                    console.error(error);
                });
            });
        },

        getAvailableLanguages(context: ActionContext<any, any>): Promise<Array<string>> {
            if (context.state.availableLanguages == null)
                context.commit("setAvailableLanguages", ["pl", "en"]);

            return Promise.resolve(context.state.availableLanguages);
        },

        reloadUserList(context: ActionContext<any, any>, forcePage: number | null = null) {
            let page: number;
            if (!forcePage) {
                if (context.state.usersState.metadata)
                    page = context.state.usersState.metadata.page;
                else
                    page = 1;
            } else {
                page = forcePage;
            }

            const request: SearchUsersRequest = {
                page,
                size: 20,
                inactiveOnly: context.state.usersState && context.state.usersState.inactiveOnly
            };

            withPendingRequest("loadUsers", context, () => {
                return ApiService.searchUsers(request).then(users => {
                    context.commit("setUsers", users);
                });
            });
        },

        setCurrentTestDefinition(context: ActionContext<any, any>, id: number) {
            let testDefinitionPromise: Promise<TestDefinitionModel>;

            if (context.state.testDefinitions != null) {
                testDefinitionPromise = new Promise<TestDefinitionModel>((resolve, reject) => {
                    for (let i = 0; i < context.state.testDefinitions.length; i++) {
                        if (context.state.testDefinitions[i].id === id) {
                            resolve(context.state.testDefinitions[i]);
                            break;
                        }
                    }

                    reject(new Error(`Test definition with id ${id} wasn't found.`));
                });
            } else {
                testDefinitionPromise = withPendingRequest("getTestDefinition", context, () => {
                    return ApiService.getTestDefinition(id);
                });
            }

            testDefinitionPromise.then((testDefinition: TestDefinitionModel) => {
                context.commit("setCurrentTestDefinition", testDefinition);
            });
        },

        activateUser(context: ActionContext<any, any>, userId: number) {
            ApiService.activateUser(userId).then(() => {
                return context.dispatch("reloadUserList");
            });
        },

        deleteUser(context: ActionContext<any, any>, userId: number) {
            ApiService.deleteUser(userId).then(() => {
                return context.dispatch("reloadUserList");
            });
        },

        setCurrentWordList(context: ActionContext<any, any>, id: number) {
            let wordListPromise: Promise<WordListModel>;

            if (context.state.wordLists != null) {
                wordListPromise = new Promise<WordListModel>((resolve, reject) => {
                    for (let i = 0; i < context.state.wordLists.length; i++) {
                        if (context.state.wordLists[i].id === id) {
                            resolve(context.state.wordLists[i]);
                            break;
                        }
                    }

                    reject(new Error(`Word list with id ${id} wasn't found.`));
                });
            } else {
                wordListPromise = withPendingRequest("getWordList", context, () => {
                    return ApiService.getWordList(id);
                });
            }

            wordListPromise.then((wordList: WordListModel) => {
                context.commit("setCurrentWordList", wordList);
            });
        },

        setWordListText(context: ActionContext<any, any>, text: WordListText) {
            withPendingRequest("setWordListText", context, () => {
                return ApiService.updateWordList(text.id, text.text)
                    .then(() => {
                        return context.dispatch("reloadWordLists");
                    });
            });
        },

        deleteWordList(context: ActionContext<any, any>, wordListId: number) {
            ApiService.deleteWordList(wordListId).then(() => {
                return context.dispatch("reloadWordLists");
            });
        }
    }
};

export default adminModule;
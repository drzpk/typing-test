// noinspection JSIgnoredPromiseFromCall

import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {ExportedWords, ImportWordsRequest, WordListModel, WordListType, WordListWordsModel} from "@/models/words";
import ApiService from "@/services/Api.service";
import FileService from "@/services/File.service";
import {TestDefinitionModel} from "@/models/test-definition";
import {SearchUsersRequest, SearchUsersResponse, UserModel} from "@/models/user";
import {PagedRequest, PageMetadata} from "@/models/pagination";
import {withPendingRequest} from "@/utils/store-utils";

export interface CreateWordListData {
    name: string;
    language: string;
    type: WordListType;
}

export interface WordListText {
    id: number;
    text: string
}

export interface CreateWordData {
    word: string;
    popularity: number;
}

export interface UpdateWordPopularityData {
    wordId: number;
    popularity: number;
}

export interface WordImportData {
    mode: "append" | "delete";
    updateExisting: boolean;
    file: File;
}

interface AdminState {
    wordLists: Array<WordListModel> | null;
    testDefinitions: Array<TestDefinitionModel> | null;
    availableLanguages: Array<string> | null;
    currentTestDefinition: TestDefinitionModel | null;
    currentWordList: WordListModel | null;
    currentWordListWords: WordListWordsModel | null;
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
        currentWordListWords: null,
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
        currentWordListWords(state): WordListWordsModel | null {
            return state.currentWordListWords;
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
        setCurrentWordListWords(state, wordListWords: WordListWordsModel | null) {
            state.currentWordListWords = wordListWords;
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

        createWordList(context: ActionContext<any, any>, data: CreateWordListData) {
            return withPendingRequest("createWordList", context, () => {
                return ApiService.createWordList(data.name, data.language, data.type);
            }).then(() => {
                return context.dispatch("reloadWordLists");
            })
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
        },

        refreshCurrentWordListWords(context: ActionContext<any, any>, pagination: PagedRequest | undefined) {
            if (!context.state.currentWordList)
                throw new Error("No current word list selected.");

            let requestPagination: PagedRequest;
            if (!pagination) {
                if (context.state.currentWordListWords)
                    requestPagination = PagedRequest.fromPageMetadata(context.state.currentWordListWords.metadata);
                else
                    requestPagination = new PagedRequest();
            } else {
                requestPagination = pagination;
            }

            return withPendingRequest("refreshCurrentWordListWords", context, () => {
                return ApiService.getWordListWords(context.state.currentWordList.id, requestPagination);
            }).then((model: WordListWordsModel) => {
                context.commit("setCurrentWordListWords", model);
            });
        },

        createWord(context: ActionContext<any, any>, data: CreateWordData) {
            if (!context.state.currentWordList)
                throw new Error("No current word list selected.");

            return withPendingRequest("createWord", context, () => {
                return ApiService.createWord(context.state.currentWordList.id, data.word, data.popularity).then(() => {
                    let isOnLastPage = false;
                    let shouldChangePage = false;

                    if (context.state.currentWordListWords) {
                        const metadata = context.state.currentWordListWords.metadata as PageMetadata;
                        isOnLastPage = metadata.page == metadata.totalPages;
                        shouldChangePage = metadata.totalElements % metadata.size == 0;
                    }

                    let pagination: PagedRequest | undefined = undefined;
                    if (isOnLastPage && shouldChangePage) {
                        pagination = PagedRequest.fromPageMetadata(context.state.currentWordListWords);
                        pagination.page = pagination.page + 1;
                    }

                    return context.dispatch("refreshCurrentWordListWords", pagination);
                })
            });
        },

        updateWordPopularity(context: ActionContext<any, any>, data: UpdateWordPopularityData) {
            if (!context.state.currentWordList)
                throw new Error("No current word list selected.");

            return withPendingRequest("updateWordPopularity", context, () => {
                return ApiService.updateWordPopularity(context.state.currentWordList.id, data.wordId, data.popularity).then(() => {
                    return context.dispatch("refreshCurrentWordListWords");
                });
            });
        },

        deleteWord(context: ActionContext<any, any>, wordId: number) {
            if (!context.state.currentWordList)
                throw new Error("No current word list selected.");

            return withPendingRequest("deleteWord", context, () => {
                return ApiService.deleteWord(context.state.currentWordList.id, wordId).then(() => {

                    let pagination: PagedRequest | undefined = undefined;
                    const words: WordListWordsModel | null = context.state.currentWordListWords;
                    if (words) {
                        const metadata: PageMetadata = words.metadata;
                        const wasLastOnPage = words.content.length == 1 && metadata.page == metadata.totalPages;

                        if (wasLastOnPage) {
                            pagination = PagedRequest.fromPageMetadata(metadata);
                            pagination.page = Math.max(1, pagination.page - 1);
                        }
                    }

                    return context.dispatch("refreshCurrentWordListWords", pagination);
                });
            });
        },

        exportWords(context: ActionContext<any, any>) {
            if (!context.state.currentWordList)
                throw new Error("No current word list selected.");

            const wordListId = context.state.currentWordList.id;

            withPendingRequest("exportWords", context, () => {
                return ApiService.exportWords(wordListId);
            }).then((data: any) => {
                const name = `word_list_${wordListId}_words.json`;
                const stringified = JSON.stringify(data, null, 2);
                FileService.saveToFile(stringified, name);
            });
        },

        importWords(context: ActionContext<any, any>, data: WordImportData) {
            if (!context.state.currentWordList)
                throw new Error("No current word list selected.");

            return withPendingRequest("importWords", context, () => {
                return data.file.text().then((content: string) => {
                    const parsed = JSON.parse(content) as ExportedWords;

                    const request: ImportWordsRequest = {
                        wordListId: context.state.currentWordList.id,
                        deleteExisting: data.mode === "delete",
                        updateExisting: data.mode === "append" && data.updateExisting,
                        words: parsed.words
                    };

                    return ApiService.importWords(request);
                }).then(() => {
                    return context.dispatch("refreshCurrentWordListWords");
                });
            });
        }
    }
};

export default adminModule;
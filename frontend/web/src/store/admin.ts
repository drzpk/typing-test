import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {WordList} from "@/models/words";
import ApiService from "@/services/Api.service";
import {TestDefinitionModel} from "@/models/test-definition";
import {SearchUsersRequest, SearchUsersResponse, UserModel} from "@/models/user";
import {PageMetadata} from "@/models/pagination";

interface AdminState {
    wordLists: Array<WordList> | null;
    testDefinitions: Array<TestDefinitionModel> | null;
    availableLanguages: Array<string> | null;
    currentWordList: WordList | null;
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
        currentWordList: null,
        usersState: new UsersState()
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
        setWordLists(state, wordLists: WordList[]) {
            state.wordLists = wordLists;
        },
        setTestDefinitions(state, testDefinitions: TestDefinitionModel[]) {
            state.testDefinitions = testDefinitions;
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
            ApiService.getWordLists().then(lists => {
                context.commit("setWordLists", lists);
            }).catch(error => {
                console.error(error);
            });
        },

        reloadTestDefinitions(context: ActionContext<any, any>) {
            ApiService.getTestDefinitions().then(definitions => {
                context.commit("setTestDefinitions", definitions);
            }).catch(error => {
                console.error(error);
            })
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
            ApiService.searchUsers(request).then(users => {
               context.commit("setUsers", users);
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
        }
    }
};

export default adminModule;
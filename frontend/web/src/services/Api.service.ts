import axios, {AxiosError} from "axios";
import {
    AuthenticationDetails,
    ChangePasswordRequest,
    SearchUsersRequest,
    SearchUsersResponse,
    UpdateSettingsRequest
} from "@/models/user";
import {
    ErrorCode,
    ErrorCodeModel,
    ServerError,
    UserNotLoggedInError,
    ValidationErrorsModel,
    ValidationFailedError
} from "@/models/error";
import {ImportWordsRequest, WordListModel, WordListType, WordListWordsModel} from "@/models/words";
import {PagedRequest} from "@/models/pagination";
import {CreateUpdateTestDefinitionRequest, TestDefinitionModel} from "@/models/test-definition";
import {FinishTestRequest, TestBestResultModel, TestModel, TestResultModel, TestResultRangeModel} from "@/models/tests";
import {TestStatsModel} from "@/models/test-stats";
import DateService from "./Date.service";
import router from "@/router";

function errorHandler(error: AxiosError): never {
    if (error.response?.status == 422) {
        const errors = error.response.data as ValidationErrorsModel;
        throw new ValidationFailedError(errors.errors);
    } else if (error.response?.status == 401) {
        if (error.config.url != "/api/login") {
            // noinspection JSIgnoredPromiseFromCall
            router.push("/login");
        }
        throw new UserNotLoggedInError();
    } else if (error.response?.data.code && error.response?.data.message) {
        const model = error.response.data as ErrorCodeModel;
        throw new ServerError(model);
    } else {
        const model: ErrorCodeModel = {
            code: ErrorCode.UNKNOWN_ERROR,
            message: "Unknown error.",
            object: null,
            additionalData: null
        };
        throw new ServerError(model);
    }
}

function convertTestResponse(input: TestModel): TestModel {
    return DateService.convertFieldsToDate(input, "createdAt", "startedAt", "finishedAt", "startDueTime", "finishDueTime");
}

class ApiService {

    getAuthenticationDetails(): Promise<AuthenticationDetails> {
        return axios.get("/api/current-user/authentication-details").then((response) => response.data)
            .catch(errorHandler);
    }

    register(email: string, displayName: string, password: string) {
        const payload = {
            email,
            displayName,
            password
        };
        return axios.post("/api/register", payload)
            .catch(errorHandler);
    }

    login(email: string, password: string): Promise<AuthenticationDetails> {
        const data = {
            email,
            password
        };
        return axios.post("/api/login", data).then((response) => response.data)
            .catch(errorHandler);
    }

    logout(): Promise<unknown> {
        return axios.post("/api/logout")
            .catch(errorHandler);
    }

    updateSettings(request: UpdateSettingsRequest): Promise<unknown> {
        return axios.put("/api/current-user/update-settings", request)
            .catch(errorHandler);
    }

    changePassword(request: ChangePasswordRequest): Promise<unknown> {
        return axios.post("/api/current-user/change-password", request)
            .catch(errorHandler);
    }

    getWordLists(): Promise<Array<WordListModel>> {
        return axios.get("/api/word-lists")
            .then(response => response.data)
            .catch(errorHandler);
    }

    getWordList(id: number): Promise<WordListModel> {
        return axios.get(`/api/word-lists/${id}`)
            .then(response => response.data)
            .catch(errorHandler);
    }

    getTestDefinitions(): Promise<Array<TestDefinitionModel>> {
        return axios.get("/api/test-definitions?active=true")
            .then(response => response.data)
            .catch(errorHandler);
    }

    getTestDefinition(id: number): Promise<TestDefinitionModel> {
        return axios.get(`/api/test-definitions/${id}`)
            .then(response => response.data)
            .catch(errorHandler);
    }

    createTest(testDefinitionId: number): Promise<TestModel> {
        const payload = {
            testDefinitionId
        };

        return axios.post("/api/tests", payload)
            .then(response => convertTestResponse(response.data as TestModel))
            .catch(errorHandler);
    }

    regenerateTestWordList(testId: number): Promise<TestModel> {
        return axios.post<TestModel>(`/api/tests/${testId}/words`)
            .then(response => convertTestResponse(response.data))
            .catch(errorHandler);
    }

    startTest(testId: number): Promise<TestModel> {
        return axios.post<TestModel>(`/api/tests/${testId}/start`)
            .then(response => convertTestResponse(response.data))
            .catch(errorHandler);
    }

    finishTest(testId: number, payload: FinishTestRequest): Promise<TestModel> {
        return axios.post<TestModel>(`/api/tests/${testId}/finish`, payload)
            .then(response => convertTestResponse(response.data))
            .catch(errorHandler);
    }

    getTestResult(testId: number): Promise<TestResultModel> {
        return axios.get<TestResultModel>(`/api/tests/${testId}/result`)
            .then(response => response.data)
            .catch(errorHandler);
    }

    getTestBestResults(testDefinitionId: number, range: TestResultRangeModel): Promise<TestBestResultModel[]> {
        const params = {range};
        return axios.get<TestBestResultModel[]>(`/api/test-definitions/${testDefinitionId}/best-results`, {params})
            .then(response => DateService.convertFieldsToDate(response.data, "testCreatedAt"))
            .catch(errorHandler);
    }

    createWordList(name: string, language: string, type: WordListType): Promise<WordListModel> {
        const payload = {
            name,
            language,
            type
        };

        return axios.post<WordListModel>("/api/word-lists", payload)
            .then(response => response.data)
            .catch(errorHandler);
    }

    updateWordList(id: number, text: string): Promise<WordListModel> {
        const payload = {
            text
        }

        return axios.patch<WordListModel>(`/api/word-lists/${id}`, payload)
            .then(response => response.data)
            .catch(errorHandler);
    }

    deleteWordList(id: number): Promise<unknown> {
        return axios.delete(`/api/word-lists/${id}`)
            .catch(errorHandler);
    }

    exportWords(wordListId: number): Promise<unknown> {
        return axios.get(`/api/word-lists/${wordListId}/words/export`)
            .then(response => response.data)
            .catch(errorHandler);
    }

    importWords(request: ImportWordsRequest): Promise<unknown> {
        return axios.post(`/api/word-lists/${request.wordListId}/words/import`, request)
            .then(() => null)
            .catch(errorHandler);
    }

    createTestDefinition(request: CreateUpdateTestDefinitionRequest): Promise<unknown> {
        return axios.post("/api/test-definitions", request)
            .catch(errorHandler);
    }

    updateTestDefinition(id: number, request: CreateUpdateTestDefinitionRequest): Promise<unknown> {
        return axios.patch(`/api/test-definitions/${id}`, request)
            .catch(errorHandler);
    }

    getWordListWords(wordListId: number, pagedRequest: PagedRequest): Promise<WordListWordsModel> {
        const params = {
            page: pagedRequest.page,
            size: pagedRequest.size
        };

        return axios.get(`/api/word-lists/${wordListId}/words`, {params: params})
            .then(response => response.data)
            .catch(errorHandler);
    }

    createWord(wordListId: number, word: string, popularity: number): Promise<unknown> {
        const payload = {
            word,
            popularity
        };

        return axios.post(`/api/word-lists/${wordListId}/words`, payload)
            .then(response => response.data)
            .catch(errorHandler);
    }

    updateWordPopularity(wordListId: number, wordId: number, popularity: number): Promise<unknown> {
        const payload = {
            popularity
        };

        return axios.patch(`/api/word-lists/${wordListId}/words/${wordId}`, payload)
            .then(response => response.data)
            .catch(errorHandler);
    }

    deleteWord(wordListId: number, wordId: number): Promise<unknown> {
        return axios.delete(`/api/word-lists/${wordListId}/words/${wordId}`)
            .catch(errorHandler);
    }

    getTestDefinitionsWithStats(): Promise<Array<TestDefinitionModel>> {
        return axios.get("/api/test-results/stats/test-definitions")
            .then(response => response.data)
            .catch(errorHandler);
    }

    getStatsForTestDefinition(testDefinitionId: number): Promise<TestStatsModel> {
        return axios.get(`/api/test-results/stats/test-definitions/${testDefinitionId}`)
            .then(response => response.data)
            .catch(errorHandler);
    }

    searchUsers(request: SearchUsersRequest): Promise<SearchUsersResponse> {
        return axios.post<SearchUsersResponse>("/api/users/search", request)
            .then(response => DateService.convertFieldsToDate(response.data, "content.createdAt"))
            .catch(errorHandler);
    }

    activateUser(userId: number): Promise<unknown> {
        return axios.put(`/api/users/${userId}/active`)
            .catch(errorHandler);
    }

    deleteUser(userId: number): Promise<unknown> {
        return axios.delete(`/api/users/${userId}`)
            .catch(errorHandler);
    }
}

export default new ApiService();
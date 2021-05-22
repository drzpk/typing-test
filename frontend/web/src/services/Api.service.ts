import axios, {AxiosError} from "axios";
import {AuthenticationDetails, ChangePasswordRequest, UpdateSettingsRequest} from "@/models/user";
import {ErrorCode, ErrorCodeModel, ServerError, ValidationErrorsModel, ValidationFailedError} from "@/models/error";
import {WordList, WordListWordsResponse} from "@/models/words";
import {PagedRequest} from "@/models/pagination";
import {CreateUpdateTestDefinitionRequest, TestDefinitionModel} from "@/models/test-definition";
import {TestModel} from "@/models/tests";

function errorHandler(error: AxiosError): never {
    if (error.response?.status == 422) {
        const errors = error.response.data as ValidationErrorsModel;
        throw new ValidationFailedError(errors.errors);
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

function convertFieldsToDate<T>(object: T, ...fields: Array<string>): T {
    const map = object as any;
    for (let i = 0; i < fields.length; i++) {
        const field = map[fields[i]];
        if (typeof (field) === "number")
            map[fields[i]] = new Date(field * 1000);
    }
    return object;
}

class ApiService {

    getAuthenticationDetails(): Promise<AuthenticationDetails> {
        return axios.get("/api/current-user/authentication-details").then((response) => response.data)
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

    updateSettings(request: UpdateSettingsRequest): Promise<any> {
        return axios.put("/api/current-user/update-settings", request)
            .catch(errorHandler);
    }

    changePassword(request: ChangePasswordRequest): Promise<any> {
        return axios.post("/api/current-user/change-password", request)
            .catch(errorHandler);
    }

    getWordLists(): Promise<Array<WordList>> {
        return axios.get("/api/word-lists")
            .then(response => response.data)
            .catch(errorHandler);
    }

    getTestDefinitions(): Promise<Array<TestDefinitionModel>> {
        return axios.get("/api/test-definitions")
            .then(response => response.data)
            .catch(errorHandler);
    }

    createTest(testDefinitionId: number): Promise<TestModel> {
        const payload = {
            testDefinitionId
        };

        return axios.post("/api/tests", payload)
            .then(response => convertFieldsToDate(response.data as TestModel, "createdAt", "startedAt", "finishedAt", "startDueTime", "finishDueTime"))
            .catch(errorHandler);
    }

    createWordList(name: string, language: string): Promise<any> {
        const payload = {
            name,
            language
        };

        return axios.post("/api/word-lists", payload)
            .catch(errorHandler);
    }

    createTestDefinition(request: CreateUpdateTestDefinitionRequest): Promise<any> {
        return axios.post("/api/test-definitions", request)
            .catch(errorHandler);
    }

    updateTestDefinition(id: number, request: CreateUpdateTestDefinitionRequest): Promise<any> {
        return axios.patch(`/api/test-definitions/${id}`, request)
            .catch(errorHandler);
    }

    getWordListWords(wordListId: number, pagedRequest: PagedRequest): Promise<WordListWordsResponse> {
        const params = {
            page: pagedRequest.page,
            size: pagedRequest.size
        };

        return axios.get(`/api/word-lists/${wordListId}/words`, {params: params})
            .then(response => response.data)
            .catch(errorHandler);
    }

    createWord(wordListId: number, word: string, popularity: number): Promise<any> {
        const payload = {
            word,
            popularity
        };

        return axios.post(`/api/word-lists/${wordListId}/words`, payload)
            .then(response => response.data)
            .catch(errorHandler);
    }

    updateWordPopularity(wordListId: number, wordId: number, popularity: number): Promise<any> {
        const payload = {
            popularity
        };

        return axios.patch(`/api/word-lists/${wordListId}/words/${wordId}`, payload)
            .then(response => response.data)
            .catch(errorHandler);
    }

    deleteWord(wordListId: number, wordId: number): Promise<any> {
        return axios.delete(`/api/word-lists/${wordListId}/words/${wordId}`)
            .catch(errorHandler);
    }
}

export default new ApiService();
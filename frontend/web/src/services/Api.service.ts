import axios, {AxiosError} from "axios";
import {
    AuthenticationDetails,
    ChangePasswordRequest,
    SearchUsersRequest,
    SearchUsersResponse,
    UpdateSettingsRequest
} from "@/models/user";
import {ErrorCode, ErrorCodeModel, ServerError, ValidationErrorsModel, ValidationFailedError} from "@/models/error";
import {WordList, WordListWordsResponse} from "@/models/words";
import {PagedRequest} from "@/models/pagination";
import {CreateUpdateTestDefinitionRequest, TestDefinitionModel} from "@/models/test-definition";
import {FinishTestRequest, TestModel, TestResultModel} from "@/models/tests";
import {TestStatsModel} from "@/models/test-stats";

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
        convertFieldToDate(map, fields[i]);
    }
    return object;
}

function convertFieldToDate(object: any, fieldPath: string) {
    const parts = fieldPath.split(".");
    const fieldName = parts[0];
    const isTerminal = parts.length == 1;
    const fieldValue = object[fieldName];

    const remainingParts: string | null = !isTerminal ? parts.slice(1, parts.length).join(".") : null;

    if (isTerminal && typeof (fieldValue) === "number") {
        object[fieldName] = convertToDate(fieldValue);
    } else if (!isTerminal && Array.isArray(fieldValue)) {
        const array = fieldValue as Array<any>;
        for (let i = 0; i < array.length; i++) {
            convertFieldToDate(array[i], remainingParts!);
        }
    } else if (!isTerminal && typeof (fieldValue) === "object" && fieldValue !== null) {
        convertFieldToDate(fieldValue, remainingParts!);
    }
}

function convertToDate(timestampSeconds: number): string {
    function pad(input: number): string {
        return input.toString().padStart(2, "0");
    }

    const dateObj = new Date(timestampSeconds * 1000);
    const date = `${dateObj.getFullYear()}-${pad(dateObj.getMonth() + 1)}-${pad(dateObj.getDate())}`;
    const time = `${pad(dateObj.getHours())}:${pad(dateObj.getMinutes())}`;
    return `${date} ${time}`;
}

function convertTestResponse(input: TestModel): TestModel {
    return convertFieldsToDate(input, "createdAt", "startedAt", "finishedAt", "startDueTime", "finishDueTime");
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
            .then(response => convertFieldsToDate(response.data, "content.createdAt"))
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
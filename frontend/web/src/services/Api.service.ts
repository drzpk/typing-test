import axios, {AxiosError} from "axios";
import {AuthenticationDetails, ChangePasswordRequest, UpdateSettingsRequest} from "@/models/user";
import {ValidationErrors, ValidationFailedError} from "@/models/error";
import {WordList, WordListWordsResponse} from "@/models/words";
import {PagedRequest} from "@/models/pagination";

function validationErrorHandler(error: AxiosError) {
    if (error.response?.status == 422) {
        const errors = error.response.data as ValidationErrors;
        throw new ValidationFailedError(errors.errors);
    }

    throw error;
}

class ApiService {

    getAuthenticationDetails(): Promise<AuthenticationDetails> {
        return axios.get("/api/current-user/authentication-details").then((response) => response.data)
            .catch(validationErrorHandler);
    }

    login(email: string, password: string): Promise<AuthenticationDetails> {
        const data = {
            email,
            password
        };
        return axios.post("/api/login", data).then((response) => response.data)
            .catch(validationErrorHandler);
    }

    updateSettings(request: UpdateSettingsRequest): Promise<any> {
        return axios.put("/api/current-user/update-settings", request)
            .catch(validationErrorHandler);
    }

    changePassword(request: ChangePasswordRequest): Promise<any> {
        return axios.post("/api/current-user/change-password", request)
            .catch(validationErrorHandler);
    }

    getWordLists(): Promise<Array<WordList>> {
        return axios.get("/api/word-lists")
            .then(response => response.data)
            .catch(validationErrorHandler);
    }

    createWordList(name: string, language: string): Promise<any> {
        const payload = {
            name,
            language
        };

        return axios.post("/api/word-lists", payload)
            .catch(validationErrorHandler);
    }

    getWordListWords(wordListId: number, pagedRequest: PagedRequest): Promise<WordListWordsResponse> {
        const params = {
            page: pagedRequest.page,
            size: pagedRequest.size
        };

        return axios.get(`/api/word-lists/${wordListId}/words`, {params: params})
            .then(response => response.data)
            .catch(validationErrorHandler);
    }

    createWord(wordListId: number, word: string, popularity: number): Promise<any> {
        const payload = {
            word,
            popularity
        };

        return axios.post(`/api/word-lists/${wordListId}/words`, payload)
            .then(response => response.data)
            .catch(validationErrorHandler);
    }

    updateWordPopularity(wordListId: number, wordId: number, popularity: number): Promise<any> {
        const payload = {
            popularity
        };

        return axios.patch(`/api/word-lists/${wordListId}/words/${wordId}`, payload)
            .then(response => response.data)
            .catch(validationErrorHandler);
    }

    deleteWord(wordListId: number, wordId: number): Promise<any> {
        return axios.delete(`/api/word-lists/${wordListId}/words/${wordId}`)
            .catch(validationErrorHandler);
    }
}

export default new ApiService();
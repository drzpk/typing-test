import axios, {AxiosError} from "axios";
import {AuthenticationDetails, ChangePasswordRequest, UpdateSettingsRequest} from "@/models/user";
import {ValidationErrors, ValidationFailedError} from "@/models/error";

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
}

export default new ApiService();
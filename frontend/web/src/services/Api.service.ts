import axios from "axios";
import {AuthenticationDetails, ChangePasswordRequest, UpdateSettingsRequest} from "@/models/user";

class ApiService {

    getAuthenticationDetails(): Promise<AuthenticationDetails> {
        return axios.get("/api/current-user/authentication-details").then((response) => response.data);
    }

    login(email: string, password: string): Promise<AuthenticationDetails> {
        const data = {
            email,
            password
        };
        return axios.post("/api/login", data).then((response) => response.data);
    }

    updateSettings(request: UpdateSettingsRequest): Promise<void> {
        return axios.put("/api/current-user/update-settings", request);
    }

    changePassword(request: ChangePasswordRequest): Promise<void> {
        return axios.post("/api/current-user/change-password", request);
    }
}

export default new ApiService();
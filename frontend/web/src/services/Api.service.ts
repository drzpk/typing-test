import axios from "axios";
import {AuthenticationDetails} from "@/models/user";

class ApiService {
    getAuthenticationDetails(): Promise<AuthenticationDetails> {
        return axios.get(`/api/authentication-details`).then((response) => response.data);
    }

    login(email: string, password: string): Promise<AuthenticationDetails> {
        const data = {
            email,
            password
        };
        return axios.post(`/api/login`, data).then((response) => response.data);
    }
}

export default new ApiService();
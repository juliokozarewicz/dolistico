import axios from "axios";
import config from "../config.json";

const baseURL = axios.create({baseURL: config.BASE_URL_API,});

export async function updatePassword(
    
    token: string,
    newPassword: string

) {

    const response = await baseURL.patch("/v1/accounts/password/update/confirm", {
        token,
        newPassword,
    });

    return response.data;

}
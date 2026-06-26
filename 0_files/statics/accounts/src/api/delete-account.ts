import axios from "axios";
import config from "../config.json";

const baseURL = axios.create({baseURL: config.BASE_URL_API,});

export async function deleteAccount(

    token: string,
    password: string

) {

    const response = await baseURL.delete("/v1/accounts/delete/confirm", {
        data: {
            token,
            password,
        },
    });

    return response.data;

}
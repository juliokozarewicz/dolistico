import axios from "axios";
import config from "../config.json";

const baseURL = axios.create({baseURL: config.BASE_URL_API,});

export async function updateEmail(

    token: string,
    pin: string

) {

    const response = await baseURL.patch("/v1/accounts/email/update/confirm", {
        token,
        pin,
    });

    return response.data;

}
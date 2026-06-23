import { api } from "./config";

export async function updatePassword(
    
    token: string,
    newPassword: string

) {

    const response = await api.patch("/v1/accounts/password/update/confirm", {
        token,
        newPassword,
    });

    return response.data;

}
# GET http://localhost:8082/payments
# GET http://localhost:8082/payments/ac328a1a-1e60-4dd3-bee5-ed573d74c841
# GET http://localhost:8082/payments/search

// Prepare a request to Keycloak for getting access token
const request = {
    url: "http://localhost:8085/realms/iprody-lms/protocol/openid-connect/token",
    method: 'POST',
    header: {
        'Accept': '*/*',
        'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: {
        mode: 'urlencoded',
        urlencoded: [
            { key: 'client_id', value: 'basic_client' },
            { key: 'grant_type', value: 'password' },
            { key: 'username', value: 'admin' },
            { key: 'password', value: 'admin_password' },
            { key: 'client_secret', value: 'client-secret' }
        ]
    }
};

// Send the request and store access_token and expiration time in the environment variables
pm.sendRequest(request,  function (err, response) {

    const body = response.json();

    console.info(body.access_token);

    pm.environment.set("accessToken", body.access_token);
    pm.environment.set("refreshToken", body.refresh_token);
});

export const oktaConfig = {
    clientId: '0oaf9y63nslf8OovQ5d7',
    issuer: 'https://dev-48534325.okta.com/oauth2/default',
    redirectUri: 'http://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,
}
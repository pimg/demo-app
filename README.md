# YTS Demo App


## Generated Sources

Run Maven compile step to generate DTOs based on Swagger JSON spec
`resources/swagger/swagger.json`


## YTS Account Setup

The app needs two keystores to connect successfully to YTS.

* keystore.p12 - TLS certificate signing
* signing-keystore.p12 - for access token requests

When you check out the app, it should be possible to run it and make requests to YTS Sandbox.

If you want to generate you own, follow these steps.

### Keystores

Follow the instructions on https://developer.yolt.com/docs/connect-to-yts-tutorial

The steps for `Request token public key` will result in 2 files:

* private-key.pem
* public-key.pem (paste this into `Request token public key`)

The steps for `Mutual TLS certificate signing request` will result in 2 files:

* tls-csr.pem  (paste this into `Mutual TLS CSR`)
* tls-private-key.pem

After successfully creating a YTS client, copy the content of the text box
`MUTUAL TLS CERTIFICATE` into a new file `tls-mutual-certificate.pem`.

Generate the `.p12` files as follows:

`openssl pkcs12 -export -out keystore.p12 -in tls-mutual-certificate.pem -inkey tls-private-key.pem -name demo-app-tls -passout pass:123`

`openssl pkcs12 -export -out signing-keystore.p12 -inkey private-key.pem -name demo-app-signing -nocerts -passout pass:123`

These files need to be placed under `resources/tls`.

### application.properties

* `client.id`- paste the value of `CLIENT_ID`
* `client.signing.request.token.id` - paste the value of `REQUEST_TOKEN_PUBLIC_KEY_ID`
* `redirect.url.id` - paste the value of `REDIRECT_URL_ID`
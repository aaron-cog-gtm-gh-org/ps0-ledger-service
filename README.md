# Ledger Service

The Acme Bank ledger service provides account balance reads to internal finance operations and settlement workflows. It retrieves the current balance from the core banking service and retains the latest successful result in process memory for continuity during a core banking connectivity interruption.

## Core banking integration

For an account such as `ACC-100234`, the service requests `GET /v1/accounts/ACC-100234/balance` from the configured core banking endpoint. The response includes the account identifier, currency, available balance, ledger balance, and the time at which the balance was recorded.

If core banking cannot be reached, a previously retrieved balance is returned when available. HTTP responses such as a missing account are returned as service errors rather than treated as connectivity interruptions.

## Configuration

| Key | Description |
| --- | --- |
| `acme.core-banking.base-url` | Internal core banking API base URL |
| `acme.core-banking.timeout` | Connection and response timeout |

The default configuration targets `https://core-banking.internal.acmebank.example` with a five-second timeout. Override these values for the target environment.

## Build

Set `ARTIFACTORY_URL` to the Acme Artifactory `maven-virtual` repository and run:

```sh
ARTIFACTORY_URL=https://artifactory.internal.acmebank.example/artifactory/maven-virtual mvn -B -s .mvn/settings.xml verify
```

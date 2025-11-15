# Step 1: Project Setup (REQUIRED)

## Bug Investigation: “Axios Error” when creating an account (Codespaces + CORS)

### Summary
Users testing account creation saw an Axios Error on login/signup. Directly opening the backend URL in a browser (replacing localhost with the Codespace domain) returned valid JSON (e.g., Alice with id, firstName, etc.), so the API worked. The problem was a mismatch between the frontend API base URL and the backend CORS settings.

### Root cause
The frontend initially targeted http://localhost:8080, while the app was actually running in Codespaces at https://<codespace>-8080.app.github.dev.

The backend CORS allowed http://localhost:3000 / http://localhost:5173, but not the Codespace UI origins (e.g., https://<codespace>-3000.app.github.dev, https://<codespace>-5173.app.github.dev).

Result: cross-origin requests were blocked by CORS.

### Fix (implemented)
Frontend: read the API base URL from an env var; include /api in the base and a localhost fallback. Relevant changes were made to api.ts, and new .env and vite-env.d.ts files were created.

Backend: allow Codespace UI origins in CORS and keep methods/headers open for dev. Relevant changes were made to WebConfig.java.

### Why this works
The frontend now points at the real API host (VITE_API_BASE_URL), and the backend explicitly trusts the UI origins used in dev/Codespaces, so CORS preflight succeeds and Axios calls complete.

### Additional Fixes

The default Codespaces environment did not include the correct versions of Java, Maven, or Node required by the project. A custom .devcontainer/devcontainer.json configuration was added to the backend to ensure consistent tooling (Java 21, Maven, and Node 18) across all Codespaces instances.

OpenAPI was originally configured for production-only URLs, which caused Swagger UI to fail when running inside Codespaces. To resolve this, the active Codespace hostname was added to OpenApiConfig, enabling Swagger’s Try it out functionality to correctly point at the Codespace backend.

Additionally, Springdoc was updated in application.properties to automatically use the Codespace server URL via the springdoc.swagger-ui.server-url property.

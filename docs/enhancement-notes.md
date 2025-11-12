# Step 3: Implement Missing Functionality

## Enhancement 1: Advanced Trade Search System

### Enhancement(trade): Enhancement 1 - Multi-criteria search
- Used logger.debug for enhancement retrieval operations in service layer to reduce noise in production. Recommendation for all retrieval operations to use logger.debug and only business operations to use logger.info.

- Used "/search/counterparty/{variable}" rather than /search/{variable}" as it’s more descriptive, explicit, and extensible. The /search endpoint family might grow (for book, and date searches etc).

### Enhancement(trade): Enhancement 1 - Paginated filtering

### Enhancement(trade): Enhancement 1 - RSQL query support
- Added RSQL parsing support and JPA Specification integration via external library.

- Introduced RsqlAliasConfig to rewrite user-friendly query aliases into entity field paths (case-insensitive on alias keys).

- Updated GlobalExceptionHandler to provide clearer responses for RSQL syntax and unknown-field errors.

- Full case-insensitive value matching is not natively supported by the RSQL library.
Implementing a universal lower-case comparison visitor would require custom parsing logic.
Recommendation: use a case-insensitive DB collation or enforce canonical casing on input values for consistent behavior.

## Enhancement 2: Comprehensive Trade Validation Engine

### Enhancement(trade): Enhancement 2 - User Privilege Enforcement
public boolean validateUserPrivileges(String userId, String operation, TradeDTO tradeDTO) -> public boolean validateUserPrivileges(String operation, TradeDTO tradeDTO)

- Removed the userId parameter from validateUserPrivileges to avoid redundant input and prevent mismatches. The identity of the logged-in user (inputterUserName) is derived internally. Security recommendation: The authenticated user should be resolved from the security context (e.g., loginId → inputterUserName), not selected by the client UI. This prevents users from spoofing identities by choosing another username from a dropdown.

- TRADER_SALES is a single user type in the database, but business rules require distinct privileges for TRADER and SALES. For security, I have not implemented SALES-specific logic and have restricted traders from seeing other traders' trades. Recommendation: make TRADER and SALES distinct user types in the database to allow fine-grained privilege enforcement.

- validateUserPrivileges is seperate method, called at the top of createTrade(). This approach is modular, clear and reusable. The method can also be called at the top of other relevant service methods, such as amendTrade etc.

### Enhancement(trade): Enhancement 2 - Date Validation Rules

### Enhancement(trade): Enhancement 2 - Cross-Leg Business Rules
public ValidationResult validateTradeLegConsistency(List<TradeLegDTO> legs) -> public ValidationResult validateTradeLegConsistency(TradeDTO tradeDTO)

- Changed method parameter from List<TradeLegDTO> -> TradeDTO. Both validateTradeCreation() and createTrade(), from which validateTradeLegConsistency will be called, already use TradeDTO as their parameter. All trade leg and cashflow data needed for consistency checks are accessible via tradeDTO. This ensures the method signatures are consistent and reduces the need to extract legs separately before validation.

- Maturity is controlled exclusively by the trade object, and legs do not carry independent maturity dates. Therefore, DTO-level maturity checks on legs are intentionally omitted. Backend lifecycle logic is implicitly trusted to apply the trade maturity consistently across both legs, and to ensure alignment during cashflow generation. 

### Enhancement(trade): Enhancement 2 - Entity Status Validation
- Reference-data validation previously performed in validateReferenceData(trade) (entity-level) has been moved into validateTradeDTOReferenceData() and invoked as part of validateTradeCreation(tradeDTO). Validating against the DTO ensures errors are detected before any entities are instantiated or persisted, preventing partial data creation and improving user feedback. System-generated reference fields (e.g., trade status and backend-assigned values) are not validated at DTO stage; the backend enforces these rules and guarantees correct population post-creation.

- Checks to ensure that the trader, book, and counterparty are active in the system are now performed in validateTradeCreation(). Recommendation: the UI should restrict dropdown options to active entities only. This prevents users from selecting invalid values, reduces avoidable validation failures, and avoids unnecessary server processing.

- Active-status checks are performed only for the trader user (the economic owner of the trade), not for the inputter or the authenticated user. Trade creation is restricted by validateUserPrivileges(), which ensures only the the economic owner of the trade can submit trades, so additional active-status checks on the inputter or logged-in user at trade creation are not required. Per prior design guidance, only active application users should be allowed to authenticate in the UI, ensuring system access and trade privileges are aligned.

- Remaining reference-data validation rules were derived from the fields defined in TradeDTO, UI requirements, and the backend reference-population logic (populateReferenceDataByName(), populateUserReferences(), populateTradeTypeReferences(), and populateLegReferenceData()). Leg-specific reference rules identified in populateLegReferenceData() were implemented in validateTradeLegConsistency(). All other reference-data requirements were added to validateTradeDTOReferenceData().

- Trade status, fixed-leg type, holiday calendar, schedule, and business-day conventions have been excluded from DTO-level validation because these fields are system-assigned rather than user-supplied. These values are enforced by backend logic at assignment time; validation should be added if they ever become user-editable.

- UTI validation is not enforced at this stage because current database values are internal test placeholders rather than industry-format UTIs. UTIs are optional at trade entry; if a value is provided, validation will apply once industry-formatted UTIs are introduced.

- Removed manual try-catch block from TradeController#createTrade(). The catch block was masking domain and validation exceptions, always returning a generic 400 response and preventing the global exception handler from producing meaningful error feedback. Allowing exceptions to bubble into @ControllerAdvice ensures correct status codes (e.g., 403/422/500) and preserves detailed error messages for both users and logs.

- Trade creation logic has been centralised in the service layer. This ensures validation runs before entity creation, eliminates duplicate mapping and redundant DB lookups, and improves separation of concerns. The controller now receives the DTO and delegates trade creation to the service.

- Front-end validation in tradeUtils.ts was temporarily disabled to ensure all validation logic is exercised and enforced by the backend during development and testing.


## Enhancement 3: Trader Dashboard and Blotter System




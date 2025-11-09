# Enhancement: Advanced Trade Search System


Enhancement(trade): Enhancement 1 - Multi-criteria search

- Used logger.debug for enhancement retrieval operations in service layer to reduce noise in production. Recommendation for all retrieval operations to use logger.debug and only business operations to use logger.info.

- Used "/search/counterparty/{variable}" rather than /search/{variable}" as it’s more descriptive, explicit, and extensible. The /search endpoint family might grow (for book, and date searches etc).

Enhancement(trade): Enhancement 1 - Paginated filtering

Enhancement(trade): Enhancement 1 - RSQL query support

- Added RSQL parsing support and JPA Specification integration via external library.

- Introduced RsqlAliasConfig to rewrite user-friendly query aliases into entity field paths (case-insensitive on alias keys).

- Updated GlobalExceptionHandler to provide clearer responses for RSQL syntax and unknown-field errors.

- Full case-insensitive value matching is not natively supported by the RSQL library.
Implementing a universal lower-case comparison visitor would require custom parsing logic.
Recommendation: use a case-insensitive DB collation or enforce canonical casing on input values for consistent behavior.

Enhancement(trade): Enhancement 2 - Comprehensive Trade Validation Engine

User Privilege Enforcement:
public boolean validateUserPrivileges(String operation, TradeDTO tradeDTO) -> public boolean validateUserPrivileges(String operation, TradeDTO tradeDTO)

- Removed the userId parameter from validateUserPrivileges to avoid redundant input and prevent mismatches. The identity of the logged-in user (inputterUserName) is derived internally. Security recommendation: The authenticated user should be resolved from the security context (e.g., loginId → inputterUserName), not selected by the client UI. This prevents users from spoofing identities by choosing another username from a dropdown.

- TRADER_SALES is a single user type in the database, but business rules require distinct privileges for TRADER and SALES. For security, I have not implemented SALES-specific logic and restrict traders from seeing other traders' trades. Recommendation: make TRADER and SALES distinct user types in the database to allow fine-grained privilege enforcement.

- validateUserPrivileges is seperate method, called at the top of createTrade(). This approach is modular, clear and reusable. The method can also be called at the top of other relevant service methods, such as amendTrade etc.

Date Validation Rules:

Cross-Leg Business Rules:
public ValidationResult validateTradeLegConsistency(List<TradeLegDTO> legs) -> public ValidationResult validateTradeLegConsistency(TradeDTO tradeDTO)

- Changed method parameter from List<TradeLegDTO> -> TradeDTO. Both validateTradeCreation() and createTrade(), from which validateTradeLegConsistency will be called, already use TradeDTO as their parameter. All trade leg and cashflow data needed for consistency checks are accessible via tradeDTO.
This ensures the method signatures are consistent and reduces the need to extract legs separately before validation.

Entity Status Validation:
User, book, and counterparty must be active in the system
All reference data must exist and be valid

- Reference data validation in validateReferenceData(trade) has been moved into validateTradeCreation(tradeDTO). Early validation of reference data using the DTO allows errors to be caught before any entities are created or persisted. This avoids any partially created entities if validation fails and provides better feedback to the user before any database changes.

- Checks to ensure that the user, book, and counterparty are active in the system are now handled within validateTradeCreation(). However, I recommend that the UI restrict the dropdown list to display only active entities. This prevents users from selecting invalid options, reduces validation failures server-side, and avoids unnecessary processing overhead.

- Active-status checks are performed only for the trader (the economic owner of the trade), not for the inputter or logged-in user. This aligns with the prior design note. Only active application users should be allowed to authenticate in the UI. Therefore, additional active-status checks for the inputter or logged-in user at trade-creation time are not required, since inactive users would already be prevented from accessing the system.










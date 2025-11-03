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

Date Validation Rules:
public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO)

Maturity date cannot be before start date or trade date
Start date cannot be before trade date
Trade date cannot be more than 30 days in the past

User Privilege Enforcement:
public boolean validateUserPrivileges(String userId, String operation, TradeDTO tradeDTO)

TRADER: Can create, amend, terminate, cancel trades
SALES: Can create and amend trades only (no terminate/cancel)
MIDDLE_OFFICE: Can amend and view trades only
SUPPORT: Can view trades only

Cross-Leg Business Rules:
public ValidationResult validateTradeLegConsistency(List<TradeLegDTO> legs)

Both legs must have identical maturity dates
Legs must have opposite pay/receive flags
Floating legs must have an index specified
Fixed legs must have a valid rate

Entity Status Validation:

User, book, and counterparty must be active in the system
All reference data must exist and be valid









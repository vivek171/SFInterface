IF OBJECT_ID('oauth_client_details', 'U') IS NOT NULL
  DROP TABLE oauth_client_details;

CREATE TABLE oauth_client_details (
  client_id               VARCHAR(255) PRIMARY KEY,
  resource_ids            VARCHAR(MAX),
  client_secret           VARCHAR(MAX),
  scope                   VARCHAR(MAX),
  authorized_grant_types  VARCHAR(MAX),
  web_server_redirect_uri VARCHAR(MAX),
  authorities             VARCHAR(MAX),
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(MAX)
);

IF OBJECT_ID('oauth_client_token', 'U') IS NOT NULL
  DROP TABLE oauth_client_token;
CREATE TABLE oauth_client_token (
  token_id          VARCHAR(MAX),
  token             VARCHAR(MAX),
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name         VARCHAR(MAX),
  client_id         VARCHAR(MAX)
);

IF OBJECT_ID('oauth_access_token', 'U') IS NOT NULL
  DROP TABLE oauth_access_token;
CREATE TABLE oauth_access_token (
  token_id          VARCHAR(MAX),
  token             VARCHAR(MAX),
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name         VARCHAR(MAX),
  client_id         VARCHAR(MAX),
  authentication    VARCHAR(MAX),
  refresh_token     VARCHAR(MAX)
);

IF OBJECT_ID('oauth_refresh_token', 'U') IS NOT NULL
  DROP TABLE oauth_refresh_token;
CREATE TABLE oauth_refresh_token (
  token_id       VARCHAR(MAX),
  token          VARCHAR(MAX),
  authentication VARCHAR(MAX)
);

IF OBJECT_ID('oauth_code', 'U') IS NOT NULL
  DROP TABLE oauth_code;
CREATE TABLE oauth_code (
  code           VARCHAR(MAX),
  authentication VARCHAR(MAX)
);

IF OBJECT_ID('oauth_approvals', 'U') IS NOT NULL
  DROP TABLE oauth_approvals;
CREATE TABLE oauth_approvals (
  userId         VARCHAR(MAX),
  clientId       VARCHAR(MAX),
  scope          VARCHAR(MAX),
  status         VARCHAR(10),
  expiresAt      DATETIME,
  lastModifiedAt DATETIME
);

IF OBJECT_ID('ClientDetails', 'U') IS NOT NULL
  DROP TABLE ClientDetails;

CREATE TABLE ClientDetails (
  appId                  VARCHAR(255) PRIMARY KEY,
  resourceIds            VARCHAR(MAX),
  appSecret              VARCHAR(MAX),
  scope                  VARCHAR(MAX),
  grantTypes             VARCHAR(MAX),
  redirectUrl            VARCHAR(MAX),
  authorities            VARCHAR(MAX),
  access_token_validity  INTEGER,
  refresh_token_validity INTEGER,
  additionalInformation  VARCHAR(4096),
  autoApproveScopes      VARCHAR(MAX)
);

IF OBJECT_ID('persistent_logins', 'U') IS NOT NULL
  DROP TABLE persistent_logins;
CREATE TABLE  persistent_logins (
  username  VARCHAR(100) NOT NULL,
  series    VARCHAR(64) PRIMARY KEY,
  token     VARCHAR(64)  NOT NULL,
  last_used DATETIME    NOT NULL
);


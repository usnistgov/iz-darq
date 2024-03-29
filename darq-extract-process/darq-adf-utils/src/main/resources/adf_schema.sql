CREATE TABLE V_DETECTIONS (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, DETECTION_CODE TINYINT, P INTEGER, N MEDIUMINT UNSIGNED, PRIMARY KEY (PROVIDER_ID, AGE_GROUP, DETECTION_CODE)) WITHOUT ROWID
CREATE TABLE V_VOCAB (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, VS TINYINT, CODE MEDIUMINT, N MEDIUMINT UNSIGNED, PRIMARY KEY (PROVIDER_ID, AGE_GROUP, VS, CODE))  WITHOUT ROWID
CREATE TABLE V_EVENTS (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, YEAR TINYINT, GENDER TINYINT, SOURCE TINYINT, CODE TINYINT, N MEDIUMINT UNSIGNED, PRIMARY KEY (PROVIDER_ID, AGE_GROUP, YEAR, GENDER, SOURCE, CODE))  WITHOUT ROWID
CREATE TABLE P_PROVIDER_DETECTIONS (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, DETECTION_CODE TINYINT, P MEDIUMINT UNSIGNED, N MEDIUMINT UNSIGNED, PRIMARY KEY (PROVIDER_ID, AGE_GROUP, DETECTION_CODE))  WITHOUT ROWID
CREATE TABLE P_PROVIDER_VOCAB (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, VS TINYINT, CODE MEDIUMINT, N MEDIUMINT UNSIGNED, PRIMARY KEY (PROVIDER_ID, AGE_GROUP, VS, CODE))  WITHOUT ROWID
CREATE TABLE P_DETECTIONS (AGE_GROUP TINYINT, DETECTION_CODE TINYINT, P MEDIUMINT UNSIGNED, N MEDIUMINT UNSIGNED, PRIMARY KEY (AGE_GROUP, DETECTION_CODE))  WITHOUT ROWID
CREATE TABLE P_VOCAB (AGE_GROUP TINYINT, VS TINYINT, CODE MEDIUMINT, N MEDIUMINT UNSIGNED, PRIMARY KEY (AGE_GROUP, VS, CODE))  WITHOUT ROWID
CREATE TABLE METADATA (INFO BLOB, SUMMARY BLOB, CONFIGURATION BLOB, ADF_VERSION TEXT)
CREATE TABLE DICTIONARY (ID INT, DICT BLOB, PRIMARY KEY (ID))
CREATE TABLE SEC (KEY_HASH BLOB, _KEY BLOB)
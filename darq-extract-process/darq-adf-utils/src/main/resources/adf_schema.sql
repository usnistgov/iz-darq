CREATE TABLE V_DETECTIONS (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, DETECTION_CODE TINYINT, P INTEGER, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX V_DETECTIONS_KEY ON V_DETECTIONS (PROVIDER_ID, AGE_GROUP, DETECTION_CODE)
CREATE TABLE V_VOCAB (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, VS TINYINT, CODE MEDIUMINT, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX V_VOCAB_KEY ON V_VOCAB (PROVIDER_ID, AGE_GROUP, VS, CODE)
CREATE TABLE V_EVENTS (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, YEAR TINYINT, GENDER TINYINT, SOURCE TINYINT, CODE TINYINT, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX V_EVENTS_KEY ON V_EVENTS (PROVIDER_ID, AGE_GROUP, YEAR, GENDER, SOURCE, CODE)
CREATE TABLE P_PROVIDER_DETECTIONS (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, DETECTION_CODE TINYINT, P MEDIUMINT UNSIGNED, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX P_PROVIDER_DETECTIONS_KEY ON P_PROVIDER_DETECTIONS (PROVIDER_ID, AGE_GROUP, DETECTION_CODE)
CREATE TABLE P_PROVIDER_VOCAB (PROVIDER_ID MEDIUMINT, AGE_GROUP TINYINT, VS TINYINT, CODE MEDIUMINT, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX P_PROVIDER_VOCAB_KEY ON P_PROVIDER_VOCAB (PROVIDER_ID, AGE_GROUP, VS, CODE)
CREATE TABLE P_DETECTIONS (AGE_GROUP TINYINT, DETECTION_CODE TINYINT, P MEDIUMINT UNSIGNED, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX P_DETECTIONS_KEY ON P_DETECTIONS (AGE_GROUP, DETECTION_CODE)
CREATE TABLE P_VOCAB (AGE_GROUP TINYINT, VS TINYINT, CODE MEDIUMINT, N MEDIUMINT UNSIGNED)
CREATE UNIQUE INDEX P_VOCAB_KEY ON P_VOCAB (AGE_GROUP, VS, CODE)
CREATE TABLE METADATA (SUMMARY TEXT, COUNTS TEXT, _KEY TEXT, ADF_VERSION TEXT)
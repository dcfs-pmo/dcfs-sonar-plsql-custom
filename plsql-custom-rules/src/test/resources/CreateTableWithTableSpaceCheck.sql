CREATE TABLE FM_COMM_PARA -- Noncompliant
(
   UNIT_ID      VARCHAR2 (10 CHAR) NOT NULL,
   UNIT_DESC    VARCHAR2 (100 CHAR),
   PARA_VALUE   VARCHAR2 (500 CHAR) NOT NULL,
   PARA_DESC    VARCHAR2 (100 CHAR)
);

CREATE INDEX UNIT ON FM_COMM_PARA(UNIT_ID); -- Noncompliant


CREATE TABLE "SYMBOLS"."RB_CHEQUE_DEAL"  -- Noncompliant
   (	"MISC_HIST_SEQ_NO" NUMBER(12,0), 
	"DENOMINATION" NUMBER, 
	"PREFIX" VARCHAR2(30 CHAR), 
	"QUANTITY" NUMBER, 
	"START_NO" NUMBER, 
	"END_NO" NUMBER, 
	"BANK_CODE" VARCHAR2(8 CHAR), 
	"REG_DATE" DATE, 
	"CHEQUE_TYPE" VARCHAR2(3 CHAR), 
	 CONSTRAINT "RCL_NN1" CHECK ("MISC_HIST_SEQ_NO" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN2" CHECK ("DENOMINATION" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN4" CHECK ("QUANTITY" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN5" CHECK ("START_NO" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN6" CHECK ("END_NO" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN7" CHECK ("REG_DATE" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_PK" PRIMARY KEY ("MISC_HIST_SEQ_NO", "DENOMINATION", "START_NO", "END_NO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "RB_INDX"  ENABLE
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING;

CREATE INDEX CASR_I1 ON CL_AUTO_SETTLE_REC -- Noncompliant
(LOAN_KEY)
NOLOGGING
NOPARALLEL;
-- compliant
CREATE TABLE FM_COMM_PARA
(
   UNIT_ID      VARCHAR2 (10 CHAR) NOT NULL,
   UNIT_DESC    VARCHAR2 (100 CHAR),
   PARA_VALUE   VARCHAR2 (500 CHAR) NOT NULL,
   PARA_DESC    VARCHAR2 (100 CHAR)
)
TABLESPACE FM_DATA;

CREATE TABLE "SYMBOLS"."RB_CHEQUE_DEAL" 
   (	"MISC_HIST_SEQ_NO" NUMBER(12,0), 
	"DENOMINATION" NUMBER, 
	"PREFIX" VARCHAR2(30 CHAR), 
	"QUANTITY" NUMBER, 
	"START_NO" NUMBER, 
	"END_NO" NUMBER, 
	"BANK_CODE" VARCHAR2(8 CHAR), 
	"REG_DATE" DATE, 
	"CHEQUE_TYPE" VARCHAR2(3 CHAR), 
	 CONSTRAINT "RCL_NN1" CHECK ("MISC_HIST_SEQ_NO" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN2" CHECK ("DENOMINATION" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN4" CHECK ("QUANTITY" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN5" CHECK ("START_NO" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN6" CHECK ("END_NO" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_NN7" CHECK ("REG_DATE" IS NOT NULL) ENABLE, 
	 CONSTRAINT "RCL_PK" PRIMARY KEY ("MISC_HIST_SEQ_NO", "DENOMINATION", "START_NO", "END_NO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "RB_INDX"  ENABLE
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  TABLESPACE FM_DATA;

CREATE INDEX UNIT ON FM_COMM_PARA(UNIT_ID) TABLESPACE FM_INDEX;

CREATE INDEX CASR_I1 ON CL_AUTO_SETTLE_REC
(LOAN_KEY)
NOLOGGING
TABLESPACE CL_INDX
NOPARALLEL;
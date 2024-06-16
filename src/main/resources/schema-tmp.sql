-- jxx_approval_line_master definition
CREATE TABLE `jxx_approval_line_master` (
    `APPROVAL_LINE_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재자 테이블 PK',
    `APPROVAL_DEPARTMENT_ID` varchar(16) NOT NULL COMMENT '결재자 부서 ID',
    `APPROVAL_DEPARTMENT_NAME` varchar(32) NOT NULL COMMENT '결재자 부서 명',
    `APPROVAL_ID` varchar(16) NOT NULL COMMENT '결재자 ID',
    `APPROVAL_NAME` varchar(32) NOT NULL COMMENT '결재자 이름',
    `APPROVAL_LINE_ORDER` int NOT NULL COMMENT '결재 순서',
    `APPROVAL_STATUS` char(12) NOT NULL COMMENT '결재 승인 여부', -- 'PENDING', 'ACCEPT', 'REJECT', 'FORWARD', 'SUBSEQUENT', 'DELEGATE'
    `APPROVAL_TIME` datetime(2) DEFAULT NULL COMMENT '결재 승인/반려 일시',
    `CONFIRM_DOCUMENT_ID` varchar(32) DEFAULT NULL COMMENT '결재 문서 테이블 ID',
    PRIMARY KEY (`APPROVAL_LINE_PK`),
    KEY `IDX_APPROVAL_ID` (`APPROVAL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

-- jxx_confirm_document_content_master definition
CREATE TABLE `jxx_confirm_document_content_master` (
   `CONFIRM_DOCUMENT_CONTENT_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재 문서 테이블 PK',
   `CONTENTS` json DEFAULT NULL,
   PRIMARY KEY (`CONFIRM_DOCUMENT_CONTENT_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

-- jxx_confirm_document_element definition

CREATE TABLE `jxx_confirm_document_element` (
    `CONFIRM_DOCUMENT_ELEMENT_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재 문서 요소 테이블 PK',
    `CONFIRM_DOCUMENT_ELEMENT_NAME` varchar(32) NOT NULL COMMENT '결재 문서 요소 이름',
    `CONFIRM_DOCUMENT_ELEMENT_GROUP` varchar(16) DEFAULT NULL COMMENT '결재 문서 요소 그룹 명',
    `CONFIRM_DOCUMENT_ELEMENT_KEY` varchar(64) NOT NULL COMMENT '결재 문서 요소 KEY',
    `CONFIRM_DOCUMENT_FORM_ID` varchar(8) DEFAULT NULL COMMENT '결재 문서 양식 FK(물리적 외래키 X)',
    `CONFIRM_DOCUMENT_FORM_COMPANY_ID` varchar(8) DEFAULT NULL COMMENT '결재 문서 양식 FK(물리적 외래키 X)',
    PRIMARY KEY (`CONFIRM_DOCUMENT_ELEMENT_PK`),
    KEY `LOGICAL_DFCID_DFID_FK` (`CONFIRM_DOCUMENT_FORM_COMPANY_ID`,`CONFIRM_DOCUMENT_FORM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

-- jxx_confirm_document_form definition
CREATE TABLE `jxx_confirm_document_form` (
     `CONFIRM_DOCUMENT_FORM_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재 문서 요소 테이블 PK',
     `CONFIRM_DOCUMENT_FORM_COMPANY_ID` varchar(8) NOT NULL COMMENT '결재 문서 양식 회사 ID',
     `CONFIRM_DOCUMENT_FORM_ID` varchar(8) NOT NULL COMMENT '결재 문서 양식 ID',
     `CONFIRM_DOCUMENT_FORM_NAME` varchar(32) DEFAULT NULL COMMENT '결재 문서 양식 이름',
     PRIMARY KEY (`CONFIRM_DOCUMENT_FORM_PK`),
     UNIQUE KEY `LOGICAL_DFCID_DFID_FK` (`CONFIRM_DOCUMENT_FORM_COMPANY_ID`,`CONFIRM_DOCUMENT_FORM_ID`),
     UNIQUE KEY `UK_a5rstsefn7c0cgjm5sf5poyfc` (`CONFIRM_DOCUMENT_FORM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

-- jxx_confirm_document_master definition
CREATE TABLE `jxx_confirm_document_master` (
   `CONFIRM_DOCUMENT_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재 문서 테이블 PK',
   `APPROVAL_LINE_LIFE_CYCLE` char(32) DEFAULT NULL COMMENT '결재선 상태', -- BEFORE_CREATE, CREATED, PROCESS_MODIFIABLE, PROCESS_UNMODIFIABLE
   `COMPLETED_TIME` datetime(6) DEFAULT NULL COMMENT '결재 문서가 최종 승인/반려된 시간',
   `CONFIRM_DOCUMENT_ID` varchar(32) DEFAULT NULL COMMENT '결재 문서 ID',
   `CONFIRM_STATUS` char(8) NOT NULL COMMENT '결재 상태', -- CREATE, UPDATE, RAISE, ACCEPT, REJECT, CANCEL
   `CREATE_SYSTEM` varchar(16) NOT NULL COMMENT '결재 데이터를 생성한 시스템',
   `CREATE_TIME` datetime(6) NOT NULL COMMENT '결재 문서 생성 시간',
   `DOCUMENT_TYPE` varchar(16) NOT NULL COMMENT '결재 양식 종류(ex 휴가, 구매 신청)',
   `COMPANY_ID` varchar(8) NOT NULL COMMENT '요청자 회사 ID',
   `DEPARTMENT_ID` varchar(16) NOT NULL COMMENT '요청자 부서 ID',
   `DEPARTMENT_NAME` varchar(32) NOT NULL COMMENT '요청자 부서 명',
   `REQUESTER_ID` varchar(16) NOT NULL COMMENT '기안자 ID',
   `REQUESTER_NAME` varchar(32) NOT NULL COMMENT '결재 요청자명',
   `CONFIRM_DOCUMENT_CONTENT_PK` bigint DEFAULT NULL COMMENT '결재 문서 본문',
   PRIMARY KEY (`CONFIRM_DOCUMENT_PK`),
   UNIQUE KEY `UK_9mdknh6iho7tfe9w9qmuj3vt5` (`CONFIRM_DOCUMENT_ID`),
   KEY `IDX_REQUESTER_NAME` (`REQUESTER_NAME`),
   KEY `IDX_CPN_ORG_RID` (`COMPANY_ID`,`DEPARTMENT_ID`,`REQUESTER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;





-- jxx_confirm_document_element_code definition
CREATE TABLE `jxx_confirm_document_element_code`
(
    `CONFIRM_DOCUMENT_ELEMENT_CODE_PK` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '결재 문서 요소 코드 테이블 PK',
    `ELEMENT_CODE_KEY`                 varchar(32) NOT NULL COMMENT '결재 문서 요소 KEY',
    `ELEMENT_CODE_NAME`                varchar(32) NOT NULL COMMENT '결재 문서 요소 이름',
    `ELEMENT_LOCATION`                 ENUM('HEADER', 'MAIN', 'FOOTER') COMMENT '요소의 위치',
    `ELEMENT_TYPE`                     ENUM('USER_NAME', 'USER_ID','DEPARTMENT_NAME', 'DEPARTMENT_ID', 'DATETIME', 'EXPENSE','')
        PRIMARY KEY (`CONFIRM_DOCUMENT_ELEMENT_CODE_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 이거 필요한거 맞는지 고민
INSERT INTO jxx_confirm_document_element_code
    (ELEMENT_CODE_KEY, ELEMENT_CODE_NAME, ELEMENT_LOCATION)
VALUES ('title', '결재 문서 제목', 'HEADER'),
       ('requester_name', '요청자 명', 'MAIN'),
       ('requester_id', '요청자 ID', 'MAIN'),
       ('writer_name', '작성자 명', 'MAIN'),
       ('writer_id', '작성자 ID', 'MAIN'),
       ('write_time', '작성일시', 'MAIN'),
       ('writer_department_name', '작성자 부서 명', 'MAIN'),
       ('start_date_time', '시작일자', 'MAIN'),
       ('end_date_time', '종료일자', 'MAIN'),
       ('delegator_id', '직무대행자 ID', 'MAIN'),
       ('delegator_name', '직무대행자 명', 'MAIN');

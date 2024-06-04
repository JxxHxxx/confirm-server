-- jxx_confirm.jxx_confirm_document_element_code definition

CREATE TABLE `jxx_confirm_document_element_code`
(
    `CONFIRM_DOCUMENT_ELEMENT_CODE_PK` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '결재 문서 요소 코드 테이블 PK',
    `ELEMENT_CODE_KEY`                 varchar(32) NOT NULL COMMENT '결재 문서 요소 KEY',
    `ELEMENT_CODE_NAME`                varchar(32) NOT NULL COMMENT '결재 문서 요소 이름',
    `ELEMENT_LOCATION`                 ENUM('HEADER', 'MAIN', 'FOOTER') COMMENT '요소의 위치',
    `ELEMENT_TYPE`                     ENUM('USER_NAME', 'USER_ID','DEPARTMENT_NAME', 'DEPARTMENT_ID'
                                            'DATETIME', 'EXPENSE','')
        PRIMARY KEY (`CONFIRM_DOCUMENT_ELEMENT_CODE_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 이거 필요한거 맞는지 고민
INSERT INTO jxx_confirm.jxx_confirm_document_element_code
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


INSERT INTO jxx_confirm_document_element
(CONFIRM_DOCUMENT_ELEMENT_NAME, CONFIRM_DOCUMENT_ELEMENT_KEY,
 CONFIRM_DOCUMENT_FORM_ID, CONFIRM_DOCUMENT_FORM_COMPANY_ID, CONFIRM_DOCUMENT_ELEMENT_GROUP)
VALUES('요청자','requester_name','vac','COM','요청 정보'),('요청부서','request_department','vac','COM','요청 정보'),
      ('시작일','start_date_time','vac','COM','휴가 기간'),('종료일','end_date_time','vac','COM','휴가 기간'),
      ('직무대행자','delegator_name','vac','COM','그 외');

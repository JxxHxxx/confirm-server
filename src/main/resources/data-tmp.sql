INSERT INTO jxx_confirm_document_form
(CONFIRM_DOCUMENT_FORM_COMPANY_ID, CONFIRM_DOCUMENT_FORM_ID, CONFIRM_DOCUMENT_FORM_NAME)
VALUES ('COM', 'vac', '휴가신청서'),
       ('COM', 'cost', '지출내역서');

INSERT INTO jxx_confirm_document_element
(CONFIRM_DOCUMENT_ELEMENT_NAME, CONFIRM_DOCUMENT_ELEMENT_KEY,
 CONFIRM_DOCUMENT_FORM_ID, CONFIRM_DOCUMENT_FORM_COMPANY_ID, CONFIRM_DOCUMENT_ELEMENT_GROUP)
VALUES ('요청자', 'requester_name', 'vac', 'COM', '요청 정보'),
       ('요청부서', 'department_name', 'vac', 'COM', '요청 정보'),
       ('사유', 'reason', 'vac', 'COM', '요청 정보'),
       ('시작일', 'vacation_durations.startDateTime', 'vac', 'COM', '휴가 기간'),
       ('종료일', 'vacation_durations.endDateTime', 'vac', 'COM', '휴가 기간'),
       ('직무대행자', 'delegator_name', 'vac', 'COM', '그 외'),
       ('품목 명', 'item_name', 'cost', 'COM', '지출 내역'),
       ('지출액', 'item_price', 'cost', 'COM', '지출 내역');

INSERT INTO jxx_confirm_document_content_master
    (CONTENTS)
VALUES ('{"title": "지출내역서", "item_name": "휴가신청서", "item_price": 15000}');

INSERT INTO jxx_confirm.jxx_confirm_document_form
(CONFIRM_DOCUMENT_FORM_COMPANY_ID, CONFIRM_DOCUMENT_FORM_ID, CONFIRM_DOCUMENT_FORM_NAME)
VALUES ('COM', 'item_request', '비품 신청서'),
       ('COM', 'auth_request', '엑세스 권한 신청서'),
       ('COM', 'employ_request', '채용 요청서'),
       ('COM', 'pay_request', '수당 요청서'),
       ('JXX', 'deploy_request', '배포 신청서'),
       ('JXX', 'dbddl_request', 'DB 스키마 변경 요청서'),
       ('JXX', 'firewall_request', '방화벽(포트 open/cose) 요청서'),
       ('BNG', 'material_order_request', '원재료 주문 요청서'),
       ('BNG', 'new_product_approve', '신제품 출시 승인서'),
       ('SPY', 'medical_eq_purchase', '의료 장비 구매 요청서'),
       ('SPY', 'access_patient_record', '환자 진료 기록 열람 신청서'),
       ('COM', 'WRK', '업무 요청서');

INSERT INTO jxx_confirm_document_element
(CONFIRM_DOCUMENT_ELEMENT_NAME, CONFIRM_DOCUMENT_ELEMENT_GROUP_NAME, CONFIRM_DOCUMENT_ELEMENT_KEY,
 CONFIRM_DOCUMENT_FORM_ID, CONFIRM_DOCUMENT_FORM_COMPANY_ID, CONFIRM_DOCUMENT_ELEMENT_GROUP_KEY,
 CONFIRM_DOCUMENT_ELEMENT_GROUP_ORDER, CONFIRM_DOCUMENT_ELEMENT_GROUP_TYPE, CONFIRM_DOCUMENT_ELEMENT_ORDER)
VALUES ('요청자', '요청 정보', 'requester_name', 'vac', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 1)
        , ('요청부서', '요청 정보', 'department_name', 'vac', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 2)
        , ('사유', '요청 정보', 'reason', 'vac', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 3)
        , ('시작일', '휴가 기간', 'vacation_durations.startDateTime', 'vac', 'COM', 'VACATION_DURATION', 2, 'PAIR', 1)
        , ('종료일', '휴가 기간', 'vacation_durations.endDateTime', 'vac', 'COM', 'VACATION_DURATION', 2, 'PAIR', 2)
        , ('직무대행자', '그 외', 'delegator_name', 'vac', 'COM', 'ETC', 3, 'PAIR', 1)
        , ( '카드 사용자', '요청 정보', 'card_user', 'cost', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 1)
        , ( '사용기간', '요청 정보', 'use_duration', 'cost', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 2)
        , ( '사용시간', '사용 내역', 'use_datetime', 'cost', 'COM', 'USAGE_HISTORY', 2, 'TABLE', 1)
        , ( '사용자', '사용 내역', 'user', 'cost', 'COM', 'USAGE_HISTORY', 2, 'TABLE', 2)
        , ( '구분', '사용 내역', 'use_type', 'cost', 'COM', 'USAGE_HISTORY', 2, 'TABLE', 3)
        , ( '사용내역(상세 기재)', '사용 내역', 'use_detail', 'cost', 'COM', 'USAGE_HISTORY', 2, 'TABLE', 4)
        , ( '사용금액', '사용 내역', 'usage_cost', 'cost', 'COM', 'USAGE_HISTORY', 2, 'TABLE', 5)
        , ( '비고', '사용 내역', 'note', 'cost', 'COM', 'USAGE_HISTORY', 2, 'TABLE', 6)
        ,('요청자', '요청 정보', 'requesterName', 'WRK', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 1)
        ,('요청부서', '요청 정보', 'requestDepartmentName', 'WRK', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 2)
        ,('요청 내용', '요청 정보', 'requestContent', 'WRK', 'COM', 'BAISC_REQUEST_INFO', 1, 'PAIR', 3)
        ,('분석 내용', '요청 분석', 'analyzeContent', 'WRK', 'COM', 'REQ_ANALYZE', 2, 'PAIR', 1)
        ,('계획 내용', '작업 계획', 'workPlanContent', 'WRK', 'COM', 'REQ_WORKPLAN', 3, 'PAIR', 1);

INSERT INTO JXX_CONFIRM_DOCUMENT_CONNECTION (DESCRIPTION, DOCUMENT_TYPE, HOST, METHOD_TYPE, TRIGGER_TYPE, URL) VALUES
    ('작업 티켓 최종 승인 후속 처리 API', 'WRK', 'http://localhost:8080','PATCH','ACCEPT','api/work-tickets/{work-ticket-pk}/complete-confirm');

INSERT INTO JXX_CONFIRM_DOCUMENT_CONNECTION_PARAMETER (PARAMETER_KEY, PARAMETER_TYPE, PATH_VARIABLE_ORDER, PARAMETER_VALUE, CONNECTION_PK) VALUES
    ('workStatus', 'REQUEST_BODY',1, 'ACCEPT', 1);
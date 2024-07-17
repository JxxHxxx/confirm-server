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
       ('품목 명', 'item_name', 'cost', 'COM','지출 내역'),
       ('지출액', 'item_price', 'cost', 'COM','지출 내역');

INSERT INTO jxx_confirm_document_content_master
(CONTENTS)
VALUES('{"title": "지출내역서", "item_name": "휴가신청서", "item_price": 15000}');

INSERT INTO jxx_confirm.jxx_confirm_document_form
(CONFIRM_DOCUMENT_FORM_COMPANY_ID, CONFIRM_DOCUMENT_FORM_ID, CONFIRM_DOCUMENT_FORM_NAME)
VALUES
    ('COM', 'item_request', '비품 신청서'),
    ('COM', 'auth_request', '엑세스 권한 신청서'),
    ('COM', 'employ_request', '채용 요청서'),
    ('COM', 'pay_request', '수당 요청서'),
    ('JXX', 'deploy_request', '배포 신청서'),
    ('JXX', 'dbddl_request', 'DB 스키마 변경 요청서'),
    ('JXX', 'firewall_request', '방화벽(포트 open/cose) 요청서'),
    ('BNG', 'material_order_request', '원재료 주문 요청서'),
    ('BNG', 'new_product_approve', '신제품 출시 승인서'),
    ('SPY', 'medical_eq_purchase', '의료 장비 구매 요청서'),
    ('SPY', 'access_patient_record', '환자 진료 기록 열람 신청서')
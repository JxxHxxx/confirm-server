package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.form.ConfirmDocumentElement;
import com.jxx.approval.confirm.domain.form.ConfirmDocumentForm;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentElementRequest;
import com.jxx.approval.confirm.dto.request.ElementPair;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormRequest;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormElementResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import com.jxx.approval.confirm.dto.response.ElementPairV2;
import com.jxx.approval.confirm.infra.ConfirmDocumentElementRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ConfirmDocumentFormService {

    private final ConfirmDocumentElementRepository elementRepository;
    private final ConfirmDocumentFormRepository formRepository;

    @Transactional
    public void createForm(ConfirmDocumentFormRequest request) {
        ConfirmDocumentForm confirmDocumentForm = new ConfirmDocumentForm(request.formId(), request.formName(), request.companyId());
        formRepository.save(confirmDocumentForm);
    }

    @Transactional
    public List<ConfirmDocumentElementServiceResponse> createElements(String confirmDocumentFormId, ConfirmDocumentElementRequest request) {
        ConfirmDocumentForm confirmDocumentForm = formRepository.findByFormIdAndCompanyId(confirmDocumentFormId, request.companyId()).orElseThrow();
        List<ElementPairV2> elementPairs = request.elementPairs();
        List<ConfirmDocumentElement> elements = elementPairs.stream()
                .map(pair -> {
                    ConfirmDocumentElement element = ConfirmDocumentElement.builder()
                            .elementGroupOrder(request.elementGroupOrder())
                            .elementGroupType(request.elementGroupType())
                            .elementGroupName(request.elementGroupName())
                            .elementGroupKey(request.elementGroupKey())
                            .elementOrder(request.elementGroupOrder())
                            .elementKey(pair.elementKey())
                            .elementName(pair.elementName())
                            .elementOrder(pair.elementOrder())
                            .build();
                    element.mappingDocumentForm(confirmDocumentForm);
                    return element;
                }).toList();

        List<ConfirmDocumentElement> savedElements = elementRepository.saveAll(elements);

        return savedElements.stream()
                .map(element -> new ConfirmDocumentElementServiceResponse(element.getPk(), element.getElementKey(), element.getElementName()))
                .toList();
    }

    public List<ConfirmDocumentFormResponse> findConfirmDocumentForms(String companyId) {
        List<ConfirmDocumentForm> confirmDocumentForms = formRepository.findByCompanyId(companyId);
        return confirmDocumentForms.stream()
                .map(form -> new ConfirmDocumentFormResponse(form.getPk(), form.getCompanyId(), form.getFormId(), form.getFormName()))
                .toList();
    }

    public List<ConfirmDocumentFormResponse> findConfirmDocumentFormsV2() {
        List<ConfirmDocumentForm> confirmDocumentForms = formRepository.findAll();
        return confirmDocumentForms.stream()
                .map(form -> new ConfirmDocumentFormResponse(form.getPk(), form.getCompanyId(), form.getFormId(), form.getFormName()))
                .toList();
    }

    public List<ElementPair> findConfirmDocumentFormElement(String companyId, String confirmDocumentFormId) {
        List<ConfirmDocumentElement> confirmDocumentFormElements = elementRepository.findByConfirmDocumentForm(companyId, confirmDocumentFormId);
        return confirmDocumentFormElements.stream()
                .map(element -> new ElementPair(element.getElementGroupName(), element.getElementKey(), element.getElementName()))
                .toList();
    }

    public List<ConfirmDocumentFormElementResponse> findConfirmDocumentFormElementV2(String companyId, String confirmDocumentFormId) {
        List<ConfirmDocumentElement> formElements = elementRepository.findByConfirmDocumentForm(companyId, confirmDocumentFormId);

        // 그룹 KEY 를 기준으로 Map
        Map<String, List<ConfirmDocumentElement>> groupByGroupKeyElements = formElements.stream()
                .collect(Collectors.groupingBy(ConfirmDocumentElement::getElementGroupKey));

        return groupByGroupKeyElements.entrySet()
                .stream()
                .map(confirmDocumentElements -> {
                    // ConfirmDocumentFormElementResponse group 데이터를 넣기 위한 작업
                    ConfirmDocumentElement tmpElement = confirmDocumentElements.getValue().get(0);
                    ConfirmDocumentFormElementResponse confirmDocumentFormElementResponse = new ConfirmDocumentFormElementResponse(
                            tmpElement.getElementGroupKey(),
                            tmpElement.getElementGroupName(),
                            tmpElement.getElementGroupType(),
                            tmpElement.getElementGroupOrder()
                    );
                    // elements 요소 추가
                    List<ElementPairV2> elements = confirmDocumentElements.getValue().stream()
                            .map(element -> new ElementPairV2(
                                    element.getElementKey(),
                                    element.getElementName(),
                                    element.getElementOrder()
                            )).toList();

                    confirmDocumentFormElementResponse.setElements(elements);
                    return confirmDocumentFormElementResponse;
                }).sorted(Comparator.comparingInt(ConfirmDocumentFormElementResponse::getElementGroupOrder))
                .toList();
    }


}

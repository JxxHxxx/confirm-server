package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.form.ConfirmDocumentElement;
import com.jxx.approval.confirm.domain.form.ConfirmDocumentForm;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentElementRequest;
import com.jxx.approval.confirm.dto.request.ElementPair;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormRequest;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import com.jxx.approval.confirm.infra.ConfirmDocumentElementRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        List<ElementPair> elementPairs = request.elementPairs();
        List<ConfirmDocumentElement> elements = elementPairs.stream()
                .map(pair -> {
                    ConfirmDocumentElement confirmDocumentElement = new ConfirmDocumentElement(pair.elementKey(), pair.elementName());
                    confirmDocumentElement.mappingDocumentForm(confirmDocumentForm);
                    return confirmDocumentElement;
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

    public List<ElementPair> findConfirmDocumentFormElement(String companyId, String confirmDocumentFormId) {
        List<ConfirmDocumentElement> confirmDocumentFormElements = elementRepository.findByConfirmDocumentForm(companyId, confirmDocumentFormId);
        return confirmDocumentFormElements.stream()
                .map(element -> new ElementPair(element.getElementKey(), element.getElementName()))
                .toList();
    }
}

package com.jxx.approval.application;

import com.jxx.approval.domain.ConfirmDocumentElement;
import com.jxx.approval.domain.ConfirmDocumentForm;
import com.jxx.approval.domain.ConfirmDocumentFormElement;
import com.jxx.approval.dto.request.FormElementCreateRequest;
import com.jxx.approval.dto.request.ConfirmDocumentElementForm;
import com.jxx.approval.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.dto.response.ContentServiceResponse;
import com.jxx.approval.infra.ConfirmDocumentElementRepository;
import com.jxx.approval.infra.ConfirmDocumentFormElementRepository;
import com.jxx.approval.infra.ConfirmDocumentFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmDocumentFormElementService {

    private final ConfirmDocumentElementRepository elementRepository;
    private final ConfirmDocumentFormRepository formRepository;
    private final ConfirmDocumentFormElementRepository formElementRepository;

    @Transactional
    public ConfirmDocumentElementServiceResponse createElement(ConfirmDocumentElementForm form) {
        ConfirmDocumentElement element = elementRepository.save(
                new ConfirmDocumentElement(form.elementKey(), form.elementName()));

        return new ConfirmDocumentElementServiceResponse(element.getPk(), element.getKey(), element.getName());
    }


    @Transactional
    public List<ConfirmDocumentElementServiceResponse> createElements(List<ConfirmDocumentElementForm> forms) {
        List<ConfirmDocumentElement> elements = forms.stream()
                .map(form -> new ConfirmDocumentElement(form.elementKey(), form.elementName()))
                .toList();
        List<ConfirmDocumentElement> savedElements = elementRepository.saveAll(elements);

        return savedElements.stream()
                .map(element -> new ConfirmDocumentElementServiceResponse(element.getPk(), element.getKey(), element.getName()))
                .toList();
    }

    public List<ConfirmDocumentElementServiceResponse> findElements() {
        List<ConfirmDocumentElement> elements = elementRepository.findAll();
        return elements.stream()
                .map(element -> new ConfirmDocumentElementServiceResponse(element.getPk(), element.getKey(), element.getName()))
                .toList();
    }

    @Transactional
    public void createFormElement(FormElementCreateRequest request) {
        ConfirmDocumentForm confirmDocumentForm = new ConfirmDocumentForm(request.formId(), request.formName(), request.companyId());

        ConfirmDocumentForm savedForm = formRepository.save(confirmDocumentForm);
        List<ConfirmDocumentElement> elements = elementRepository.findByPkIn(request.elementPks());

        List<ConfirmDocumentFormElement> formElements = elements.stream()
                .map(element -> ConfirmDocumentFormElement.builder()
                        .confirmDocumentForm(savedForm)
                        .confirmDocumentElement(element)
                        .build()).toList();

        formElementRepository.saveAll(formElements);
    }

    public List<ContentServiceResponse> readFormElement(Long documentFormPk) {
        List<ConfirmDocumentFormElement> formElements = formElementRepository.findByConfirmDocumentFormPk(documentFormPk);

        return formElements.stream()
                .map(formElement -> new ContentServiceResponse(formElement.getFormElementKey(), formElement.getFormElementName()))
                .toList();
    }
}

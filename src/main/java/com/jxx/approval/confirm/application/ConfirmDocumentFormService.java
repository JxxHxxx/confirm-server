package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.form.ConfirmDocumentElement;
import com.jxx.approval.confirm.domain.form.ConfirmDocumentForm;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentElementForm;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormRequest;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.confirm.infra.ConfirmDocumentElementRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<ConfirmDocumentElementServiceResponse> createElements(String confirmDocumentFormId, List<ConfirmDocumentElementForm> forms) {
        ConfirmDocumentForm confirmDocumentForm = formRepository.findByFormId(confirmDocumentFormId).orElseThrow();
        List<ConfirmDocumentElement> elements = forms.stream()
                .map(form -> {
                    ConfirmDocumentElement confirmDocumentElement = new ConfirmDocumentElement(form.elementKey(), form.elementName());
                    confirmDocumentElement.mappingDocumentForm(confirmDocumentForm);
                    return confirmDocumentElement;
                }).toList();

        List<ConfirmDocumentElement> savedElements = elementRepository.saveAll(elements);

        return savedElements.stream()
                .map(element -> new ConfirmDocumentElementServiceResponse(element.getPk(), element.getElementKey(), element.getElementName()))
                .toList();
    }
}

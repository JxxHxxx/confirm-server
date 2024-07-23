package com.jxx.approval.common.page;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/** spring bean 으로 등록해서 사용 X 필드 동시 접근 가능성 존재 **/
@RequiredArgsConstructor
public class PageService {

    private final int page;
    private final int size;

    public <T> Page<T> convertToPage(List<T> responses) {
        int total = responses.size();
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), total);

        return new PageImpl<>(responses.subList(start,end), pageRequest, total);
    }
}

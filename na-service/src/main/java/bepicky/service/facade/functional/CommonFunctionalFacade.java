package bepicky.service.facade.functional;

import org.springframework.data.domain.PageRequest;

public interface CommonFunctionalFacade {

    default PageRequest pageReq(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}

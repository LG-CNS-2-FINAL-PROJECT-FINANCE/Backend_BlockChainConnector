package com.ddiring.Backend_BlockchainConnector.common;

import com.ddiring.Backend_BlockchainConnector.utils.RequestHeaderUtils;
import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String userSeq = RequestHeaderUtils.getUserSeq();
            return Optional.of(userSeq);
        } catch (Exception e) {
            return Optional.of("system");
        }
    }
}
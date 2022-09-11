package dev.practice.order.domain.partner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerStore partnerStore;
    private final PartnerReader partnerReader;

    @Override
    public PartnerInfo registerPartner(PartnerCommand command) {
        Partner initPartner = command.toEntity();
        Partner partner = partnerStore.store(initPartner);

        return new PartnerInfo(partner);
    }

    @Override
    public PartnerInfo getPartnerInfo(String partnerToken) {
        Partner partner = partnerReader.getPartner(partnerToken);
        return new PartnerInfo(partner);
    }

    @Override
    public PartnerInfo enablePartner(String partnerToken) {
        Partner partner = partnerReader.getPartner(partnerToken);
        partner.enabled();
        return new PartnerInfo(partner);
    }

    @Override
    public PartnerInfo disablePartner(String partnerToken) {
        Partner partner = partnerReader.getPartner(partnerToken);
        partner.disabled();
        return new PartnerInfo(partner);
    }
}

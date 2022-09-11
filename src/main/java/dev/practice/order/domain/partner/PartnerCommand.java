package dev.practice.order.domain.partner;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class PartnerCommand {
    private final String partnerToken;
    private final String partnerName;
    private final String businessNo;
    private final String email;

    public Partner toEntity() {
       return Partner.builder()
               .partnerName(this.getPartnerName())
               .businessNo(this.getBusinessNo())
               .email(this.getEmail())
               .build();
    }
}

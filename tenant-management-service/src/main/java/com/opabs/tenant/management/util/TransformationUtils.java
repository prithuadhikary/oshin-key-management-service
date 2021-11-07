package com.opabs.tenant.management.util;

import com.opabs.tenant.management.controller.command.ContactInfoDTO;
import com.opabs.tenant.management.domain.Address;
import com.opabs.tenant.management.domain.ContactInfo;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.util.CollectionUtils.isEmpty;

public class TransformationUtils {

    public static ContactInfo fromContactInfoDTO(ContactInfoDTO contactInfoDTO) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmailAddress(contactInfoDTO.getEmailAddress());
        contactInfo.setPhone(contactInfoDTO.getPhone());
        if (!isEmpty(contactInfoDTO.getAddresses())) {
            List<Address> addresses = new ArrayList<>();
            contactInfoDTO.getAddresses().forEach(addressDTO -> {
                Address address = new Address();
                copyProperties(addressDTO, address);
                address.setContactInfo(contactInfo);
                addresses.add(address);
            });
            contactInfo.setAddresses(addresses);
        }
        return contactInfo;
    }

}

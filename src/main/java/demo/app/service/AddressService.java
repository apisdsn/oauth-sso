//package demo.app.service;
//
//
//import demo.app.entity.Address;
//import demo.app.model.AddressRequest;
//import demo.app.model.AddressResponse;
//import demo.app.repository.AddressRepository;
//import demo.app.validator.ValidationService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Service
//@Slf4j
//public class AddressService {
//    private final AddressRepository addressRepository ;
//    private final ValidationService validationService;
//
//    @Autowired
//    public AddressService(AddressRepository addressRepository, ValidationService validationService) {
//        this.addressRepository = addressRepository;
//        this.validationService = validationService;
//    }
//
//    public AddressResponse toAddressResponse(Address address) {
//        return AddressResponse.builder()
//                .id(address.getAddressId())
//                .street(address.getStreet())
//                .city(address.getCity())
//                .province(address.getProvince())
//                .country(address.getCountry())
//                .postalCode(address.getPostalCode())
//                .build();
//    }
//
//}

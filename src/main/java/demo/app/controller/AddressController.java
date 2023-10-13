//package demo.app.controller;
//
//
//import demo.app.entity.Employee;
//import demo.app.model.AddressRequest;
//import demo.app.model.AddressResponse;
//import demo.app.model.EmployeeRequest;
//import demo.app.model.WebResponse;
//import demo.app.service.AddressService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/addresses")
//public class AddressController {
//
//    private final AddressService addressService;
//
//    @Autowired
//    public AddressController(AddressService addressService) {
//        this.addressService = addressService;
//    }
//
//    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public WebResponse<String> create(@RequestBody AddressRequest request) {
//        addressService.create(request);
//        return WebResponse.<String>builder().data("OK").build();
//    }
//}
//

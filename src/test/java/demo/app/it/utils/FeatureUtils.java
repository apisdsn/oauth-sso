//package demo.app.it.utils;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.entity.Employee;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//
//public class FeatureUtils {
//    @LocalServerPort
//    private static int port;
//    public static String URL = "http://localhost:" + port;
//
//    public static Employee getMockAccount() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Resource resource = new ClassPathResource("");
//        try {
//            return objectMapper.readValue(resource.getInputStream(), Employee.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
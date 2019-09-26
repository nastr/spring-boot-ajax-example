package com.nastrsoft.controller;

import com.nastrsoft.model.Type;
import com.nastrsoft.services.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class MainController {

    private ResourceManager service;

    @Autowired
    public void setUserService(ResourceManager service) {
        this.service = service;
    }

    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> getItemsList(@RequestParam("type") Type type) throws InterruptedException {
        return service.getResponse(type);
    }

    @PostMapping(value = "list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateLatest(@RequestParam("db") String db) throws InterruptedException, IOException {
        service.setCustomLatestDB(db);

        return service.getResponse(Type.PATCH);
    }

//    @PostMapping("/api/search")
//    public ResponseEntity<AjaxResponseBody> getSearchResultViaAjax(@Valid @RequestBody SearchCriteria search, Errors errors) throws InterruptedException {
//        AjaxResponseBody result = new AjaxResponseBody();
//
//        //If error, just return a 400 bad request, along with the error message
//        if (errors.hasErrors()) {
//            result.setMsg(StreamEx.of(errors.getAllErrors()).map(DefaultMessageSourceResolvable::getDefaultMessage).joining(","));
//            return ResponseEntity.badRequest().body(result);
//        }
//
//        List<User> users = service.findByUserNameOrEmail(search.getUsername());
//        if (users.isEmpty()) {
//            result.setMsg("no user found!");
//        } else {
//            result.setMsg("success");
//            result.setResult(users);
//        }
////        TimeUnit.SECONDS.sleep(5);
//
//        return ResponseEntity.ok(result);
//
//    }
}

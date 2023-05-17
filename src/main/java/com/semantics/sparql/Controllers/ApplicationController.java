package com.semantics.sparql.Controllers;


import com.semantics.sparql.Models.Event;
import com.semantics.sparql.SparqlClient.Events.SearchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("sparqltool/")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {
    
    @PostMapping(
            value = "/query",
            consumes = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8",
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<String> post(
            @RequestBody Event request) throws IOException {
        log.info("received intent: {}", request);
        SearchEvent searchEvent = new SearchEvent(request);
        request.validateSparql();
        searchEvent.query();
        return ResponseEntity.ok("Successfully sent");
    }
}

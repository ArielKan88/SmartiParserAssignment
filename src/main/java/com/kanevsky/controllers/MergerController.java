package com.kanevsky.controllers;

import com.kanevsky.services.MergerService;
import com.kanevsky.views.ErrorView;
import com.kanevsky.views.MergerInputView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MergerController {
    @Autowired
    private MergerService mergerService;

    @Autowired
    private MergerInputValidator inputValidator;

    @PostMapping("/merge")
    public ResponseEntity merge(@RequestBody MergerInputView mergerInputView) {
        try {
            final BeanPropertyBindingResult validationResult = new BeanPropertyBindingResult(mergerInputView, "inputs");
            inputValidator.validate(mergerInputView, validationResult);
            if (validationResult.hasErrors()) {
                final List<String> prettyErrorMessages = validationResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
                return ResponseEntity.internalServerError().body(new ErrorView(prettyErrorMessages));
            }
            var result = mergerService.merge(mergerInputView.getInputs());
            return ResponseEntity.ok(result);
        } catch (Throwable t) {
            return ResponseEntity.internalServerError().body(new ErrorView(t.getMessage()));
        }
    }
}

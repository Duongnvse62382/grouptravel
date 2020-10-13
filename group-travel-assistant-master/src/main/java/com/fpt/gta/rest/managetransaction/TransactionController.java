package com.fpt.gta.rest.managetransaction;

import com.fpt.gta.model.entity.Document;
import com.fpt.gta.model.entity.Transaction;
import com.fpt.gta.model.service.DocumentService;
import com.fpt.gta.model.service.TransactionService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    ModelMapper modelMapper;
    TransactionService transactionService;
    DocumentService documentService;


    @Autowired
    public TransactionController(ModelMapper modelMapper, TransactionService transactionService, DocumentService documentService) {
        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
        this.documentService = documentService;
    }

    @GetMapping
    public List<TransactionDTO> findAllTransactionInGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                                       @RequestParam Integer idGroup
    ) {
        return Arrays.asList(modelMapper.map(transactionService.findAllTransactionInGroup(firebaseToken.getUid(), idGroup),
                TransactionDTO[].class));
//        return transactionService.findAllTransactionInGroup(firebaseToken.getUid(), idGroup);
    }

    @GetMapping("/{idTransaction}")
    public TransactionDTO findTransactionById(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                              @PathVariable Integer idTransaction
    ) {
        return modelMapper.map(transactionService.findTransactionById(firebaseToken.getUid(), idTransaction),
                TransactionDTO.class);
    }

    @PostMapping
    public void createTransaction(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                  @RequestParam Integer idGroup,
                                  @RequestBody TransactionDTO transactionDTO
    ) {
        Transaction transaction = transactionService.createTransaction(firebaseToken.getUid(),
                idGroup,
                modelMapper.map(transactionDTO, Transaction.class),
                transactionDTO.getDocumentList() == null ? null : Arrays.asList(modelMapper.map(transactionDTO.getDocumentList(), Document[].class))
        );
    }

    @PutMapping
    public void updateTransaction(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                  @RequestBody TransactionDTO transactionDTO
    ) {
        Transaction transaction = transactionService.updateTransaction(firebaseToken.getUid(),
                modelMapper.map(transactionDTO, Transaction.class),
                transactionDTO.getDocumentList() == null ? null : Arrays.asList(modelMapper.map(transactionDTO.getDocumentList(), Document[].class))
        );
    }

    @DeleteMapping("/{idTransaction}")
    public void deleteTransaction(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                  @PathVariable String idTransaction) {
        transactionService.deleteTransaction(firebaseToken.getUid(), Integer.parseInt(idTransaction));
    }
}

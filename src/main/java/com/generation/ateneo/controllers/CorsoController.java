package com.generation.ateneo.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.generation.ateneo.DTO.CorsoRecordDTO;
import com.generation.ateneo.entities.Corso;
import com.generation.ateneo.services.CorsoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/corsi")
@Controller 
public class CorsoController {

    private final CorsoService corsoService;
    //oggetto che passa i dati del docente

    //metodo che gestisce la richiesta di poter vedere la pagina con la lista dei corsi
    //il metodo restituirà la view/ il template html
    @GetMapping //non mettendo l'endpoint sto dicendo implicitamente che uso quello della root
    public String list(Model model){
        //salvo nel model una chiave con valore corso che ha lo stesso nome del placeholder nell'html
        //associo a quella chiave la lista dei corsi da renderizzare tramite thymeleaf
        model.addAttribute("corsi",corsoService.getAll());
        return "corsi/list";//se c'è la sottocartella va indicata prima del nome del template
    }


    //metodo per vedere i dettagli di un corso
    @GetMapping("/{id}")
    public String details(
        @PathVariable Long id,
        @RequestParam(name="showIscritti", required=false, defaultValue="false") boolean showIscritti,
        Model model,
        RedirectAttributes ra
    ){
        //prendo il corso dal servizio
        //se showIscritti è true prendo il corso con le iscrizioni altrimenti 
        //prendo il corso normale senza iscrizioni
        Corso corso = showIscritti ? corsoService.getByIdWithIscrizioni(id) : 
                                     corsoService.getByIdOrNull(id);         
           
        if(corso == null){
            return "redirect:/corsi";//se non trovo il corso torno alla lista corsi
        }
        model.addAttribute("corso", corso);
        model.addAttribute("showIscritti", showIscritti);//per far vedere la tabella iscritti
        return "corsi/details";
    }


    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("corsoDto",CorsoRecordDTO.empty());
        model.addAttribute("mode","create");
        //TODO aggiungere i docenti nel menu
        return "corsi/form-dto";
    }

    //Spring MVC 
    //richiesta -> ritorna template
    //richiesta -> controllo nel server -> ricarica la pagina -> messaggio di errore ->
    //Rest -> richiesta -> controllo nel server lo fa fare a javascript -> Json 
    @PostMapping // risponde a /corsi come metodo POST
    public String create(
        @Valid @ModelAttribute("corsoDto") CorsoRecordDTO dto, //prendi i dati della form e li salva in dto
        BindingResult br, //cattura gli errori
        Model model,
        RedirectAttributes ra
    ){
        //2 vie o controllo ad ogni step gli errori e ricarico la form 
        //segnala gli errori generati dalla validazione sopra i campi del dto
        // if(br.hasErrors()){
        //     //mettere nel model la lista dei docenti
        //     model.addAttribute("mode","create");
        //     return "corsi/form-dto";
        // }

        //controlli codice corsi
        //che la data di fine non sia antecedente a quella di inizio
        if(dto.dataInizio() != null && dto.dataFine() != null 
            && dto.dataFine().isBefore(dto.dataInizio())){
                br.rejectValue("dataFine", "dataOrder","Data fine deve essere successiva alla data di inizio");
            }

        //segnala gli errori sulle date
        // if(br.hasErrors()){
        //     //mettere nel model la lista dei docenti
        //     model.addAttribute("mode","create");
        //     return "corsi/form-dto";
        // }

        //controllo sull'unicità
        Map<String,String> uniq = corsoService.uniqueErrorsForCreate(dto);
        uniq.forEach((field,msg) -> br.rejectValue(field,"duplicazione",msg));

        if(br.hasErrors()){
            //mettere nel model la lista dei docenti
            model.addAttribute("mode","create");
            return "corsi/form-dto";
        }

        boolean ok = corsoService.createFromDto(dto);
        ra.addFlashAttribute(ok ? "success" : "error", ok? "Corso creato" : "Creazione del corso fallita");
        return "redirect:/corsi";
    }

    //metodo per vedere la form di modifica
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,Model model,RedirectAttributes ra){
       CorsoRecordDTO dto = corsoService.getDTOById(id);
       if(dto == null){
            ra.addFlashAttribute("error","Corso non trovato");
        return "redirect:/corsi";
       }

       model.addAttribute("mode","edit");
       model.addAttribute("corsoDto", dto);
       //TODO aggiungere i docenti nel menu
       return "corsi/form-dto";
    }

    //metodo che riceve i dati della form e li salva nel db
    @PostMapping("/{id}")
    public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("corsoDto") CorsoRecordDTO dto, //prendi i dati della form e li salva in dto
        BindingResult br, //cattura gli errori
        Model model,
        RedirectAttributes ra
    ){
        CorsoRecordDTO dtoPerTemplate = (dto.id() == null) ? dto.withId(id) : dto;

        //controlli codice corsi
        //che la data di fine non sia antecedente a quella di inizio
        if(dto.dataInizio() != null && dto.dataFine() != null 
            && dto.dataFine().isBefore(dto.dataInizio())){
                br.rejectValue("dataFine", "dataOrder","Data fine deve essere successiva alla data di inizio");
            }

        //segnala gli errori sulle date
        // if(br.hasErrors()){
        //     //mettere nel model la lista dei docenti
        //     model.addAttribute("mode","create");
        //     return "corsi/form-dto";
        // }

        //controllo sull'unicità
        Map<String,String> uniq = corsoService.uniqueErrorsForUpdate(id,dtoPerTemplate);
        uniq.forEach((field,msg) -> br.rejectValue(field,"duplicazione",msg));

        if(br.hasErrors()){
            //mettere nel model la lista dei docenti
            model.addAttribute("mode","edit");
            model.addAttribute("corsoDto", dtoPerTemplate);
            return "corsi/form-dto";
        }

        boolean ok = corsoService.updateFromDto(id,dtoPerTemplate);
        ra.addFlashAttribute(ok ? "success" : "error", ok? "Corso creato" : "Creazione del corso fallita");
        return "redirect:/corsi";
    }
}

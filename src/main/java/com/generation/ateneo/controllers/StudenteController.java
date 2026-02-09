package com.generation.ateneo.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
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

import com.generation.ateneo.DTO.StudenteFormDTO;
import com.generation.ateneo.entities.Corso;
import com.generation.ateneo.entities.Persona;
import com.generation.ateneo.entities.StatusStudente;
import com.generation.ateneo.entities.Studente;
import com.generation.ateneo.entities.UserAccount;
import com.generation.ateneo.services.CorsoService;
import com.generation.ateneo.services.IscrizioneService;
import com.generation.ateneo.services.StudenteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
// quando annoto una classe con questa annotazione indico che è una classe che
// gestisce
// le richieste HTTP e restituisce le view(template)
// se usate in combinazione con Thymeleaf i controller(i metodi) restituiranno
// pagine/template HTML
@RequestMapping("/studenti")
// in questo modo mappo tutte le richieste che iniziano con /studenti a questo
// controller
// serve a centralizzare un percorso/path di base
// poi i singoli metodi dovranno usare e dichiarare solo il loro sottopercorso
@RequiredArgsConstructor
public class StudenteController {

    // proprietà
    private final StudenteService studenteService;
    private final IscrizioneService iscrizioneService;
    private final CorsoService corsoService;
    // public StudenteController(StudenteService studenteService){
    // this.studenteService = studenteService;
    // }

    // metodo che ci permette di visualizzare la lista degli studenti-> LETTURA DATI
    // del db
    // quindi rispodere ad una richiesta HTTP
    @GetMapping // ("/studenti")
    public String list(Model model, Authentication authentication) {
        if (authentication != null) {
            UserAccount user = (UserAccount) authentication.getPrincipal();
            Persona p = user.getPersona();
            model.addAttribute("idPersona", p.getId());
            log.info("modifca");
        }
        model.addAttribute("studenti", studenteService.getAll());
        return "studenti/list";
    }

    @GetMapping("/pagina-studente")
    public String paginaStudente(Model model, Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        Persona p = user.getPersona();
        if (p != null && p instanceof Studente s) {
            model.addAttribute("studente", s);
            return "redirect:/studenti/" + p.getId();
        }

        return "redirect:/studenti/";
    }

    // dettaglio dello studente con i corsi a cui è iscritto e quelli ancora
    // disponibili
    @GetMapping("/{id}") // ad esempio se volessi vedere lo studente con id 2: /studenti/2
    public String detail(@PathVariable Long id,
            @RequestParam(name = "iscrizioneId", required = false) Long iscrizioneId,
            Model model,
            RedirectAttributes ra,
            Authentication authentication) {

        UserAccount user = (UserAccount) authentication.getPrincipal();
        Persona p = user.getPersona();

        if (authentication != null && p instanceof Studente s) {
            if (s.getId() != id) {
                return "redirect:/studenti";
            }
        }
        // StudenteFormDTO dto = studenteService.getDtoById(id);
        // cerca lo studente con il suo id e le sue iscrizioni
        Studente s = studenteService.getByIdWithIscrizioni(id);
        if (s == null) {
            ra.addFlashAttribute("error", "Studente non trovato");
            return "redirect:/studenti";
        }

        // tutti i corsi disponibili
        List<Corso> tuttiICorsi = corsoService.getAll();

        // ora salvo in un set tutti gli id dei corsi a cui lo studente è già iscritto
        // così da poterli escludere dalla lista dei corsi disponibili
        Set<Long> corsoIdsGiaIscritti = s.getIscrizioni().stream() // stream delle iscrizioni dello studente
                .map(i -> i.getCorso().getId()) // prendo per ogni iscrizione l'id del corso associato
                .collect(Collectors.toSet());// metto tutti gli id in un set

        // filtro la lista di tutti i corsi escludendo quelli a cui lo studente è già
        // iscritto
        List<Corso> corsiDisponibili = tuttiICorsi.stream() // stream di tutti i corsi
                .filter(c -> c.getId() != null && !corsoIdsGiaIscritti.contains(c.getId())) // filtro escludendo quelli
                                                                                            // già iscritti
                .collect(Collectors.toList()); // metto il risultato in una lista

        log.info("Corsi disponibili per lo studente {}: {}", s.getId(), corsiDisponibili);

        model.addAttribute("studente", s);
        model.addAttribute("corsiDisponibili", corsiDisponibili);
        model.addAttribute("highlightIscrizioneId", iscrizioneId);
        return "studenti/details";
    }

    // permette di visualizzare la form per creare un nuovo studente
    // risponde alla richiesta GET /studenti/new
    @GetMapping("/new")
    public String createForm(Model model) {
        StudenteFormDTO dto = new StudenteFormDTO();
        dto.setStatus(StatusStudente.ATTIVO);

        model.addAttribute("studenteDto", dto);
        model.addAttribute("statusValues", StatusStudente.values());
        model.addAttribute("mode", "create");
        return "studenti/form-dto";
    }

    /*
     * Spring prende i dati dalla form e cerca di riempire un oggetto
     * di tipo StudenteformDto cioè fa un Data binding usando
     * l'annotazione @ModelAttribute(oggetto)
     * cioè prende i valori della form e prova a metterlo nel DTO
     * spring prima di inviare vermanete i dati al db controlla i campi
     * usando leannotazioni inserite sopra i fields di studenteFormDTO
     * va leggere le annotazione controlla se i valori sono validi o meno,se
     * rispettano le regole
     * se incontra errori li salva in un oggetto di tipo BindingResult,che questo
     * metodo prende come parametro
     * se ci sono errori allora rimandiamo la pagina di form all'utente e gli
     * stampiamo i messaggi di errore
     * così che possa modificare i dati e farli rientrare nelle regole di
     * validazione
     * Passaggi:
     * 1.compilazione form
     * 2. utente preme invia
     * 3. il browser invia la richiesta Post al controller
     * 4. il server controlla i campi :
     * 5. usando @Valid -> (@NotBlank....
     * ma anche i controlli che abbiamo nel metodo del controller -> ad esempio
     * email univoca
     * 6. se ci sono errori il server non manda l'istrruzione al db, cioè non fa il
     * save ma
     * 7. rimanda alla stessa pagina della form
     * 8. thyleaf inserisce nell'input corrispondente all'errore il messagio di
     * errore da noi settato
     * 9. l'utente può correggere il valore e riprovare a fare save
     * 10. -> quindi il messaggio di errore non appare mentre si scrivo i valori ma
     * dopo il submit
     * 
     * pagina web -> request http -> form dati come dto -> controller ->
     * trasformazione in enità -> service -> db
     * -> redirect/ricaricata la pagina
     * Spring MVC: view -> controller -> model(dati) -> view
     */
    @PostMapping // ("/studenti")
    public String create(@Valid @ModelAttribute("studenteDto") StudenteFormDTO dto,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {// serve quando si fa un redirect da un controller a un
        // altro endpoint e si vuole passare dei dati insieme al redirect.
        // Questi dati passati hanno queste caratteristiche:
        // NON finiscono nell’URL e sono salvati temporaneamente in sessione
        // quindi: POST /salva -> redirect:/lista (qui passo quei dati salvati in ra) ->
        // GET/lista

        /*
         * Thymeleaf legge il BindingResult cioè -> quando Thymeleaf renderizza la
         * pagina,
         * Spring mette nel model anche un oggetto chiamato BindingResult.studenteDto
         * (.studenteDto perché l’oggetto in model si chiama studenteDto)
         * in esso vengono salvati gli errori di Bean Validation (@NotBlank, @Email,
         * ecc.)
         * Flusso per capire al gestione dei controlli e degli errori:
         * //1. Submit form -> Spring crea DTO (@ModelAttribute)
         * //2. @Valid controlla regole base -> errori in BindingResult
         * //3. Service controlla DB (existsBy...) → aggiunge errori con il metodo
         * rejectValue
         * //4. Se ci sono errori -> ritorni la stessa view (non fa il redirect)
         * //5. Thymeleaf legge i dati del BindingResult -> mostra i messaggi impostati
         * sotto i campi
         * //6. Se è tutto ok allora fa il save
         */

        // errori se le regole divalidazioni non vengono seguite(ad esempio nome csolo
        // con spazi vuoti)
        if (br.hasErrors()) {
            model.addAttribute("statusValues", StatusStudente.values());
            model.addAttribute("mode", "create");
            return "studenti/form-dto";// view -> pagina HTML
        }

        // errori di unicità(mail e matricola)
        // se ci sono errori rimando alla pagina della form
        Map<String, String> errori = studenteService.uniqueErrorsForCreate(dto);
        errori.forEach((field, msg) -> br.rejectValue(field, "duplicate", msg));

        if (br.hasErrors()) {
            model.addAttribute("statusValues", StatusStudente.values());
            model.addAttribute("mode", "create");
            return "studenti/form-dto";// view -> pagina HTML
        }

        boolean ok = studenteService.createFromDto(dto);
        // oggetto che permette di salvare valori temporanei tra la post e la get con il
        // redirect
        ra.addFlashAttribute(ok ? "success" : "error",
                ok ? "Studente creato" : "Errore nella creazione dello studente");
        // se è tutto ok allora salvo lo studente nel db
        // faccio un redirect alla lista degli studenti
        return "redirect:/studenti"; // POST-salvataggio-> redirect -> GET:/studenti
    }

    // <!-- "@{/studenti/{id}/edit(id=${s.id})}"
    // /studenti/1/edit--> diverso da /studenti?id=1
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, // qui salvo l'id che arriva tramite url
            Model model,
            RedirectAttributes ra) {
        StudenteFormDTO dto = studenteService.getDtoById(id);// cerco i dati dello studente con quell'id
        // e li savo nell'oggetto dto
        if (dto == null) {
            ra.addFlashAttribute("error", "Studente non trovato");
            return "redirect:/studenti";
        }

        model.addAttribute("studenteDto", dto); // metto nella form i dati dello studente in modo da verderli
        // prima di aggiornarli
        model.addAttribute("mode", "edit");// assegno a mode il valore edit nella form
        model.addAttribute("statusValues", StatusStudente.values()); // mettonella form i valori possibili dello status
        return "studenti/form-dto";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, // questo è il valore dell'id dello studente
            // che viene passato dal front end direttamente nell'URL della richiesta
            // corrisponde all'id dello studente che devo aggiornare
            // cioè all'id dello studente che il ciclo for ha iterato quando ho creato la
            // tabella
            @Valid // -> controlla le regole
            @ModelAttribute("studenteDto") StudenteFormDTO dto, // -> crea e riempie il DTO
            BindingResult br, // -> raccoglie gli errori
            // prima di BindingResult ci deve essere il Valid, non il contrario
            // non può raccoglire gli errori se non ci sono stati controlli sulle regole
            Model model,
            RedirectAttributes ra) {

        dto.setId(id);// per essere sicuri di aggiornare lo studente corretto
        // setto l'id del dto con quello che arriva dall'URL

        // controllo sull'univocità
        Map<String, String> erroriUnicita = studenteService.uniqueErrorsForUpdate(id, dto);
        // per ogni coppia della mappa
        // salvo in br, l'oggetto che cattura gli errori
        // il campo in cui c'è l'errore come "chiave" e il messaggio che ho inserito
        // nell validazione del DTO
        // come messaggia associato a quel campo
        // così nella form se ho inserito valori non validi verranno stampati i messaggi
        // di errore
        erroriUnicita.forEach((field, msg) -> br.rejectValue(field, "duplicate", msg));

        if (br.hasErrors()) {
            model.addAttribute("statusValues", StatusStudente.values());
            model.addAttribute("mode", "edit");
            return "studenti/form-dto";
        }

        boolean ok = studenteService.updateFromDto(id, dto);
        ra.addFlashAttribute(ok ? "success" : "error", ok ? "Studente aggiornato" : "errore aggiornamento studente");
        return "redirect:/studenti";
    }

    @PostMapping("/{id}/delete") // /studenti/2/delete
    // salvo il 2 nella variabile Long id
    public String delete(@PathVariable Long id, RedirectAttributes ra) {

        boolean ok = studenteService.delete(id);
        ra.addFlashAttribute(ok ? "success" : "error",
                ok ? "Studente disattivato" : "Impossibile disattivatre lo studente");
        return "redirect:/studenti";
    }

    // metodo per iscrivere uno studente a un corso
    @PostMapping("/{id}/iscrizioni")
    public String iscrivi(@PathVariable Long id, // parametro nell'URL
            @RequestParam("corsoId") Long corsoId, // valore preso dalla form
            RedirectAttributes ra) {

        boolean ok = iscrizioneService.iscrivi(id, corsoId);
        ra.addFlashAttribute(ok ? "success" : "error",
                ok ? "Iscrizione creata" : "Iscrizione non possibile(duplicata o dati mancanti)");
        return "redirect:/studenti";
    }

    // metodo per rimuovere l'iscrizione di uno studente a un corso
    @PostMapping("/{id}/iscrizioni/{iscrizioneId}/delete")
    public String rimuoviIscrizione(@PathVariable Long id,
            @PathVariable Long iscrizioneId,
            RedirectAttributes ra) {

        boolean ok = iscrizioneService.rimuoviIscrizione(iscrizioneId);
        ra.addFlashAttribute(ok ? "success" : "error",
                ok ? "Iscrizione rimossa" : "Rimozione iscrizione non riuscita");
        return "redirect:/studenti/" + id;
    }

    @PostMapping("/{id}/cambio-password")
    public String cambioPassword(@PathVariable Long id,
            @RequestParam String vecchiaPassword,
            @RequestParam String nuovaPassword,
            @RequestParam String confermaPassword,
            Authentication authentication,
            RedirectAttributes ra) {

        UserAccount user = (UserAccount) authentication.getPrincipal();
        // Controllo, lo studente può cambiare solo la propria password
        if (user.getPersona().getId() != id) {
            ra.addFlashAttribute("error", "Azione non autorizzata.");
            return "redirect:/studenti/" + id;
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            ra.addFlashAttribute("error", "Le nuove password non coincidono.");
            return "redirect:/studenti/" + id;
        }

        boolean ok = studenteService.cambiaPassword(id, vecchiaPassword, nuovaPassword);

        if (ok) {
            ra.addFlashAttribute("success", "Password aggiornata con successo.");
        } else {
            ra.addFlashAttribute("error", "Errore: la vecchia password è errata.");
        }

        return "redirect:/studenti/" + id;
    }
}

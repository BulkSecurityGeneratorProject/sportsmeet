package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.repository.EventoRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Evento.
 */
@RestController
@RequestMapping("/api")
public class EventoResource {

    private final Logger log = LoggerFactory.getLogger(EventoResource.class);
        
    @Inject
    private EventoRepository eventoRepository;
    
    /**
     * POST  /eventos : Create a new evento.
     *
     * @param evento the evento to create
     * @return the ResponseEntity with status 201 (Created) and with body the new evento, or with status 400 (Bad Request) if the evento has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/eventos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Evento> createEvento(@RequestBody Evento evento) throws URISyntaxException {
        log.debug("REST request to save Evento : {}", evento);
        if (evento.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("evento", "idexists", "A new evento cannot already have an ID")).body(null);
        }
        Evento result = eventoRepository.save(evento);
        return ResponseEntity.created(new URI("/api/eventos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("evento", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /eventos : Updates an existing evento.
     *
     * @param evento the evento to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated evento,
     * or with status 400 (Bad Request) if the evento is not valid,
     * or with status 500 (Internal Server Error) if the evento couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/eventos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Evento> updateEvento(@RequestBody Evento evento) throws URISyntaxException {
        log.debug("REST request to update Evento : {}", evento);
        if (evento.getId() == null) {
            return createEvento(evento);
        }
        Evento result = eventoRepository.save(evento);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("evento", evento.getId().toString()))
            .body(result);
    }

    /**
     * GET  /eventos : get all the eventos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of eventos in body
     */
    @RequestMapping(value = "/eventos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Evento> getAllEventos() {
        log.debug("REST request to get all Eventos");
        List<Evento> eventos = eventoRepository.findAll();
        return eventos;
    }

    /**
     * GET  /eventos/:id : get the "id" evento.
     *
     * @param id the id of the evento to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the evento, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/eventos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Evento> getEvento(@PathVariable Long id) {
        log.debug("REST request to get Evento : {}", id);
        Evento evento = eventoRepository.findOne(id);
        return Optional.ofNullable(evento)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /eventos/:id : delete the "id" evento.
     *
     * @param id the id of the evento to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/eventos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id) {
        log.debug("REST request to delete Evento : {}", id);
        eventoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("evento", id.toString())).build();
    }

}

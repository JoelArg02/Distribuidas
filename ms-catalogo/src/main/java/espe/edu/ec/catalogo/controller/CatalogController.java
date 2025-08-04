package espe.edu.ec.catalogo.controller;

import espe.edu.ec.catalogo.entity.Catalog;
import espe.edu.ec.catalogo.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService service;

    @GetMapping
    public List<Catalog> listarTodas() {
        return service.getAll();
    }
}

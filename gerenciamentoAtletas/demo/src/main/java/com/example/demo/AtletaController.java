package com.example.demo;

import com.example.demo.model.Atleta;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/atletas")
public class AtletaController {

    private static final String REDIRECT_LISTA = "redirect:/atletas";

    @Autowired
    private AtletaService atletaService;

    @GetMapping
    public String listar(@RequestParam(name = "nome", required = false) String nome, Model model, HttpSession session) {
        String filtroAtual = (String) session.getAttribute(Sessao.SEARCH_FILTER_KEY);
        List<Atleta> atletas;
        String nomeBuscado;
        boolean isSearching = false;

        if (nome != null && !nome.trim().isEmpty()) {
            session.setAttribute(Sessao.SEARCH_FILTER_KEY, nome.trim());
            atletas = atletaService.buscarPorNome(nome.trim());
            nomeBuscado = nome.trim();
            isSearching = true;
        } else if (nome != null && nome.trim().isEmpty()) {
            session.removeAttribute(Sessao.SEARCH_FILTER_KEY);
            atletas = atletaService.listar();
            nomeBuscado = "";
        } else if (filtroAtual != null && !filtroAtual.isEmpty()) {
            atletas = atletaService.buscarPorNome(filtroAtual);
            nomeBuscado = filtroAtual;
            isSearching = true;
        } else {
            atletas = atletaService.listar();
            nomeBuscado = "";
        }
        if (isSearching && atletas.isEmpty()) {
            model.addAttribute("alerta", "Nenhum atleta encontrado com o nome: " + nomeBuscado);
        }

        model.addAttribute("atletas", atletas);
        model.addAttribute("nomeBuscado", nomeBuscado);
        return "lista";
    }

    @GetMapping("/limparFiltro") //
    public String limparFiltro(HttpSession session) {
        session.removeAttribute(Sessao.SEARCH_FILTER_KEY); //
        return REDIRECT_LISTA;
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("atleta", new Atleta());
        return "form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("atleta") Atleta atleta, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("atleta", atleta);
            return "form";
        }
        atletaService.salvar(atleta);
        redirectAttributes.addFlashAttribute("sucesso", "Atleta cadastrado com sucesso!");
        return REDIRECT_LISTA;
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Atleta> atleta = atletaService.buscar(id);
        if (atleta.isEmpty()) {
            redirectAttributes.addFlashAttribute("Erro", "Atleta não encontrado!");
            return "redirect:/atletas";
        }
        model.addAttribute("atleta", atleta.get());
        return "form";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("atleta") Atleta atleta, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "form";
        }
        atletaService.atualizar(id, atleta);
        redirectAttributes.addFlashAttribute("sucesso", "Atleta atualizado com sucesso!");
        return REDIRECT_LISTA;
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean removido = atletaService.remover(id);
        if (!removido) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível remover. Atleta não encontrado!");
        } else {
            redirectAttributes.addFlashAttribute("sucesso", "Atleta removido com sucesso!");
        }
        return REDIRECT_LISTA;
    }
}
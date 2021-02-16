package com.ewch.reactive.client.reactivespringclient.controller;

import com.ewch.reactive.client.reactivespringclient.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get().uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in client using retrieve.");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {
        return webClient.get().uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in client using exchange.");
    }

    @GetMapping("/client/exchange/toflux")
    public Flux<Item> getAllItemsUsingExchangeToFlux() {
        return webClient.get().uri("/v1/items")
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in client using exchange to flux.");
    }

    @GetMapping("/client/retrieve/singleitem")
    public Mono<Item> getOneItemByIdUsingRetrieve() {
        String id = "abc";
        return webClient.get().uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Item by id in client using retrieve.");
    }

    @GetMapping("/client/exchange/singleitem")
    public Mono<Item> getOneItemByIdUsingExchange() {
        String id = "abc";
        return webClient.get().uri("/v1/items/{id}", id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Item by id in client using exchange.");
    }

    @GetMapping("/client/exchange/singleitem/tomono")
    public Mono<Item> getOneItemByIdUsingExchangeToMono() {
        String id = "abc";
        return webClient.get().uri("/v1/items/{id}", id)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Item by id in client using exchange to mono.");
    }

    @PostMapping("/client/createItem")
    public Mono<Item> createItemUsingRetrieve(@RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created Item using retrieve is: ");
    }

    @PostMapping("/client/createItem/exchange")
    public Mono<Item> createItemUsingExchange(@RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Created Item using exchange is: ");
    }

    @PostMapping("/client/createItem/tomono")
    public Mono<Item> createItemUsingExchangeToMono(@RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Created Item using exchange to mono is: ");
    }

    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> updateItemUsingRetrieve(@PathVariable String id, @RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.put().uri("/v1/items/{id}", id)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Updated Item using retrieve is: ");
    }

    @PutMapping("/client/updateItem/{id}/exchange")
    public Mono<Item> updateItemUsingExchange(@PathVariable String id, @RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.put().uri("/v1/items/{id}", id)
                .body(itemMono, Item.class)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Updated Item using exchange is: ");
    }

    @PutMapping("/client/updateItem/{id}/exchange/tomono")
    public Mono<Item> updateItemUsingExchangeToMono(@PathVariable String id, @RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.put().uri("/v1/items/{id}", id)
                .body(itemMono, Item.class)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Updated Item using exchange to mono is: ");
    }

    @DeleteMapping("/client/deleteItem/{id}")
    public Mono<Void> deleteItemUsingRetrieve(@PathVariable String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("Deleted Item using retrieve is: ");
    }

    @DeleteMapping("/client/deleteItem/{id}/exchange")
    public Mono<Void> deleteItemUsingExchange(@PathVariable String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Void.class))
                .log("Deleted Item using retrieve is: ");
    }

    @DeleteMapping("/client/deleteItem/{id}/exchange/tomono")
    public Mono<Void> deleteItemUsingExchangeToMono(@PathVariable String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Void.class))
                .log("Deleted Item using retrieve is: ");
    }

    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve() {
        return webClient.get().uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    clientResponse.bodyToMono(String.class);
                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono
                            .flatMap(errorMessage -> {
                                log.error("The error message in retrieve is: " + errorMessage);
                                throw new RuntimeException(errorMessage);
                            });
                })
                .bodyToFlux(Item.class);
    }

    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange() {
        return webClient.get().uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany((clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("The error message in exchange is: " + errorMessage);
                                    return Mono.error(new RuntimeException(errorMessage));
                                });
                    } else {
                        return clientResponse.bodyToFlux(Item.class);
                    }
                }));
    }
}

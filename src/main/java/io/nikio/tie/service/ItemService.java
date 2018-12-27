package io.nikio.tie.service;

import io.nikio.tie.domain.Item;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Item.
 */
public interface ItemService {

    /**
     * Save a item.
     *
     * @param item the entity to save
     * @return the persisted entity
     */
    Item save(Item item);

    /**
     * Get all the items.
     *
     * @return the list of entities
     */
    List<Item> findAll();


    /**
     * Get the "id" item.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Item> findOne(Long id);

    /**
     * Delete the "id" item.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the item corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<Item> search(String query);
}

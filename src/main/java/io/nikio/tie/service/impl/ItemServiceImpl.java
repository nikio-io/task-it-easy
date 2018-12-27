package io.nikio.tie.service.impl;

import io.nikio.tie.service.ItemService;
import io.nikio.tie.domain.Item;
import io.nikio.tie.repository.ItemRepository;
import io.nikio.tie.repository.search.ItemSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Item.
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;

    private final ItemSearchRepository itemSearchRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemSearchRepository itemSearchRepository) {
        this.itemRepository = itemRepository;
        this.itemSearchRepository = itemSearchRepository;
    }

    /**
     * Save a item.
     *
     * @param item the entity to save
     * @return the persisted entity
     */
    @Override
    public Item save(Item item) {
        log.debug("Request to save Item : {}", item);
        Item result = itemRepository.save(item);
        itemSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the items.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Item> findAll() {
        log.debug("Request to get all Items");
        return itemRepository.findAll();
    }


    /**
     * Get one item by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findOne(Long id) {
        log.debug("Request to get Item : {}", id);
        return itemRepository.findById(id);
    }

    /**
     * Delete the item by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Item : {}", id);
        itemRepository.deleteById(id);
        itemSearchRepository.deleteById(id);
    }

    /**
     * Search for the item corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Item> search(String query) {
        log.debug("Request to search Items for query {}", query);
        return StreamSupport
            .stream(itemSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

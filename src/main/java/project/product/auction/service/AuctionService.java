package project.product.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import project.product.auction.dto.BidDto;
import project.product.auction.model.Bid;
import project.product.auction.model.Customer;
import project.product.auction.model.Item;
import project.product.auction.repository.BidRepository;
import project.product.auction.repository.CustomerRepository;
import project.product.auction.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class AuctionService {

    @Autowired
    ItemRepository itemRepo;

    @Autowired
    BidRepository bidRepo;

    @Autowired
    CustomerRepository customerRepository;

//    String[] categories = {"möbler", "elektronik"};

    public Item registerItem(Item item) {
        return itemRepo.save(item);
    }

    public Iterable<Item> getAllItems() {
        return itemRepo.findAll();
    }

    // Get all expired items
    public Iterable<Item> getAllCurrentItemsExpiredTime() {
        return itemRepo.findByExpTimeLessThanEqual(LocalDateTime.now());
    }

    // Get all expired items by category
    public Iterable<Item> getCurrentItemsExpiredTimeByCategory(String category) {
//        if (Arrays.asList(categories).contains(category)) {
            return itemRepo.findByExpTimeLessThanEqualAndCategory(LocalDateTime.now(), category);
//        } else {
//            return null;
//        }
    }

    // Get all not expired items
    public Iterable<Item> getAllCurrentItemsNotExpiredTime() {
        return itemRepo.findByExpTimeGreaterThanEqual(LocalDateTime.now());
    }

    // Get all not expired items by category
    public Iterable<Item> getCurrentItemsNotExpiredTimeByCategory(String category) {
        return itemRepo.findByExpTimeGreaterThanEqualAndCategory(LocalDateTime.now(), category);
    }

    public Optional<Customer> getProfile(long id){
        return customerRepository.findById(id);
    }

    public Optional<Bid> getAuction(long itemId) {
        return bidRepo.findFirstByItemIdOrderByBidTimeDesc(itemId);
    }

    public ResponseEntity removeItem(long itemId) {
        if (itemRepo.findById(itemId).isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            itemRepo.deleteById(itemId);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    public ResponseEntity registerBid(BidDto bidDto) {
        Optional<Bid> lastBid = bidRepo.findFirstByItemIdOrderByBidCountDesc(bidDto.getItemId());
        int lastBidCount = lastBid.get().getBidCount() + 1;

        if (bidDto.getBid().compareTo(lastBid.get().getBid()) == 1) {
            bidRepo.save(new Bid(bidDto.getItemId(), bidDto.getCustomerID(), bidDto.getBid(), lastBidCount, LocalDateTime.now()));
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

    }

    //TODO: ItemId in Item @ManyToOne Bid - Low prio?
}

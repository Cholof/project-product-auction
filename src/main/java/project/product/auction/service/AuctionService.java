package project.product.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.product.auction.dto.AuctionDto;
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

    public Item registerItem(Item item) {
        return itemRepo.save(item);
    }

    public Customer registerCustomer(Customer customer) {
       return customerRepository.save(customer);
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
        return itemRepo.findByExpTimeLessThanEqualAndCategory(LocalDateTime.now(), category);
    }

    // Get all not expired items
    public Iterable<Item> getAllCurrentItemsNotExpiredTime() {
        return itemRepo.findByExpTimeGreaterThanEqual(LocalDateTime.now());
    }

    // Get all not expired items by category
    public Iterable<Item> getCurrentItemsNotExpiredTimeByCategory(String category) {
        return itemRepo.findByExpTimeGreaterThanEqualAndCategory(LocalDateTime.now(), category);
    }

    // Get customer's profile
    public Optional<Customer> getProfile(long id) {
        return customerRepository.findById(id);
    }

    // Get all possible data for and item. Usable when entering a item's page
    public AuctionDto getAuction(long itemId) {
        Optional<Item> item = itemRepo.findById(itemId);
        Optional<Bid> bid = bidRepo.findFirstByItemIdOrderByBidCountDesc(itemId);
        if (item.isEmpty()) {
            return null;
        } else {
            LocalDateTime expirationTime = item.get().getExpTime();
            if (bid.isEmpty()) {
                return AuctionDto.builder()
                        .name(item.get().getItemName())
                        .desc(item.get().getDescription())
                        .expTime(item.get().getExpTime())
                        .category(item.get().getCategory())
                        .imageUrl(item.get().getImageUrl())
                        .bid(item.get().getStartPrice())
                        .bidCount(0)
                        .hasExpired(expirationTime.isBefore(LocalDateTime.now()))
                        .build();
            } else {
                return AuctionDto.builder()
                        .name(item.get().getItemName())
                        .desc(item.get().getDescription())
                        .expTime(item.get().getExpTime())
                        .category(item.get().getCategory())
                        .imageUrl(item.get().getImageUrl())
                        .bid(bid.get().getBid())
                        .bidCount(bid.get().getBidCount())
                        .bidTime(bid.get().getBidTime())
                        .hasExpired(expirationTime.isBefore(LocalDateTime.now()))
                        .build();
            }
        }
    }

    // Get a highest bid for an auction
    public Optional<Bid> getHighestBid(long itemId) {
        return bidRepo.findFirstByItemIdOrderByBidTimeDesc(itemId);
    }

    // Delete item
    public Item removeItem(long itemId) {
        Optional<Item> deletedItem = itemRepo.findById(itemId);
        if (deletedItem.isEmpty()) {
            return null;
        } else {
            itemRepo.deleteById(itemId);
            return deletedItem.get();
        }
    }

    // Register a bid on an auction
    public Bid registerBid(BidDto bidDto, LocalDateTime bidTime) {
        Optional<Bid> lastBid = bidRepo.findFirstByItemIdOrderByBidCountDesc(bidDto.getItemId());
        if (lastBid.isEmpty()) {
            BigDecimal startPrice = itemRepo.findById(bidDto.getItemId()).get().getStartPrice();
            if (bidDto.getBid().compareTo(startPrice) == 1) {
                Bid newBid = new Bid(bidDto.getItemId(), bidDto.getCustomerID(), bidDto.getBid(), 1, bidTime);
                bidRepo.save(newBid);
                return newBid;
            } else {
                return null;
            }
        }
        int lastBidCount = lastBid.get().getBidCount() + 1;

        if (bidDto.getBid().compareTo(lastBid.get().getBid()) == 1) {
            Bid newBid = new Bid(bidDto.getItemId(), bidDto.getCustomerID(), bidDto.getBid(), lastBidCount, bidTime);
            bidRepo.save(newBid);
            return newBid;
        } else {
            return null;
        }
    }
}

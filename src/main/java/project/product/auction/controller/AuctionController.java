package project.product.auction.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.product.auction.model.Item;
import project.product.auction.service.AuctionService;

import java.util.List;


@RestController
@RequestMapping("/")
public class AuctionController {

    @Autowired
    private AuctionService itemService;

    @ApiOperation(value = "Get all items", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved all items"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @GetMapping("/items")
    public Iterable<Item> getItems(){
        return itemService.getAllItems();
    }

    @ApiOperation(value = "Get all current items", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved all items"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @GetMapping("/currentauctions")
    public Iterable<Item> getCurrentAcutions() {
        return itemService.getAllCurrentItems();
    }

}

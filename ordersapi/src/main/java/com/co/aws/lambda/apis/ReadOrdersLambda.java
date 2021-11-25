package com.co.aws.lambda.apis;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.co.aws.lambda.dto.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadOrdersLambda {
    public APIGatewayProxyResponseEvent getOrders(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        AmazonDynamoDB dynamodb = AmazonDynamoDBClientBuilder.defaultClient();

        System.out.println(System.getenv("ORDERS_TABLE"));
        ScanRequest request = new ScanRequest().withTableName(System.getenv("ORDERS_TABLE"));
        ScanResult scanResult= dynamodb.scan(request);

        List<Order> orders= scanResult.getItems().stream()
                .map(item -> new Order(
                        Integer.parseInt(item.get("id").getN()),
                        item.get("itemName").getS(),
                        Integer.parseInt(item.get("quantity").getN())))
                .collect(Collectors.toList());
        String jsonOutPut = objectMapper.writeValueAsString(orders);
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(jsonOutPut);
    }
}

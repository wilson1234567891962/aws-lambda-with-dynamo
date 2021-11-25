package com.co.aws.lambda.apis;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.co.aws.lambda.dto.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CreateOrderLambda {

    public APIGatewayProxyResponseEvent createOrder(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), Order.class);
        DynamoDB dynamodb =new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
        Table table = dynamodb.getTable(System.getenv("ORDERS_TABLE"));
        Item item = new Item().withPrimaryKey("id", order.id).
                withString("itemName", order.itemName).
                withInt("quantity", order.quantity);
        table.putItem(item);

        return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("order ID: "+ order.id);
    }
}

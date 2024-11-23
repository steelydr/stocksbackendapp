package stocks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/stockprices")
public class StockPrices {
    
    @Inject
    Template stockprices;

    private static final Map<String, String> AVAILABLE_STOCKS = new HashMap<>() {{
        put("NVDA:NASDAQ", "NVIDIA Corporation");
        put("AAPL:NASDAQ", "Apple Inc.");
        put("GOOGL:NASDAQ", "Alphabet Inc.");
        put("MSFT:NASDAQ", "Microsoft Corporation");
        put("AMZN:NASDAQ", "Amazon.com Inc.");
        put("META:NASDAQ", "Meta Platforms Inc.");
        put("TSLA:NASDAQ", "Tesla Inc.");
        put("AMD:NASDAQ", "Advanced Micro Devices Inc.");
    }};

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getStockPrices(@QueryParam("symbol") @DefaultValue("NVDA:NASDAQ") String symbol) {
        String stockPrice = "Unavailable";
        
        try {
            // Fetch stock data
            Document doc = Jsoup.connect("https://www.google.com/finance/quote/" + symbol + "?hl=en").get();
            Element priceElement = doc.selectFirst(".YMlKec.fxKbKc");
            if (priceElement != null) {
                stockPrice = priceElement.text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Render the template
        return stockprices
                .data("selectedStock", symbol)
                .data("stockPrice", stockPrice)
                .data("stocks", AVAILABLE_STOCKS)
                .render();
    }
}
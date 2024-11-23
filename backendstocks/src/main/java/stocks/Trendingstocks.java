package stocks;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/trendingstocks")
public class Trendingstocks {

    @Inject
    Template trendingstocks;

    private static final String TRENDING_URL = "https://finance.yahoo.com/trending-tickers";
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getTrendingStocks(@QueryParam("symbol") @DefaultValue("") String symbol) {
        Map<String, String> trendingStocks = fetchTrendingStocks();
        Map<String, String> stockPrices = new LinkedHashMap<>();
        
        // If no symbol is selected, use the first trending stock
        if (symbol.isEmpty() && !trendingStocks.isEmpty()) {
            symbol = trendingStocks.keySet().iterator().next();
        }
        
        // Get the price for the selected stock
        String selectedStockPrice = fetchStockPrice(symbol);
        
        // Get prices for all trending stocks
        for (String stockSymbol : trendingStocks.keySet()) {
            String price = fetchStockPrice(stockSymbol);
            stockPrices.put(stockSymbol, price);
        }

        return trendingstocks
                .data("selectedStock", symbol)
                .data("selectedStockPrice", selectedStockPrice)
                .data("stocks", trendingStocks)
                .data("stockPrices", stockPrices)
                .render();
    }

    private Map<String, String> fetchTrendingStocks() {
        Map<String, String> stocks = new LinkedHashMap<>();
        
        try {
            Document doc = Jsoup.connect(TRENDING_URL)
                              .userAgent("Mozilla/5.0")
                              .get();
            
            Elements stockRows = doc.select("table tbody tr");
            
            for (Element row : stockRows) {
                if (stocks.size() >= 50) break; // Limit to top 50
                
                try {
                    String symbol = row.select("td:nth-child(1)").text();
                    String name = row.select("td:nth-child(2)").text();
                    
                    if (!symbol.isEmpty() && !name.isEmpty()) {
                        // Add exchange suffix if not present
                        if (!symbol.contains(":")) {
                            // Check if it's listed on NASDAQ or NYSE
                            if (isNasdaqListed(symbol)) {
                                symbol += ":NASDAQ";
                            } else {
                                symbol += ":NYSE";
                            }
                        }
                        stocks.put(symbol, name);
                    }
                } catch (Exception e) {
                    continue; // Skip problematic entries
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return stocks;
    }

    private boolean isNasdaqListed(String symbol) {
        try {
            String url = "https://www.nasdaq.com/market-activity/stocks/" + symbol.toLowerCase();
            Document doc = Jsoup.connect(url)
                              .userAgent("Mozilla/5.0")
                              .get();
            return !doc.select(".error-page").hasText();
        } catch (IOException e) {
            return false;
        }
    }

    private String fetchStockPrice(String symbol) {
        try {
            Document doc = Jsoup.connect("https://www.google.com/finance/quote/" + symbol + "?hl=en")
                              .userAgent("Mozilla/5.0")
                              .get();
            Element priceElement = doc.selectFirst(".YMlKec.fxKbKc");
            return priceElement != null ? priceElement.text() : "Unavailable";
        } catch (IOException e) {
            e.printStackTrace();
            return "Unavailable";
        }
    }
}
package stocks;
import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import stocks.homepage.StockData;
import stocks.homepage.StockScraper;

@Path("/api/stocks")
public class StocksApi {

    private static final String BASE_URL = "https://finance.yahoo.com/markets/stocks/";

    @GET
    @Path("/most-active")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, StockData> getMostActive(@QueryParam("symbol") String symbol) {
        return fetchStocks(BASE_URL + "most-active/", symbol);
    }

    @GET
    @Path("/trending")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, StockData> getTrending(@QueryParam("symbol") String symbol) {
        return fetchStocks(BASE_URL + "trending/", symbol);
    }

    @GET
    @Path("/gainers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, StockData> getGainers(@QueryParam("symbol") String symbol) {
        return fetchStocks(BASE_URL + "gainers/", symbol);
    }

    @GET
    @Path("/losers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, StockData> getLosers(@QueryParam("symbol") String symbol) {
        return fetchStocks(BASE_URL + "losers/", symbol);
    }

    private Map<String, StockData> fetchStocks(String url, String filterSymbol) {
        return StockScraper.fetchStocks(url, filterSymbol);
    }
}

package stocks;
import java.time.LocalDate;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import stocks.historical.HistoricalStockData;
import stocks.historical.HistoricalStockScraper;

@Path("/api/stocks/historical")
public class HistoricalStocksApi {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HistoricalStockData> getHistoricalData(
        @QueryParam("symbol") String symbol,
        @QueryParam("startDate") String startDate,
        @QueryParam("endDate") String endDate
    ) {
        if (symbol == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Symbol, start date, and end date are required");
        }
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        return HistoricalStockScraper.fetchHistoricalData(symbol, start, end);
    }
}
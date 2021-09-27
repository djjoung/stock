package chargingstation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/stock")
 public class StockController {

    @Autowired
    StockRepository stockRepository;

    // Station 상품 조회
	@GetMapping("/list")
	public ResponseEntity<List<Stock>> getManagementLists() {
		List<Stock> stockList = stockRepository.findAll();
		return ResponseEntity.ok(stockList);
	}
 }
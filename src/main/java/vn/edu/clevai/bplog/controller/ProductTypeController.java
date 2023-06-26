package vn.edu.clevai.bplog.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.dto.bp.PtGgDfdlDTO;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.service.BpPTProductTypeService;

import java.util.List;

@RestController
@RequestMapping({"/product-types"})
@AllArgsConstructor
public class ProductTypeController {

	private final BpPTProductTypeService productTypeService;

	@GetMapping
	public ResponseEntity<List<BpPTProductType>> findAllByCode(@RequestParam(value = "codes", required = false) List<String> codes) {
		return ResponseEntity.ok(productTypeService.findAll());
	}

	@GetMapping("/{product-code}/gg-dfdl")
	public ResponseEntity<PtGgDfdlDTO> findGGDFDL(@PathVariable(value = "product-code") String pt,
												  @RequestParam(value = "capCode") String capCode,
												  @RequestParam(value = "register", defaultValue = "Register1-Setting") String register) {

		return ResponseEntity.ok(productTypeService.findGGDFDLByProductCode(capCode, pt, register));
	}

	@GetMapping("/find-by-pt")
	public ResponseEntity<PtGgDfdlDTO> findByPT(@RequestParam(value = "productCode") String pt,
												  @RequestParam(value = "register", defaultValue = "Register1-Setting") String register) {

		return ResponseEntity.ok(productTypeService.findGGByPT(pt, register));
	}
}

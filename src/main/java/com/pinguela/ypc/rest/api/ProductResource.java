package com.pinguela.ypc.rest.api;

import java.util.Locale;

import javax.validation.constraints.Min;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pinguela.yourpc.model.ProductCriteria;
import com.pinguela.yourpc.model.dto.ProductDTO;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.service.impl.ProductServiceImpl;
import com.pinguela.ypc.rest.api.util.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/product")
public class ProductResource {

	private ProductService productService;

	public ProductResource() {
		this.productService = new ProductServiceImpl();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") @Min(1) Long id) {
		return ResponseUtils.wrap(() -> productService.findById(id, null)); // TODO: Locale?
	}
	
	@GET
	@Path("/{locale}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByIdLocalized(@PathParam("locale") Locale locale, 
			@PathParam("id") @Min(1) Long id) {
		return ResponseUtils.wrap(() -> productService.findByIdLocalized(id, locale));
	}
	
	@POST // Justificado por el tamaño de la URL por las búsqueda complejas por Atribute
	@Path("/{locale}/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation()
	public Response findBy(
/*			@PathParam("locale") Locale locale,
			@QueryParam("name") String name, 
			@QueryParam("launchDateFrom") Date launchDateMin,
			@QueryParam("launchDateTo") Date launchDateMax,
			@QueryParam("stockMin") Integer stockMin,
			@QueryParam("stockMax") Integer stockMax,
			@QueryParam("priceMin") Double priceMin,
			@QueryParam("priceMax") Double priceMax,
			@QueryParam("categoryId") Short categoryId
			*/
			ProductCriteria criteria
			) {
		
		return ResponseUtils.wrap(() -> productService.findBy(criteria, locale, 0, 0));
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			responses = {
					@ApiResponse(responseCode = "200")})
	public Response create(ProductDTO dto) {
		return ResponseUtils.wrap(() -> {
			Long id = productService.create(dto);
			return productService.findById(id, null);
		});
	}
}

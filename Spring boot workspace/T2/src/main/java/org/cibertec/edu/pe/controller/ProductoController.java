package org.cibertec.edu.pe.controller;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;



import org.cibertec.edu.pe.model.Detalle;
import org.cibertec.edu.pe.model.Producto;
import org.cibertec.edu.pe.model.Venta;
import org.cibertec.edu.pe.repository.IDetalleRepository;
import org.cibertec.edu.pe.repository.IProductoRepository;
import org.cibertec.edu.pe.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"carrito", "total"})
public class ProductoController {
	@Autowired
	private IProductoRepository productoRepository;
	@Autowired
	private IVentaRepository ventaRepository;
	@Autowired
	private IDetalleRepository detalleRepository;
	
	@GetMapping("/index")
	public String listado(Model model) {
		List<Producto> lista = new ArrayList<>();
		lista = productoRepository.findAll();
		model.addAttribute("productos", lista);
		return "index";
	}
	

	@GetMapping("/agregar/{idProducto}")
	public String agregar(Model model, @PathVariable(name = "idProducto", required = true) int idProducto) {
		// Codigo para agregar un producto
	    Optional<Producto> optionalProducto = productoRepository.findById(idProducto);
	    // Verificar si el producto está presente
	    if (optionalProducto.isPresent()) {
	        Producto producto = optionalProducto.get();

	        // Obtener el carrito de la sesión
	        List<Detalle> carrito = (List<Detalle>) model.getAttribute("carrito");

	        // Si el carrito aún no ha sido inicializado, inicializarlo
	        if (carrito == null) {
	            carrito = new ArrayList<>();
	        }

	        // Verificar si el producto ya está en el carrito
	        boolean productoExistente = false;
	        for (Detalle detalle : carrito) {
	            if (detalle.getProducto().getIdProducto() == idProducto) {
	                // Si ya existe, aumentar la cantidad
	                detalle.setCantidad(detalle.getCantidad() + 1);
	                detalle.setSubtotal(detalle.getCantidad() * detalle.getProducto().getPrecio());
	                productoExistente = true;
	                break;
	            }
	        }

	        // Si no existe, agregar uno nuevo al carrito
	        if (!productoExistente) {
	            Detalle nuevoDetalle = new Detalle();
	            nuevoDetalle.setProducto(producto);
	            nuevoDetalle.setCantidad(1);
	            nuevoDetalle.setSubtotal(producto.getPrecio());
	            carrito.add(nuevoDetalle);
	        }

	        // Actualizar el modelo con el carrito modificado
	        model.addAttribute("carrito", carrito);
	        detalleRepository.saveAll(carrito);

	    }
		return "redirect:/index";
	}
	
	@GetMapping("/carrito")
	public String carrito() {
		return "carrito";
	}
	
	@GetMapping("/pagar")
	public String pagar(Model model) {
	    // Codigo para pagar
	    return "pagar";
	}

	@PostMapping("/actualizarCarrito")
	public String actualizarCarrito(Model model) {
	    // Codigo para actualizar el carrito
	    return "carrito";
	}
	
	// Inicializacion de variable de la sesion
	@ModelAttribute("carrito")
	public List<Detalle> getCarrito() {
		return new ArrayList<Detalle>();
	}
	
	@ModelAttribute("total")
	public double getTotal() {
		return 0.0;
	}
	
	@GetMapping("/eliminar/{idDetalle}")
	public String Eliminar(Model model, @PathVariable(name = "idDetalle", required = true) int idDetalle) {
        System.out.println("Detalle eliminado del carrito y base de datos. ID: " + idDetalle);
        detalleRepository.deleteById(idDetalle);
        
        List<Detalle> carritoActualizado = detalleRepository.findAll();
        model.addAttribute("carrito", carritoActualizado);
        
    	return "redirect:/carrito";


	}
}


//@GetMapping("/eliminar/{idDetalle}")
//public String pagar(Model model, @PathVariable(name = "idDetalle", required = true) int idDetalle) {
	//detalleRepository.deleteById(idDetalle);
	//return "redirect:/carrito";
//}

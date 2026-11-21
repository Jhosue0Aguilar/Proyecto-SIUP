import java.math.BigDecimal;

public class Estudiante extends Persona {
    /** Descuento de estudiante como fraccion (0.10 = 10 %). El UML no fija el valor: ajustalo aqui. */
    public static final BigDecimal DESCUENTO = new BigDecimal("0.00");

    private String codigo;
    private String carrera;

    public Estudiante(int id, String nombres, String numeroDoc, String codigo, String carrera) {
        super(id, nombres, numeroDoc);
        this.codigo = codigo;
        this.carrera = carrera;
    }

    @Override
    public BigDecimal getDescuento() {
        return DESCUENTO;
    }

    @Override
    public String getTipo() { 
        return TipoPersona.ESTUDIANTE.getEtiqueta(); 
    }

    @Override
    public String toString() { 
        return super.toString() + " | Codigo: " + codigo + " | Carrera: " + carrera; 
    }
}

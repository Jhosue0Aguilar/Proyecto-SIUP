import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Usuario {
    private static final int ITERACIONES = 65536;
    private static final int LARGO_MIN_CLAVE = 8;
    private static final String ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final String usuario;
    private String claveHash;        // "sal:hash" en Base64; la clave nunca se guarda en claro
    private boolean activo;
    private boolean claveTemporal;   // true = debe cambiar la clave en su primer ingreso
    private final Rol rol;

    public Usuario(String usuario, String clave, Rol rol) {
        this(usuario, clave, rol, false);
    }

    public Usuario(String usuario, String clave, Rol rol, boolean claveTemporal) {
        this.usuario = usuario;
        this.claveHash = hashear(clave, nuevaSal());
        this.rol = rol;
        this.activo = true;
        this.claveTemporal = claveTemporal;
    }

    public boolean iniciarSesion(String usuario, String clave) {
        if (!activo || usuario == null || clave == null) return false;
        return this.usuario.equals(usuario) && coincideClave(clave);
    }

    /** Cambia la clave. Lanza IllegalArgumentException si no cumple las reglas. */
    public void cambiarClave(String nueva) {
        if (nueva == null || nueva.length() < LARGO_MIN_CLAVE) {
            throw new IllegalArgumentException("La clave debe tener al menos " + LARGO_MIN_CLAVE + " caracteres.");
        }
        if (nueva.equals(usuario)) {
            throw new IllegalArgumentException("La clave no puede ser igual al usuario.");
        }
        if (coincideClave(nueva)) {
            throw new IllegalArgumentException("La nueva clave debe ser distinta de la actual.");
        }
        claveHash = hashear(nueva, nuevaSal());
        claveTemporal = false;
    }

    /** Clave aleatoria de 10 caracteres (sin caracteres ambiguos como 0/O o 1/l/I). */
    public static String generarClaveTemporal() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(ALFABETO.charAt(ALEATORIO.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }

    private boolean coincideClave(String clave) {
        String[] partes = claveHash.split(":");
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        byte[] calculado = Base64.getDecoder().decode(hashear(clave, sal).split(":")[1]);
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] nuevaSal() {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return sal;
    }

    private static String hashear(String clave, byte[] sal) {
        PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(sal) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la clave.", e);
        } finally {
            spec.clearPassword();
        }
    }

    public String getUsuario() {
        return usuario;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public boolean isClaveTemporal() {
        return claveTemporal;
    }
    public Rol getRol() {
        return rol;
    }
}

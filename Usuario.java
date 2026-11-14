public class Usuario {
    private String usuario;
    private String clave;
    private Boolean activo;
    private Rol rol;

    public Usuario(String usuario, String clave, Rol rol) {
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
        this.activo = true;
    }

    public boolean iniciarSesion(String usuario, String clave) {
        return activo && this.usuario.equals(usuario) && this.clave.equals(clave);
    }

    public String getUsuario() { return usuario; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Rol getRol() { return rol; }
}

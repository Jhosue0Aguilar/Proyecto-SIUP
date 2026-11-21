public enum TipoPersona {
    ESTUDIANTE("Estudiante", "Codigo:", "Carrera:"),
    DOCENTE("Docente", "Codigo:", "Departamento:"),
    EXTERNO("Externo", "Empresa:", "RUC:");

    private final String etiqueta;
    private final String etiquetaDato1;
    private final String etiquetaDato2;

    TipoPersona(String etiqueta, String etiquetaDato1, String etiquetaDato2) {
        this.etiqueta = etiqueta;
        this.etiquetaDato1 = etiquetaDato1;
        this.etiquetaDato2 = etiquetaDato2;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getEtiquetaDato1() { return etiquetaDato1; }
    public String getEtiquetaDato2() { return etiquetaDato2; }

    @Override
    public String toString() { return etiqueta; }
}

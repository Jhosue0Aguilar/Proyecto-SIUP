public final class Validador {
    private Validador() { }

    // DNI: exactamente 8 digitos. 
    public static boolean validarDocumento(String doc) {
        return doc != null && doc.matches("\\d{8}");
    }

    // RUC: exactamente 11 digitos. 
    public static boolean validarRuc(String ruc) {
        return ruc != null && ruc.matches("\\d{11}");
    }

    // Numero de operacion: al menos 4 caracteres. 
    public static boolean validarNumeroOp(String op) {
        return op != null && op.trim().length() >= 4;
    }
}

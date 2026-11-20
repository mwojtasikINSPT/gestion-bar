package gestionbar.utils;

public final class Validaciones {

    private Validaciones() {
    }

    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.trim().toUpperCase();
    }

    public static boolean esCodigoValido(String codigo) {
        return codigo != null
                && codigo.matches("[A-Z]{3}\\d{4}");
    }

    public static boolean esCodigoValido(String codigo, String prefijo) {
        if (!esCodigoValido(codigo) || prefijo == null) {
            return false;
        }

        return codigo.startsWith(normalizarTexto(prefijo))
                && normalizarTexto(prefijo).matches("[A-Z]{3}");
    }

    public static String validarTextoObligatorio(String texto, String campo) {
        String normalizado = normalizarTexto(texto);

        if (!esTextoValido(normalizado)) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede estar vacío"
            );
        }

        if (normalizado.contains(",")) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede contener comas"
            );
        }

        return normalizado;
    }

    public static String validarCodigo(String codigo, String prefijo) {
        String normalizado = normalizarTexto(codigo);

        if (!esCodigoValido(normalizado, prefijo)) {
            throw new IllegalArgumentException(
                    "Código inválido. Escriba un código como "
                    + normalizarTexto(prefijo) + "0001"
            );
        }

        return normalizado;
    }
}

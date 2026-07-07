package com.meucaixa.app.utils

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

/**
 * Funções utilitárias para formatar e interpretar valores monetários.
 *
 * Todo valor monetário no aplicativo é representado internamente como [Long]
 * em centavos, evitando os erros de arredondamento inerentes ao uso de
 * [Double] em cálculos financeiros.
 */
object CurrencyUtils {

    private val formatadorReal: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    /**
     * Formata um valor em centavos como moeda, ex.: 12050L -> "R$ 120,50".
     */
    fun formatarCentavos(valorCentavos: Long): String {
        return formatadorReal.format(valorCentavos / 100.0)
    }

    /**
     * Formata um valor em centavos com sinal explícito de mais/menos,
     * ex.: usado em extratos onde entradas e saídas aparecem lado a lado.
     */
    fun formatarComSinal(valorCentavos: Long): String {
        val sinal = if (valorCentavos < 0) "- " else "+ "
        return sinal + formatarCentavos(abs(valorCentavos))
    }

    /**
     * Converte uma string de dígitos puros (como a produzida por um teclado
     * numérico durante a digitação, ex.: "12050") em centavos, interpretando
     * os dois últimos dígitos como centavos. Útil para o campo de valor da
     * tela de cadastro de movimentação, no estilo "digite da direita para a esquerda"
     * usado por apps bancários.
     */
    fun parseDigitosParaCentavos(digitos: String): Long {
        val somenteNumeros = digitos.filter { it.isDigit() }
        if (somenteNumeros.isEmpty()) return 0L
        return somenteNumeros.toLongOrNull() ?: 0L
    }

    /**
     * Converte um texto decimal com vírgula (ex.: "120,50") para centavos.
     * Usado ao importar/restaurar dados de backups ou relatórios.
     */
    fun parseTextoDecimalParaCentavos(texto: String): Long {
        val normalizado = texto.trim().replace(".", "").replace(",", ".")
        val valor = normalizado.toDoubleOrNull() ?: return 0L
        return Math.round(valor * 100)
    }
}

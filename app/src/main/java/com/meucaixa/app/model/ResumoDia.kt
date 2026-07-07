package com.meucaixa.app.model

import java.time.LocalDate

/**
 * Resumo financeiro agregado de um único dia, usado para desenhar os
 * marcadores coloridos na tela de calendário sem precisar carregar a lista
 * completa de movimentações daquele dia.
 *
 * @property data o dia ao qual este resumo se refere.
 * @property totalEntradasCentavos soma de todas as entradas do dia, em centavos.
 * @property totalSaidasCentavos soma de todas as saídas do dia, em centavos.
 * @property quantidadeMovimentacoes número total de lançamentos no dia.
 */
data class ResumoDia(
    val data: LocalDate,
    val totalEntradasCentavos: Long = 0L,
    val totalSaidasCentavos: Long = 0L,
    val quantidadeMovimentacoes: Int = 0
) {
    /** Saldo do dia: entradas menos saídas, em centavos. */
    val saldoCentavos: Long
        get() = totalEntradasCentavos - totalSaidasCentavos

    /** Verdadeiro se o dia teve algum lançamento cadastrado. */
    val possuiMovimentacoes: Boolean
        get() = quantidadeMovimentacoes > 0

    /** Verdadeiro se o saldo do dia é positivo (lucro). */
    val ehLucro: Boolean
        get() = saldoCentavos > 0

    /** Verdadeiro se o saldo do dia é negativo (prejuízo). */
    val ehPrejuizo: Boolean
        get() = saldoCentavos < 0

    companion object {
        /**
         * Agrupa uma lista de movimentações de qualquer período em um mapa de
         * [LocalDate] para [ResumoDia], pronto para ser consultado pela tela de calendário.
         */
        fun agruparPorDia(movimentacoes: List<Movimentacao>): Map<LocalDate, ResumoDia> {
            return movimentacoes
                .groupBy { it.data }
                .mapValues { (data, itensDoDia) ->
                    ResumoDia(
                        data = data,
                        totalEntradasCentavos = itensDoDia
                            .filter { it.tipo == TipoMovimentacao.ENTRADA }
                            .sumOf { it.valorCentavos },
                        totalSaidasCentavos = itensDoDia
                            .filter { it.tipo == TipoMovimentacao.SAIDA }
                            .sumOf { it.valorCentavos },
                        quantidadeMovimentacoes = itensDoDia.size
                    )
                }
        }
    }
}

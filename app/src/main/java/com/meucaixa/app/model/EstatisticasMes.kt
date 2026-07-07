package com.meucaixa.app.model

import java.time.YearMonth

/**
 * Conjunto completo de estatísticas financeiras de um mês, usado para
 * alimentar a tela de Dashboard e os gráficos.
 */
data class EstatisticasMes(
    val mes: YearMonth,
    val saldoCentavos: Long = 0L,
    val totalRecebidoCentavos: Long = 0L,
    val totalGastoCentavos: Long = 0L,
    val quantidadeEntradas: Int = 0,
    val quantidadeSaidas: Int = 0,
    val maiorEntradaCentavos: Long = 0L,
    val maiorGastoCentavos: Long = 0L,
    val mediaDiariaCentavos: Long = 0L,
    val distribuicaoPorFormaPagamento: Map<FormaPagamento, Long> = emptyMap(),
    val evolucaoSaldoDiario: List<Pair<Int, Long>> = emptyList(),
    val ganhosPorDia: List<Pair<Int, Long>> = emptyList()
) {
    /** Lucro líquido do mês: total recebido menos total gasto, em centavos. */
    val lucroLiquidoCentavos: Long
        get() = totalRecebidoCentavos - totalGastoCentavos

    companion object {
        /**
         * Calcula todas as estatísticas de um mês a partir da lista de
         * movimentações já filtrada para aquele período.
         */
        fun calcular(mes: YearMonth, movimentacoes: List<Movimentacao>): EstatisticasMes {
            if (movimentacoes.isEmpty()) {
                return EstatisticasMes(mes = mes)
            }

            val entradas = movimentacoes.filter { it.tipo == TipoMovimentacao.ENTRADA }
            val saidas = movimentacoes.filter { it.tipo == TipoMovimentacao.SAIDA }

            val totalRecebido = entradas.sumOf { it.valorCentavos }
            val totalGasto = saidas.sumOf { it.valorCentavos }
            val diasComMovimentacao = movimentacoes.map { it.data }.distinct().size.coerceAtLeast(1)

            val distribuicao = saidas
                .groupBy { it.formaPagamento }
                .mapValues { (_, itens) -> itens.sumOf { it.valorCentavos } }

            val resumosPorDia = ResumoDia.agruparPorDia(movimentacoes)
                .toSortedMap()

            var saldoAcumulado = 0L
            val evolucao = resumosPorDia.map { (data, resumo) ->
                saldoAcumulado += resumo.saldoCentavos
                data.dayOfMonth to saldoAcumulado
            }

            val ganhosPorDia = resumosPorDia.map { (data, resumo) ->
                data.dayOfMonth to resumo.totalEntradasCentavos
            }

            return EstatisticasMes(
                mes = mes,
                saldoCentavos = totalRecebido - totalGasto,
                totalRecebidoCentavos = totalRecebido,
                totalGastoCentavos = totalGasto,
                quantidadeEntradas = entradas.size,
                quantidadeSaidas = saidas.size,
                maiorEntradaCentavos = entradas.maxOfOrNull { it.valorCentavos } ?: 0L,
                maiorGastoCentavos = saidas.maxOfOrNull { it.valorCentavos } ?: 0L,
                mediaDiariaCentavos = (totalRecebido - totalGasto) / diasComMovimentacao,
                distribuicaoPorFormaPagamento = distribuicao,
                evolucaoSaldoDiario = evolucao,
                ganhosPorDia = ganhosPorDia
            )
        }
    }
}

package com.meucaixa.app.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Funções utilitárias de data usadas principalmente pela grade do calendário
 * mensal e pelas telas de resumo/relatório.
 */
object DateUtils {

    private val localePtBr = Locale("pt", "BR")

    /**
     * Representa uma célula da grade do calendário: pode ser um dia do mês
     * exibido ([pertenceAoMesAtual] = true) ou um dia de preenchimento do mês
     * anterior/seguinte, mostrado esmaecido para completar as semanas.
     */
    data class DiaDoCalendario(
        val data: LocalDate,
        val pertenceAoMesAtual: Boolean
    )

    /**
     * Gera a lista completa de células para desenhar a grade do calendário de
     * um mês, sempre com semanas completas (múltiplos de 7 dias), começando
     * no domingo, incluindo dias esmaecidos do mês anterior/seguinte para
     * preencher a primeira e a última semana.
     */
    fun gerarGradeDoMes(mes: YearMonth): List<DiaDoCalendario> {
        val primeiroDia = mes.atDay(1)
        val ultimoDia = mes.atEndOfMonth()

        // DayOfWeek.value: segunda=1 ... domingo=7. Convertendo para offset a partir do domingo.
        val offsetInicio = primeiroDia.dayOfWeek.value % 7

        val dias = mutableListOf<DiaDoCalendario>()

        for (i in offsetInicio downTo 1) {
            dias.add(DiaDoCalendario(primeiroDia.minusDays(i.toLong()), pertenceAoMesAtual = false))
        }

        for (dia in 1..ultimoDia.dayOfMonth) {
            dias.add(DiaDoCalendario(mes.atDay(dia), pertenceAoMesAtual = true))
        }

        val diasRestantes = (7 - dias.size % 7) % 7
        for (i in 1..diasRestantes) {
            dias.add(DiaDoCalendario(ultimoDia.plusDays(i.toLong()), pertenceAoMesAtual = false))
        }

        return dias
    }

    /** Nome do mês em português, com a primeira letra maiúscula, ex.: "Julho de 2026". */
    fun nomeMesPorExtenso(mes: YearMonth): String {
        val nome = mes.month.getDisplayName(TextStyle.FULL, localePtBr)
            .replaceFirstChar { it.uppercase() }
        return "$nome de ${mes.year}"
    }

    /** Nome curto do mês em português, ex.: "Jul". */
    fun nomeMesAbreviado(mes: YearMonth): String {
        return mes.month.getDisplayName(TextStyle.SHORT, localePtBr)
            .replaceFirstChar { it.uppercase() }
    }

    /** Iniciais dos dias da semana em português, na ordem domingo -> sábado, ex.: ["D","S","T","Q","Q","S","S"]. */
    fun iniciaisDiasDaSemana(): List<String> {
        val domingo = DayOfWeek.SUNDAY
        return (0..6).map { offset ->
            val dia = DayOfWeek.of(((domingo.value - 1 + offset) % 7) + 1)
            dia.getDisplayName(TextStyle.NARROW, localePtBr).uppercase()
        }
    }

    /** Formata uma data como "dd/MM/yyyy". */
    fun formatarDataCurta(data: LocalDate): String {
        return "%02d/%02d/%04d".format(data.dayOfMonth, data.monthValue, data.year)
    }

    /** Formata uma data por extenso, ex.: "Segunda-feira, 7 de julho". */
    fun formatarDataPorExtenso(data: LocalDate): String {
        val diaSemana = data.dayOfWeek.getDisplayName(TextStyle.FULL, localePtBr)
            .replaceFirstChar { it.uppercase() }
        val mes = data.month.getDisplayName(TextStyle.FULL, localePtBr)
        return "$diaSemana, ${data.dayOfMonth} de $mes"
    }

    /** Retorna verdadeiro se a data informada é o dia de hoje. */
    fun ehHoje(data: LocalDate): Boolean = data == LocalDate.now()

    /**
     * Retorna o 5º dia útil do mês informado, considerando dia útil como
     * segunda a sexta-feira (não leva feriados em conta).
     */
    fun quintoDiaUtilDoMes(mes: YearMonth): LocalDate {
        var dia = mes.atDay(1)
        var contador = 0
        while (true) {
            if (dia.dayOfWeek != DayOfWeek.SATURDAY && dia.dayOfWeek != DayOfWeek.SUNDAY) {
                contador++
                if (contador == 5) return dia
            }
            dia = dia.plusDays(1)
        }
    }

    /** Retorna verdadeiro se a data informada é o 5º dia útil do respectivo mês. */
    fun ehQuintoDiaUtilDoMes(data: LocalDate): Boolean {
        return data == quintoDiaUtilDoMes(YearMonth.from(data))
    }
}

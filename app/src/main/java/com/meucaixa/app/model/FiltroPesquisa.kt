package com.meucaixa.app.model

/**
 * Período de agrupamento usado nos filtros da tela de pesquisa/relatórios.
 */
enum class FiltroPeriodo(val rotulo: String) {
    SEMANA("Semana"),
    MES("Mês"),
    ANO("Ano")
}

/**
 * Tipo de movimentação a exibir nos filtros: todas, só entradas ou só saídas.
 */
enum class FiltroTipo(val rotulo: String) {
    TODAS("Todas"),
    SOMENTE_ENTRADAS("Entradas"),
    SOMENTE_SAIDAS("Saídas")
}

/**
 * Conjunto de critérios usados para pesquisar e filtrar movimentações.
 *
 * @property termo texto livre pesquisado na descrição, valor ou forma de pagamento.
 * @property periodo período de agrupamento (semana, mês ou ano) usado como base do filtro.
 * @property tipo filtro por natureza da movimentação.
 * @property formaPagamento se não nula, restringe o resultado a uma única forma de pagamento.
 */
data class FiltroPesquisa(
    val termo: String = "",
    val periodo: FiltroPeriodo = FiltroPeriodo.MES,
    val tipo: FiltroTipo = FiltroTipo.TODAS,
    val formaPagamento: FormaPagamento? = null
) {
    /**
     * Aplica todos os critérios deste filtro sobre uma lista de movimentações
     * já recortada para o período desejado, retornando somente as que combinam
     * com o termo de pesquisa, o tipo e a forma de pagamento selecionados.
     */
    fun aplicar(movimentacoes: List<Movimentacao>): List<Movimentacao> {
        return movimentacoes.filter { movimentacao ->
            val combinaComTipo = when (tipo) {
                FiltroTipo.TODAS -> true
                FiltroTipo.SOMENTE_ENTRADAS -> movimentacao.tipo == TipoMovimentacao.ENTRADA
                FiltroTipo.SOMENTE_SAIDAS -> movimentacao.tipo == TipoMovimentacao.SAIDA
            }
            val combinaComFormaPagamento = formaPagamento == null || movimentacao.formaPagamento == formaPagamento
            val combinaComTermo = termo.isBlank() || combinaComTermoDeBusca(movimentacao, termo)

            combinaComTipo && combinaComFormaPagamento && combinaComTermo
        }
    }

    private fun combinaComTermoDeBusca(movimentacao: Movimentacao, termo: String): Boolean {
        val termoNormalizado = termo.trim().lowercase()
        val descricaoCombina = movimentacao.descricao.lowercase().contains(termoNormalizado)
        val formaPagamentoCombina = movimentacao.formaPagamento.rotulo.lowercase().contains(termoNormalizado)
        val valorTexto = "%.2f".format(movimentacao.valorCentavos / 100.0).replace(".", ",")
        val valorCombina = valorTexto.contains(termoNormalizado)
        val dataTexto = "%02d/%02d/%04d".format(
            movimentacao.data.dayOfMonth,
            movimentacao.data.monthValue,
            movimentacao.data.year
        )
        val dataCombina = dataTexto.contains(termoNormalizado)

        return descricaoCombina || formaPagamentoCombina || valorCombina || dataCombina
    }
}

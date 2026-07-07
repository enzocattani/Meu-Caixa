package com.meucaixa.app.model

/**
 * Representa a natureza de uma movimentação financeira: uma entrada (dinheiro
 * que entrou no caixa) ou uma saída (dinheiro que saiu do caixa).
 */
enum class TipoMovimentacao {
    ENTRADA,
    SAIDA;

    /** Retorna verdadeiro se esta movimentação representa dinheiro recebido. */
    fun isEntrada(): Boolean = this == ENTRADA

    /** Retorna verdadeiro se esta movimentação representa dinheiro gasto. */
    fun isSaida(): Boolean = this == SAIDA
}

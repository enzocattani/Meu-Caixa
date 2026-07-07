package com.meucaixa.app.model

/**
 * Formas de pagamento suportadas para uma movimentação.
 *
 * @param rotulo texto amigável exibido na interface.
 */
enum class FormaPagamento(val rotulo: String) {
    DINHEIRO("Dinheiro"),
    PIX("Pix"),
    CARTAO_DEBITO("Cartão Débito"),
    CARTAO_CREDITO("Cartão Crédito"),
    TRANSFERENCIA("Transferência"),
    BOLETO("Boleto"),
    OUTRO("Outro");

    companion object {
        /** Lista de formas de pagamento na ordem em que devem aparecer na interface. */
        fun listaOrdenada(): List<FormaPagamento> = entries.toList()
    }
}
